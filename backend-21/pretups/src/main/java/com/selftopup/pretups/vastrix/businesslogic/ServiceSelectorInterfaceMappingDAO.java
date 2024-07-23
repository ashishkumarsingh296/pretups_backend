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
package com.selftopup.pretups.vastrix.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.IDGenerator;
import com.selftopup.common.ListValueVO;
import com.selftopup.common.TypesI;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

public class ServiceSelectorInterfaceMappingDAO {

    private Log _log = LogFactory.getLog(ServiceSelectorInterfaceMappingDAO.class.getName());

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
        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypes", "Entered:");
        ArrayList serviceList = new ArrayList();
        
  
        ListValueVO listValueVO = null;
        StringBuffer srvcList = new StringBuffer();
        try {
            if (!p_srvcType.contains(","))
                p_srvcType = p_srvcType + ",";
            String sevcType[] = p_srvcType.split(",");
            for (int i = 0, j = sevcType.length; i < j; i++) {
                srvcList.append("'");
                srvcList.append(sevcType[i]);
                srvcList.append("',");
            }
            String srvcTypeList = srvcList.substring(0, (srvcList.length() - 1));
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT service_type,name ");
            selectQuery.append(" FROM service_type ");
            selectQuery.append(" WHERE external_interface='Y' and status <> 'N' and service_type IN (" + srvcTypeList + ") order by UPPER(name)");
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypes", "Query=" + selectQuery);
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                serviceList.add(listValueVO);
            }
            }
        } catch (SQLException sqe) {
            _log.error("loadServiceTypes", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceTypes]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypes", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadServiceTypes", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceTypes]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypes", "error.general.sql.processing");
        } finally {
            
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypes", "Exiting:serviceList size=" + serviceList.size());
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
        if (_log.isDebugEnabled())
            _log.debug("loadServiceProductList", "Entered:");
        ArrayList productList = new ArrayList();
        
        ListValueVO listValueVO = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT selector_code,selector_name ");
            selectQuery.append(" FROM service_type_selector_mapping ");
            selectQuery.append(" WHERE service_type=? and  status <> 'N'  order by UPPER(selector_name)");
            if (_log.isDebugEnabled())
                _log.debug("loadServiceProductList", "Query=" + selectQuery);
           try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
           {
            pstmtSelect.setString(1, p_serviceType);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("selector_name"), rs.getString("selector_code"));
                productList.add(listValueVO);
            }
           }
           }
        } catch (SQLException sqe) {
            _log.error("loadServiceProductList", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceProductList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceProductList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadServiceProductList", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceProductList]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceProductList", "error.general.sql.processing");
        } finally {
            
            if (_log.isDebugEnabled())
                _log.debug("productList", "Exiting:productList size=" + productList.size());
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
        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceList", "Entered:p_interfaceType:" + p_interfaceType);
        ArrayList interfaceList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInterfaceIdSelect = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        String interfaceTypeId = null;
        ListValueVO listValueVO = null;
        try {
            StringBuffer selectInterfaceIdQuery = new StringBuffer();
            StringBuffer selectQuery = new StringBuffer();

            selectInterfaceIdQuery.append("SELECT interface_type_id,interface_name ");
            selectInterfaceIdQuery.append(" FROM interface_types ");
            selectInterfaceIdQuery.append(" WHERE interface_category=? ");

            selectQuery.append("SELECT interface_id,interface_description ");
            selectQuery.append(" FROM interfaces ");
            selectQuery.append(" WHERE interface_type_id=? and status='Y' ");

            if (_log.isDebugEnabled())
                _log.debug("selectInterfaceIdQuery", "Query=" + selectInterfaceIdQuery);
            if (_log.isDebugEnabled())
                _log.debug("selectQuery", "Query=" + selectQuery);

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
            _log.error("loadInterfaceList", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadInterfaceList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadInterfaceList", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadInterfaceList]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceList", "error.general.sql.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceList", "Exiting:interfaceList size=" + interfaceList.size());
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
        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceForModify", "Entered:");
        ArrayList interfaceTypeList = new ArrayList();
        
        ListValueVO listValueVO = null;
        try {

            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append(" SELECT  distinct I.interface_id,I.interface_description ");
            selectQuery.append(" FROM interfaces I , svc_setor_intfc_mapping sim ");
            selectQuery.append(" WHERE sim.interface_id=I.interface_id  order by UPPER(interface_description)");
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceForModify", "Query=" + selectQuery);
           try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString()); ResultSet rs = pstmtSelect.executeQuery();)
           {
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("interface_description"), rs.getString("interface_id"));
                interfaceTypeList.add(listValueVO);

            }
           }
        } catch (SQLException sqe) {
            _log.error("loadInterfaceForModify", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadInterfaceForModify]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceForModify", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadServiceTypes", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadInterfaceForModify]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadInterfaceForModify", "error.general.sql.processing");
        } finally {
            
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceForModify", "Exiting:interfaceTypeList size=" + interfaceTypeList.size());
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
        
        int insertCount = 0;

        if (_log.isDebugEnabled()) {
            _log.debug("addPrdServiceInterfaceMapping", "Entered: p_voList List Size= " + p_voList.size());
        }
        try {

            if ((p_voList != null)) {
                StringBuffer insertQueryBuff = new StringBuffer();
                insertQueryBuff.append("INSERT INTO svc_setor_intfc_mapping (srv_selector_interface_id,service_type,selector_code,network_code, ");
                insertQueryBuff.append("interface_id,method_type,created_on,created_by,modified_on,modified_by,action,prefix_id ) ");
                insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                String insertQuery = insertQueryBuff.toString();
                if (_log.isDebugEnabled())
                    _log.debug("addPrdServiceInterfaceMapping", "Insert query:" + insertQuery);

               try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
               {
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
                    psmtInsert.setLong(12, serviceSelectorInterfaceMappingVO.getPrefixID());
                    insertCount = psmtInsert.executeUpdate();
                    psmtInsert.clearParameters();
                    // check the status of the update
                    if (insertCount <= 0) {
                        throw new BTSLBaseException(this, "addPrdServiceInterfaceMapping", "error.general.sql.processing");
                    }
                }

            }
        }
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error("addPrdServiceInterfaceMapping", "SQLException: " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[addPrdServiceInterfaceMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addPrdServiceInterfaceMapping", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error("addPrdServiceInterfaceMapping", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[addPrdServiceInterfaceMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addPrdServiceInterfaceMapping", "error.general.processing");
        } // end of catch
        finally {
            

            if (_log.isDebugEnabled()) {
                _log.debug("addPrdServiceInterfaceMapping", "Exiting: insertCount=" + insertCount);
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
        if (_log.isDebugEnabled())
            _log.debug("loadServiceInterfaceMappingRuleList()", "Entered:p_networkCode=" + p_networkCode + ",p_serviceType=" + p_serviceType);
        
        
        ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
        ArrayList serviceInterfaceRulesList = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT sim.srv_selector_interface_id, sim.service_type,sim.selector_code, sim.network_code,sim.interface_id ,");
        strBuff.append("sim.action,sim.method_type, np.series,sim.created_on,sim.created_by,sim.modified_on,sim.modified_by ");
        strBuff.append("FROM svc_setor_intfc_mapping sim ,network_prefixes np ");
        strBuff.append("WHERE sim.prefix_id=np.prefix_id AND sim.network_code=? AND sim.service_type=? ORDER BY sim.selector_code,sim.interface_id,sim.method_type,sim.action ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadServiceInterfaceMappingRuleList", "QUERY sqlSelect=" + sqlSelect);
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_serviceType);
            try(ResultSet rs = pstmt.executeQuery();)
            {
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
        }
        }catch (SQLException sqe) {
            _log.error("loadServiceInterfaceMappingRuleList()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceInterfaceMappingRuleList]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSrvInterfaceMappingRuleList()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServiceInterfaceMappingRuleList()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServiceInterfaceMappingRuleList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServiceInterfaceMappingRuleList()", "error.general.processing");
        } finally {
            
            if (_log.isDebugEnabled())
                _log.debug("loadServiceInterfaceMappingRuleList()", "Exiting: serviceInterfaceRulesList.size=" + serviceInterfaceRulesList.size());
        }
        return serviceInterfaceRulesList;
    }

    public int updatePrdServiceInterfaceMapping(Connection p_con, ArrayList p_serviceMappingList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updatePrdServiceInterfaceMapping", "Entered p_serviceMappingList:" + p_serviceMappingList.size());
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtDelete = null;
        int insertCount = 0;
        int deleteCount = 0;
        ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
        try {
            StringBuffer deleteQueryBuff = new StringBuffer();
            deleteQueryBuff.append("DELETE FROM  svc_setor_intfc_mapping ");
            deleteQueryBuff.append(" WHERE service_type = ? AND network_code=? AND selector_code=? AND interface_id=? ");
            if (_log.isDebugEnabled())
                _log.debug("updatePrdServiceInterfaceMapping", "deleteQueryBuff query:" + deleteQueryBuff);

            StringBuffer insertQueryBuff = new StringBuffer();
            insertQueryBuff.append("INSERT INTO svc_setor_intfc_mapping (srv_selector_interface_id,service_type,selector_code,network_code, ");
            insertQueryBuff.append("interface_id,method_type,created_on,created_by,modified_on,modified_by,action,prefix_id ) ");
            insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");

            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("addPrdServiceInterfaceMapping", "Insert query:" + insertQuery);

            pstmtDelete = p_con.prepareStatement(deleteQueryBuff.toString());

            pstmtInsert = p_con.prepareStatement(insertQuery.toString());
            // // This will delete the new existing VO LIST IN ANY
            for (int i = 0, j = p_serviceMappingList.size(); i < j; i++) {
                serviceSelectorInterfaceMappingVO = (ServiceSelectorInterfaceMappingVO) p_serviceMappingList.get(i);

                // /pstmtDelete.setLong(1,serviceSelectorInterfaceMappingVO.getPrefixID());
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
                pstmtInsert.setLong(12, serviceSelectorInterfaceMappingVO.getPrefixID());

                insertCount = pstmtInsert.executeUpdate();
                pstmtInsert.clearParameters();
                // check the status of the update
                if (insertCount <= 0) {
                    throw new BTSLBaseException(this, "updatePrdServiceInterfaceMapping", "error.general.sql.processing");
                }
            }

            if (insertCount < 0)
                throw new BTSLBaseException(this, "updatePrdServiceInterfaceMapping", "error.modify.true");
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (SQLException sqle) {
            _log.error("updatePrdServiceInterfaceMapping", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[updatePrdServiceInterfaceMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updatePrdServiceInterfaceMapping", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updatePrdServiceInterfaceMapping", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[updatePrdServiceInterfaceMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updatePrdServiceInterfaceMapping", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtDelete != null)
                    pstmtDelete.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updatePrdServiceInterfaceMapping", "Exiting insertCount=" + insertCount);
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
        if (_log.isDebugEnabled())
            _log.debug("deletePrdServiceInterfaceMapping", "Entered p_serviceMappingList:" + p_serviceMappingList.size());
        
        int[] deleteCount = { 0 };
        // /int i=1;
        ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
        try {
            StringBuffer deleteQueryBuff = new StringBuffer();
            deleteQueryBuff.append("DELETE FROM  svc_setor_intfc_mapping ");
            deleteQueryBuff.append(" WHERE prefix_id=? AND service_type = ? AND network_code=? AND selector_code=? AND interface_id=? ");
            if (_log.isDebugEnabled())
                _log.debug("deletePrdServiceInterfaceMapping", "deleteQueryBuff query:" + deleteQueryBuff);
           try(PreparedStatement pstmtDelete = p_con.prepareStatement(deleteQueryBuff.toString());)
           {
            for (int i = 0, j = p_serviceMappingList.size(); i < j; i++) {
                serviceSelectorInterfaceMappingVO = (ServiceSelectorInterfaceMappingVO) p_serviceMappingList.get(i);
                pstmtDelete.setLong(1, serviceSelectorInterfaceMappingVO.getPrefixID());
                pstmtDelete.setString(2, serviceSelectorInterfaceMappingVO.getServiceType());
                pstmtDelete.setString(3, serviceSelectorInterfaceMappingVO.getNetworkCode());
                pstmtDelete.setString(4, serviceSelectorInterfaceMappingVO.getSelectorCode());
                pstmtDelete.setString(5, serviceSelectorInterfaceMappingVO.getInterfaceID());

                pstmtDelete.addBatch();// adding the batch
            }
            deleteCount = pstmtDelete.executeBatch();// executing the batch
            if (deleteCount.length <= 0) {

                throw new BTSLBaseException(this, "deletePrdServiceInterfaceMapping", "error.general.sql.processing");
            }

        }
        }catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (SQLException sqle) {
            _log.error("deletePrdServiceInterfaceMapping", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[deletePrdServiceInterfaceMapping]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deletePrdServiceInterfaceMapping", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("deletePrdServiceInterfaceMapping", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[deletePrdServiceInterfaceMapping]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deletePrdServiceInterfaceMapping", "error.general.processing");
        }// end of catch
        finally {
            

            if (_log.isDebugEnabled())
                _log.debug("deletePrdServiceInterfaceMapping", "Exiting deleteCount=" + deleteCount.length);
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
    public HashMap loadServSelInterfMappingCache() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadServSelInterfMappingCache()", "Entered");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap servSelInterfMap = new HashMap();

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT  sst.service_type,sst.selector_code,sst.network_code,sst.interface_id,sst.prefix_id");
        strBuff.append(" ,sst.action,sst.method_type,sst.created_on,sst.created_by,sst.modified_on,sst.modified_by");
        strBuff.append(" ,sst.srv_selector_interface_id");
        strBuff.append(" ,i.external_id,i.status,i.message_language1,i.message_language2,i.status_type statustype,i.single_state_transaction");
        strBuff.append(" ,im.handler_class,im.underprocess_msg_reqd,sc.service_class_id,im.interface_type_id");
        strBuff.append(" FROM svc_setor_intfc_mapping sst,interfaces i,interface_types im,service_classes sc");
        strBuff.append(" WHERE sst.interface_id=i.interface_id ");
        strBuff.append(" AND i.interface_type_id = im.interface_type_id AND i.status<>'N'");
        strBuff.append(" AND I.interface_id=sc.interface_id(+) AND sc.service_class_code(+)=? AND sc.STATUS(+)<>'N'");
        String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled())
            _log.debug("loadServSelInterfMappingCache", "QUERY sqlSelect=" + sqlSelect);
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
                servSelInterfMap.put(serSelIntMappVO.getServiceType() + "_" + serSelIntMappVO.getSelectorCode() + "_" + serSelIntMappVO.getAction() + "_" + serSelIntMappVO.getNetworkCode() + "_" + serSelIntMappVO.getPrefixID(), serSelIntMappVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadServSelInterfMappingCache()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServSelInterfMappingCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServSelInterfMappingCache()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServSelInterfMappingCache()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorInterfaceMappingDAO[loadServSelInterfMappingCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServSelInterfMappingCache()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadServSelInterfMappingCache()", "Exiting: ServSelInterfMappingCache size=" + servSelInterfMap.size());
            }
        }
        return servSelInterfMap;
    }

}
