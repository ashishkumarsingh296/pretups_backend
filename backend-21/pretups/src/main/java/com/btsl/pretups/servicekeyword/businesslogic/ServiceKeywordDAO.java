/*
 * @# ServiceKeywordDAO.java
 * This class used in the SERVICE KEYWORD MODULE
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * June 10, 2005 sandeep.goel Initial creation
 * June 16, 2005 avinash.kamthan Added method loadServiceCache()
 * 05/12/2006 Gurjeet Bedi CR 000009 Sub Keyword Fields
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.servicekeyword.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.ServiceTypeobjVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

/**
 */
public class ServiceKeywordDAO {
    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Constructor for ServiceDAO.
     */
    public ServiceKeywordDAO() {
        super();
    }
    
    
    
    /**
     * Method loadServiceTypeListData. This method is used to load the list of the
     * service_type from the table.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public  List<ServiceTypeobjVO>  loadServiceTypeListData(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadServiceTypeListData";
       
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        List<ServiceTypeobjVO> serviceTypeList = new ArrayList();
    PreparedStatement pstmtSelect = null;
    ResultSet rs = null;
    try {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("select  ST.SERVICE_TYPE,ST.NAME,LKP.LOOKUP_NAME MODULE,ST.REQUEST_PARAM,sub_keyword_applicable from  service_type ST,LOOKUPS LKP  WHERE   LKP.lookup_code=ST.module AND LKP.lookup_type='MOTYP' AND ST.status <> 'N' order by ST.NAME");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query=" + selectQuery);
        }
        pstmtSelect = p_con.prepareStatement(selectQuery.toString());
        rs = pstmtSelect.executeQuery();
        
        ServiceTypeobjVO serviceTypeobjVO=null;
        while (rs.next()) {
        	serviceTypeobjVO= new ServiceTypeobjVO();
        	serviceTypeobjVO.setServiceType(rs.getString("SERVICE_TYPE"));
        	serviceTypeobjVO.setServiceTypeName(rs.getString("NAME"));
        	serviceTypeobjVO.setModule(rs.getString("MODULE"));
        	serviceTypeobjVO.setRequest_param(rs.getString("REQUEST_PARAM"));
        	serviceTypeobjVO.setSubKeyWordApplicable(PretupsI.NO);
        	 if(rs.getString("REQUEST_PARAM")!=null &&    PretupsI.YES.equalsIgnoreCase(rs.getString("REQUEST_PARAM").trim())) {
        		 serviceTypeobjVO.setSubKeyWordApplicable(PretupsI.YES);
        	 }
        	
        	serviceTypeList.add(serviceTypeobjVO);
        }
    } catch (SQLException sqe) {
        _log.error(methodName, "SQLException:" + sqe.getMessage());
        _log.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeListData]", "", "", "", "SQL Exception:" + sqe.getMessage());
        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR,sqe);
    } catch (Exception e) {
        _log.error(methodName, "Exception:" + e.getMessage());
        _log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeListData]", "", "", "", "Exception:" + e.getMessage());
        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR,e);
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
        }
        try {
            if (pstmtSelect != null) {
                pstmtSelect.close();
            }
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting:list size=" + serviceTypeList.size());
        }
    }
 
        
      return serviceTypeList;
    }
    
    
    
    /**
     * Method validateServiceType. This method is used to load the list of the
     * service_type from the table.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public  boolean  validateServiceType(Connection p_con,String serviceType) throws BTSLBaseException {
        final String methodName = "validateServiceType";
       
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        
    PreparedStatement pstmtSelect = null;
    ResultSet rs = null;
    boolean valid=false;
    try {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("select  ST.SERVICE_TYPE,ST.NAME MODULE from  service_type ST   WHERE ST.SERVICE_TYPE = ?  ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query=" + selectQuery);
        }
        pstmtSelect = p_con.prepareStatement(selectQuery.toString());
        pstmtSelect.setString(1, serviceType.trim());
        rs = pstmtSelect.executeQuery();
        
        
        while (rs.next()) {
        	valid=true;
        	break;
        }
    } catch (SQLException sqe) {
        _log.error(methodName, "SQLException:" + sqe.getMessage());
        _log.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[validateServiceType]", "", "", "", "SQL Exception:" + sqe.getMessage());
        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR,sqe);
    } catch (Exception e) {
        _log.error(methodName, "Exception:" + e.getMessage());
        _log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[validateServiceType]", "", "", "", "Exception:" + e.getMessage());
        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR,e);
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
        }
        try {
            if (pstmtSelect != null) {
                pstmtSelect.close();
            }
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
        }
        
    }
 
        
      return valid;
    }
    
    

    /**
     * Method checkServiceKeywordExists. This method is used to check the existence of service keyword.
     * 
     * @param p_con
     *            Connection
     * @return boolean
     * @throws BTSLBaseException
     */
    public  boolean  checkServiceKeywordExists(Connection p_con,String servicekeyword,String reqIntrType,String service_port,String serviceType ) throws BTSLBaseException {
        final String methodName = "checkServiceKeywordExists";
       
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        
    PreparedStatement pstmtSelect = null;
    ResultSet rs = null;
    boolean valid=false;
    try {
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("select  ST.SERVICE_TYPE,ST.NAME MODULE from  service_keywords ST   WHERE ST.keyword = ?   ");
        selectQuery.append(" and req_interface_type =?  and service_port=? and service_type =?  ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query=" + selectQuery);
        }
        pstmtSelect = p_con.prepareStatement(selectQuery.toString());
        pstmtSelect.setString(1, servicekeyword.trim());
        pstmtSelect.setString(2, reqIntrType.trim());
        pstmtSelect.setString(3, service_port.trim());
        pstmtSelect.setString(4, serviceType.trim());
        rs = pstmtSelect.executeQuery();
        
        
        while (rs.next()) {
        	valid=true;
        	break;
        }
    } catch (SQLException sqe) {
        _log.error(methodName, "SQLException:" + sqe.getMessage());
        _log.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[validateServiceType]", "", "", "", "SQL Exception:" + sqe.getMessage());
        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR,sqe);
    } catch (Exception e) {
        _log.error(methodName, "Exception:" + e.getMessage());
        _log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[validateServiceType]", "", "", "", "Exception:" + e.getMessage());
        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR,e);
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
        }
        try {
            if (pstmtSelect != null) {
                pstmtSelect.close();
            }
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
        }
        
    }
 
        
      return valid;
    }
    
    


    /**
     * Method loadServiceTypeList. This method is used to load the list of the
     * service_type from the table.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceTypeList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadServiceTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        ArrayList serviceTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT service_type,name,sub_keyword_applicable,request_param FROM service_type WHERE status='Y' ORDER BY name");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                listValueVO.setOtherInfo(rs.getString("sub_keyword_applicable"));
                listValueVO.setOtherInfo(rs.getString("request_param"));
                serviceTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:list size=" + serviceTypeList.size());
            }
        }
        return serviceTypeList;
    }
    
    public ArrayList loadServiceTypeListByModule(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadServiceTypeListByModule";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        ArrayList serviceTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT service_type,name,sub_keyword_applicable,request_param FROM service_type WHERE  module in ('C2S', 'P2P') and status='Y' ORDER BY name");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                listValueVO.setOtherInfo(rs.getString("sub_keyword_applicable"));
                listValueVO.setOtherInfo(rs.getString("request_param"));
                serviceTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO["+methodName+"]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,"ServiceKeywordDAO["+methodName+"]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:list size=" + serviceTypeList.size());
            }
        }
        return serviceTypeList;
    }


    /**
     * Method loadServiceTypeData. This method is used to load the full details
     * of the specified serviceType.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceTypeData(Connection p_con, String p_serviceType) throws BTSLBaseException {
        final String methodName = "loadServiceTypeData";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:serviceType=" + p_serviceType);
        }
        ArrayList serviceTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ServiceKeywordVO serviceKeywordVO = null;
        try {
        	StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT LKP.lookup_name module_name, SK.service_type,SK.keyword,SK.req_interface_type,SK.service_port, ");
            selectQuery.append("SK.name,SK.status,stsLookup.LOOKUP_NAME as statusDesc, menu,sub_menu,allowed_version,SK.modify_allowed,SK.modified_on,service_keyword_id, ");
            selectQuery.append("ST.sub_keyword_applicable,SK.sub_keyword,ST.REQUEST_PARAM, SK.REQUEST_PARAM GREQUEST_PARAM ");
            selectQuery.append("FROM service_keywords SK,service_type ST,lookups LKP ,LOOKUPS stsLookup ");
            selectQuery.append("WHERE SK.service_type=? ");
            selectQuery.append("AND ST.service_type=SK.service_type ");
            selectQuery.append("AND LKP.lookup_code=ST.module AND LKP.lookup_type=? AND SK.status <> ? ");
            selectQuery.append("AND stsLookup.LOOKUP_CODE =SK.status AND stsLookup.LOOKUP_TYPE ='GSTAT'  ");
            selectQuery.append("ORDER BY service_type ");
            String query = selectQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_serviceType);
            pstmtSelect.setString(2, PretupsI.MODULE_TYPE);
            pstmtSelect.setString(3, PretupsI.STATUS_DELETE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                serviceKeywordVO = new ServiceKeywordVO();
                serviceKeywordVO.setServiceType(rs.getString("service_type"));
                serviceKeywordVO.setKeyword(rs.getString("keyword"));
                serviceKeywordVO.setInterface(rs.getString("req_interface_type"));
                serviceKeywordVO.setReceivePort(rs.getString("service_port"));
                // serviceKeywordVO.setResponseCode(rs.getString("res_interface_code"));
                serviceKeywordVO.setName(rs.getString("name"));
                serviceKeywordVO.setStatus(rs.getString("statusDesc"));
                serviceKeywordVO.setMenu(rs.getString("menu"));
                serviceKeywordVO.setSubMenu(rs.getString("sub_menu"));
                serviceKeywordVO.setAllowedVersion(rs.getString("allowed_version"));
                serviceKeywordVO.setModifyAllowed(rs.getString("modify_allowed"));
                serviceKeywordVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                serviceKeywordVO.setServiceKeywordID(rs.getString("service_keyword_id"));
                serviceKeywordVO.setModuleDesc(rs.getString("module_name"));

                serviceKeywordVO.setSubKeyword(rs.getString("sub_keyword"));
                serviceKeywordVO.setServiceRequestParam(rs.getString("REQUEST_PARAM"));
                serviceKeywordVO.setGatewayRequestParam(rs.getString("GREQUEST_PARAM"));

                if (TypesI.YES.equals(rs.getString("sub_keyword_applicable"))) {
                    serviceKeywordVO.setSubKeywordApplicable(true);
                }

                serviceTypeList.add(serviceKeywordVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeData]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:list size=" + serviceTypeList.size());
            }
        }
        return serviceTypeList;
    }

    /**
     * Method addServiceType. This method is used to add a new record in the
     * table.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceKeywordVO
     *            ServiceKeywordVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addServiceType(Connection p_con, ServiceKeywordVO p_serviceKeywordVO) throws BTSLBaseException {
        final String methodName = "addServiceType";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_serviceKeywordVO=" + p_serviceKeywordVO);
        }
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
        	StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO service_keywords(service_type,keyword,req_interface_type,service_port,name,status,menu,sub_menu,");
            insertQuery.append("allowed_version,modify_allowed,created_by,created_on,modified_by,modified_on,service_keyword_id,sub_keyword,REQUEST_PARAM) ");
            insertQuery.append("VALUES(?,UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?,?,UPPER(?),?) ");
            String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            // commented for DB2 pstmtInsert = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(query);
            int i = 1;
            pstmtInsert.setString(i++, p_serviceKeywordVO.getServiceType());

            // for multilanguage support
            // commented for DB2 pstmtInsert.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(i++, p_serviceKeywordVO.getKeyword());

            pstmtInsert.setString(i++, p_serviceKeywordVO.getInterface());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getReceivePort());
            // pstmtInsert.setString(i++, p_serviceKeywordVO.getResponseCode());

            // for multilanguage support
            // commented for DB2 pstmtInsert.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(i++, p_serviceKeywordVO.getName());

            pstmtInsert.setString(i++, p_serviceKeywordVO.getStatus());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getMenu());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getSubMenu());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getAllowedVersion());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getModifyAllowed());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_serviceKeywordVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_serviceKeywordVO.getModifiedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_serviceKeywordVO.getModifiedOn()));
            pstmtInsert.setString(i++, p_serviceKeywordVO.getServiceKeywordID());

            // commented for DB2 pstmtInsert.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(i++, p_serviceKeywordVO.getSubKeyword());
            pstmtInsert.setString(i++, p_serviceKeywordVO.getGatewayRequestParam());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[addServiceType]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[addServiceType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:return=" + addCount);
            }
        }
        return addCount;
    }

    /**
     * Method updateServiceType.
     * This method is used to update the data in the table.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceKeywordVO
     *            ServiceKeywordVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateServiceType(Connection p_con, ServiceKeywordVO p_serviceKeywordVO) throws BTSLBaseException {
        final String methodName = "updateServiceType";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_serviceKeywordVO=" + p_serviceKeywordVO);
        }
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
        	StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("UPDATE  service_keywords SET keyword=UPPER(?),req_interface_type=?,service_port=?,name=?,status=?,");
            updateQuery.append("menu=?,sub_menu=?,allowed_version=?,modified_by=?,modified_on=?,modify_allowed=?,sub_keyword=UPPER(?),REQUEST_PARAM=? ");
            updateQuery.append("WHERE service_keyword_id=? ");
            String query = updateQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }

            // commented for DB2 pstmtUpdate = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(query);
            int i = 1;
            // for multilanguage support
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getKeyword());

            pstmtUpdate.setString(i++, p_serviceKeywordVO.getInterface());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getReceivePort());
            // pstmtUpdate.setString(4, p_serviceKeywordVO.getResponseCode());

            // for multilanguage support
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getName());

            pstmtUpdate.setString(i++, p_serviceKeywordVO.getStatus());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getMenu());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getSubMenu());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getAllowedVersion());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_serviceKeywordVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getModifyAllowed());
            // commented for DB2 pstmtUpdate.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getSubKeyword());
            pstmtUpdate.setString(i++, p_serviceKeywordVO.getGatewayRequestParam());

            pstmtUpdate.setString(i++, p_serviceKeywordVO.getServiceKeywordID());

            // for the checking is the record modified during the transaction.
            boolean modified = this.isRecordModified(p_con, p_serviceKeywordVO.getLastModifiedTime(), p_serviceKeywordVO.getServiceKeywordID());
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[updateServiceType]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[updateServiceType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:return=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method isRecordModified.
     * This method is used to check that is the record modified during the
     * processing.
     * 
     * @param p_con
     *            Connection
     * @param p_oldlastModified
     *            Long
     * @param p_key
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection p_con, long p_oldlastModified, String p_key) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_oldlastModified=" + p_oldlastModified + ",p_key=" + p_key);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        StringBuilder sqlRecordModified = new StringBuilder();
        sqlRecordModified.append("SELECT modified_on FROM service_keywords ");
        sqlRecordModified.append("WHERE service_keyword_id=? ");
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            String query = sqlRecordModified.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_key);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record is modified by other
            // person and the
            // modification is done on the value of the primary key.
            else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_oldlastModified) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[isRecordModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[isRecordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method isServiceKeywordExist.
     * This method is used to chech the existance of the servicekeyword in the
     * table
     * to maintain the uniqueness of the data record.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceKeywordVO
     *            ServiceKeywordVO
     * @param p_flag
     *            boolean
     * @return boolean
     * @throws BTSLBaseException
     *             CR 000009 Sub Keyword Fields Change
     */
    public boolean isServiceKeywordExist(Connection p_con, ServiceKeywordVO p_serviceKeywordVO, boolean p_flag) throws BTSLBaseException {
        final String methodName = "isServiceKeywordExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_serviceKeywordVO=" + p_serviceKeywordVO + ",p_flag=" + p_flag);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        StringBuilder sqlRecordModified = new StringBuilder();
        String query = null;
        try {
            sqlRecordModified.append("SELECT 1 FROM service_keywords ");
            sqlRecordModified.append("WHERE req_interface_type=? AND keyword=UPPER(?) ");
            if (p_serviceKeywordVO.isSubKeywordApplicable()) {
                sqlRecordModified.append("AND sub_keyword =UPPER(?) ");
                sqlRecordModified.append("AND service_port=?   ");
            } else {
                // sqlRecordModified.append("AND service_type=?  ");
                sqlRecordModified.append("AND service_port=?   ");
            }
            if (p_flag)// for the update record method
            {
                sqlRecordModified.append("AND service_keyword_id !=? ");
            }
            query = sqlRecordModified.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            pstmtSelect = p_con.prepareStatement(query);
            int i = 1;
            // pstmtSelect.setString(i++, p_serviceKeywordVO.getServiceType());
            pstmtSelect.setString(i++, p_serviceKeywordVO.getInterface());
            pstmtSelect.setString(i++, p_serviceKeywordVO.getKeyword());
            if (p_serviceKeywordVO.isSubKeywordApplicable()) {
                pstmtSelect.setString(i++, p_serviceKeywordVO.getSubKeyword());
                pstmtSelect.setString(i++, p_serviceKeywordVO.getReceivePort());
            } else {
                pstmtSelect.setString(i++, p_serviceKeywordVO.getReceivePort());
            }

            if (p_flag) {
                pstmtSelect.setString(i++, p_serviceKeywordVO.getServiceKeywordID());
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[isServiceKeywordExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[isServiceKeywordExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng:isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }// end isServiceKeywordExist

    /**
     * To load the service cache data
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap<String,ServiceKeywordCacheVO> loadServiceCache() throws BTSLBaseException {

        final String methodName = "loadServiceCache()";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap<String,ServiceKeywordCacheVO> serviceMap = new HashMap<String,ServiceKeywordCacheVO>();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT");
        strBuff.append(" SK.keyword, ST.module, SK.req_interface_type, ST.type, ST.message_format, ");
        strBuff.append(" SK.service_port, ST.service_type, ST.request_handler, ST.unregistered_access_allowed, ");
        strBuff.append(" ST.error_key, SK.allowed_version, SK.status, SK.modified_on as keywordmodified, ");
        strBuff.append(" ST.modified_on  as keywordtypemodified , ST.name name1 , ST.external_interface extintface, ST.use_interface_language,");
        strBuff.append(" ST.group_type,ST.sub_keyword_applicable,SK.sub_keyword,SK.REQUEST_PARAM,ST.RESPONSE_PARAM,ST.underprocess_check_reqd ");
        strBuff.append(" FROM  ");
        strBuff.append(" service_type ST , service_keywords SK ");
        strBuff.append(" WHERE  ");
        strBuff.append(" ST.service_type = SK.service_type AND SK.status <> 'N' AND ST.status <> 'N' ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            ServiceKeywordCacheVO cacheVO = null;
            ServiceKeywordCacheVO cacheVO2 = null;
            ArrayList subKeywordList = null;
            while (rs.next()) {
                cacheVO = new ServiceKeywordCacheVO();

                cacheVO.setKeyword(rs.getString("keyword"));
                cacheVO.setModule(rs.getString("module"));
                cacheVO.setRequestInterfaceType(rs.getString("req_interface_type"));
                cacheVO.setType(rs.getString("type"));
                String servicePort = rs.getString("service_port");
                cacheVO.setServiceType(rs.getString("service_type"));
                cacheVO.setRequestHandlerClass(rs.getString("request_handler"));
                cacheVO.setErrorKey(rs.getString("error_key"));
                cacheVO.setAllowedVersion(rs.getString("allowed_version"));
                cacheVO.setStatus(rs.getString("status"));
                cacheVO.setModifiedServiceType(rs.getDate("keywordmodified"));
                cacheVO.setModifiedOnServiceKeyword(rs.getDate("keywordtypemodified"));
                cacheVO.setModifiedServiceTypeTimestamp(rs.getTimestamp("keywordmodified"));
                cacheVO.setModifiedOnServiceKeywordTimestamp(rs.getTimestamp("keywordtypemodified"));
                cacheVO.setExternalInterface(rs.getString("extintface"));
                cacheVO.setUnregisteredAccessAllowed(rs.getString("unregistered_access_allowed"));
                cacheVO.setName(rs.getString("name1"));
                cacheVO.setMessageFormat(rs.getString("message_format"));
                cacheVO.setUseInterfaceLanguage(rs.getString("use_interface_language"));
                cacheVO.setGroupType(rs.getString("group_type"));

                cacheVO.setRequestParam(rs.getString("REQUEST_PARAM"));
                cacheVO.setResponseParam(rs.getString("RESPONSE_PARAM"));
                if (TypesI.YES.equals(rs.getString("underprocess_check_reqd"))) {
                    cacheVO.setServUderPrcsChkReqrd(true);
                }
                if (TypesI.YES.equals(rs.getString("sub_keyword_applicable"))) {
                    cacheVO.setSubKeywordApplicable(true);
                }
                cacheVO.setSubKeyword(rs.getString("sub_keyword"));
                String port[] = servicePort.split(",");

                for (int i = 0; i < port.length; i++) {
                    cacheVO2 = new ServiceKeywordCacheVO(cacheVO);
                    cacheVO2.setServerPort(port[i]);
                    String key = cacheVO2.getKeyword() + "_" + cacheVO2.getModule() + "_" + cacheVO2.getRequestInterfaceType() + "_" + cacheVO2.getServerPort();
                    if (serviceMap.containsKey(key)) {
                        subKeywordList = cacheVO2.getSubKeywordList();
                        if (subKeywordList == null) {
                            subKeywordList = new ArrayList();
                        }
                        subKeywordList.add(cacheVO2);
                        cacheVO2.setSubKeywordList(subKeywordList);
                        serviceMap.put(key, cacheVO2);
                    } else {
                        serviceMap.put(key, cacheVO2);
                    }
                }
                // String key = cacheVO.getKeyword() + "_" +
                // cacheVO.getModule()+ "_" + cacheVO.getRequestInterfaceType()
                // + "_"+ cacheVO.getServerPort();
                // serviceMap.put(key, cacheVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: Service Map size=" + serviceMap.size());
            }
        }
        return serviceMap;
    }

    /**
     * method loadServiceTypeListForNetworkServices
     * This method to load services having external interface
     * 
     * @param p_con
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public ArrayList loadServiceTypeListForNetworkServices(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadServiceTypeListForNetworkServices";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        ArrayList serviceTypeList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
        	StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT service_type, description,module ");
            selectQuery.append("FROM  service_type ");
            selectQuery.append("WHERE external_interface='Y' AND status ='Y' ");
            selectQuery.append("ORDER BY description ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            serviceTypeList = new ArrayList();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("description"), rs.getString("module") + ":" + rs.getString("service_type"));
                serviceTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeListForNetworkServices]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeListForNetworkServices]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:list size=" + serviceTypeList.size());
            }
        }
        return serviceTypeList;
    }

    /**
     * To load the service types data
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * 
     */
    public HashMap<String,ServiceKeywordCacheVO> loadServiceTypeCache() throws BTSLBaseException {

        final String methodName = "loadServiceTypeCache()";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap<String,ServiceKeywordCacheVO> serviceTypesMap = new HashMap<String,ServiceKeywordCacheVO>();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT ST.module,ST.type, ST.message_format, ");
        strBuff.append(" ST.service_type, ST.request_handler, ST.unregistered_access_allowed,ST.file_parser, ");
        strBuff.append(" ST.error_key,  ST.modified_on  as keywordtypemodified , ST.name name1 , ST.external_interface extintface,");
        strBuff.append(" ST.use_interface_language,ST.group_type,ST.sub_keyword_applicable,ST.REQUEST_PARAM,ST.RESPONSE_PARAM,ST.underprocess_check_reqd ");
        strBuff.append(" FROM service_type ST  WHERE ST.status <> 'N' ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            ServiceKeywordCacheVO cacheVO = null;
            String key = null;
            while (rs.next()) {
                cacheVO = new ServiceKeywordCacheVO();
                cacheVO.setModule(rs.getString("module"));
                cacheVO.setType(rs.getString("type"));
                cacheVO.setServiceType(rs.getString("service_type"));
                cacheVO.setRequestHandlerClass(rs.getString("request_handler"));
                cacheVO.setModifiedOnServiceKeyword(rs.getDate("keywordtypemodified"));
                cacheVO.setModifiedOnServiceKeywordTimestamp(rs.getTimestamp("keywordtypemodified"));
                cacheVO.setExternalInterface(rs.getString("extintface"));
                cacheVO.setUnregisteredAccessAllowed(rs.getString("unregistered_access_allowed"));
                cacheVO.setMessageFormat(rs.getString("message_format"));
                cacheVO.setUseInterfaceLanguage(rs.getString("use_interface_language"));
                cacheVO.setGroupType(rs.getString("group_type"));
                cacheVO.setFileParser(rs.getString("file_parser"));
                cacheVO.setRequestParam(rs.getString("REQUEST_PARAM"));
                cacheVO.setResponseParam(rs.getString("RESPONSE_PARAM"));
                if (TypesI.YES.equals(rs.getString("sub_keyword_applicable"))) {
                    cacheVO.setSubKeywordApplicable(true);
                }
                if (TypesI.YES.equals(rs.getString("underprocess_check_reqd"))) {
                    cacheVO.setServUderPrcsChkReqrd(true);
                }
                key = cacheVO.getServiceType() + "_" + cacheVO.getModule();
                serviceTypesMap.put(key, cacheVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: Service Map size=" + serviceTypesMap.size());
            }
        }
        return serviceTypesMap;
    }

    /**
     * method loadAllServiceTypeList
     * This method to load services having Status not N
     * 
     * @param p_con
     * @param p_status
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public ArrayList loadAllServiceTypeList(Connection p_con, String p_status) throws BTSLBaseException {
        final String methodName = "loadAllServiceTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        ArrayList serviceTypeList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
        	StringBuilder selectQuery = new StringBuilder();
            // module_code, service_type, sender_network, status,
            // language1_message, language2_message, modified_on
            selectQuery.append("SELECT module_code, service_type, sender_network, ");
            selectQuery.append("language1_message, language2_message, modified_on ");
            selectQuery.append("FROM  network_services ");
            selectQuery.append("WHERE status = ? ");
            // selectQuery.append("WHERE sender_network = ? ");
            selectQuery.append("ORDER BY module_code ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_status);
            rs = pstmtSelect.executeQuery();
            ServiceKeywordVO serviceKeywordVO = null;
            serviceTypeList = new ArrayList();
            while (rs.next()) {
                serviceKeywordVO = new ServiceKeywordVO();
                serviceKeywordVO.setModuleCode(rs.getString("module_code"));
                serviceKeywordVO.setServiceType(rs.getString("service_type"));
                serviceKeywordVO.setSender_network(rs.getString("sender_network"));
                serviceKeywordVO.setLanguage1Message(rs.getString("language1_message"));
                serviceKeywordVO.setLanguage2Message(rs.getString("language2_message"));
                serviceKeywordVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                serviceTypeList.add(serviceKeywordVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadAllServiceTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadAllServiceTypeList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:list size=" + serviceTypeList.size());
            }
        }
        return serviceTypeList;
    }

    /**
     * Method for updateServiceTypeStatus.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            java.util.ArrayList
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateServiceTypeStatus(Connection p_con, ArrayList p_voList) throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement psmtUpdate = null;
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;

        final String methodName = "updateServiceTypeStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_voList= " + p_voList);
        }

        try {
            // checking the modified status of all the networks one by one
            int listSize = 0;
            int count = 0;
            boolean modified = false;

            if (p_voList != null) {
                listSize = p_voList.size();
            }

            for (int i = 0; i < listSize; i++) {
                ServiceKeywordVO serviceKeywordVO = (ServiceKeywordVO) p_voList.get(i);
                modified = this.recordModified(p_con, serviceKeywordVO.getServiceType(), serviceKeywordVO.getModuleCode(), serviceKeywordVO.getSender_network(), serviceKeywordVO.getLastModified());

                // if modified = true mens record modified by another user
                if (modified) {
                    throw new BTSLBaseException("error.modified");
                } else {
                    count++;
                }
            }
            if (p_voList != null) {
                count = p_voList.size();
            }
            // if count== p_voList means no record is updated
            if ((p_voList != null) && (count == p_voList.size())) {
                count = 0;
                StringBuilder strBuff = new StringBuilder();

                strBuff.append("Update network_services SET status =?, modified_by =?, modified_on =? ");
                strBuff.append(" WHERE service_type=? AND module_code =? AND sender_network=? ");

                String updateQuery = strBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
                }

                // commented for DB2 psmtUpdate = (OraclePreparedStatement)
                // p_con.prepareStatement(updateQuery);
                psmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
                for (int i = 0; i < listSize; i++) {
                    ServiceKeywordVO serviceKeywordVO = (ServiceKeywordVO) p_voList.get(i);

                    psmtUpdate.setString(1, serviceKeywordVO.getStatus());
                    psmtUpdate.setString(2, serviceKeywordVO.getModifiedBy());
                    psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(serviceKeywordVO.getModifiedOn()));
                    psmtUpdate.setString(4, serviceKeywordVO.getServiceType());
                    psmtUpdate.setString(5, serviceKeywordVO.getModuleCode());
                    psmtUpdate.setString(6, serviceKeywordVO.getSender_network());

                    updateCount = psmtUpdate.executeUpdate();

                    psmtUpdate.clearParameters();

                    // check the status of the update
                    if (updateCount > 0) {
                        count++;
                    }
                }

                if (count == p_voList.size()) {
                    updateCount = 1;
                } else {
                    updateCount = 0;
                }
            }
        } // end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[updateServiceTypeStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[updateNetworkStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
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
     * @param serviceTypeCode
     *            String
     * @param oldLastModified
     *            long
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection con, String serviceTypeCode, String module, String networkCode, long oldLastModified) throws BTSLBaseException {
        final String methodName = "recordModified";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered: serviceTypeCode= " + serviceTypeCode + " module= " + module + " oldLastModified= " + oldLastModified);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = "SELECT modified_on FROM network_services WHERE service_type=? AND module_code = ? AND sender_network = ? ";
        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY: sqlselect= " + sqlRecordModified);
            }
            // create a prepared statement and execute it
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, serviceTypeCode);
            pstmt.setString(2, module);
            pstmt.setString(3, networkCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (_log.isDebugEnabled()) {
                _log.debug("recordModified", " old=" + oldLastModified);
                _log.debug("recordModified", " new=" + newLastModified.getTime());
            }
            if (newLastModified != null && newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

            return modified;
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[recordModified]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[recordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng: modified=" + modified);
            }
        } // end of finally
    } // end recordModified

    /**
     * Method loadServiceTypeListForServiceSelector. This method is used to load
     * the list of the
     * service_type from the table.
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceTypeListForServiceSelector(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadServiceTypeListForServiceSelector";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        ArrayList serviceTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
        	StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT service_type,name,sub_keyword_applicable FROM service_type WHERE status='Y' and external_interface='Y' and module='C2S' ORDER BY name");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("service_type"));
                listValueVO.setOtherInfo(rs.getString("sub_keyword_applicable"));
                serviceTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeListForServiceSelector]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceTypeListForServiceSelector]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:list size=" + serviceTypeList.size());
            }
        }
        return serviceTypeList;
    }

    public ArrayList loadServiceCache(Connection con) throws BTSLBaseException {

        final String methodName = "loadServiceCache()";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList serviceList = new ArrayList();
        HashMap serviceMap = new HashMap();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT");
        strBuff.append(" SK.keyword, ST.module, SK.req_interface_type, ST.type, ST.message_format, ");
        strBuff.append(" SK.service_port, ST.service_type, ST.request_handler, ST.unregistered_access_allowed, ");
        strBuff.append(" ST.error_key, SK.allowed_version, SK.status, SK.modified_on as keywordmodified, ");
        strBuff.append(" ST.modified_on  as keywordtypemodified , ST.name name1 , ST.external_interface extintface, ST.use_interface_language,ST.group_type,ST.sub_keyword_applicable,SK.sub_keyword ");
        strBuff.append(" FROM  ");
        strBuff.append(" service_type ST , service_keywords SK ");
        strBuff.append(" WHERE  ");
        strBuff.append(" ST.service_type = SK.service_type AND SK.status <> 'N' AND ST.status <> 'N' order by seq_no ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            ServiceKeywordCacheVO cacheVO = null;
            ServiceKeywordCacheVO cacheVO2 = null;
            ArrayList subKeywordList = null;
            while (rs.next()) {
                cacheVO = new ServiceKeywordCacheVO();

                cacheVO.setKeyword(rs.getString("keyword"));
                cacheVO.setModule(rs.getString("module"));
                cacheVO.setRequestInterfaceType(rs.getString("req_interface_type"));
                cacheVO.setType(rs.getString("type"));
                String servicePort = rs.getString("service_port");
                cacheVO.setServiceType(rs.getString("service_type"));
                cacheVO.setRequestHandlerClass(rs.getString("request_handler"));
                cacheVO.setErrorKey(rs.getString("error_key"));
                cacheVO.setAllowedVersion(rs.getString("allowed_version"));
                cacheVO.setStatus(rs.getString("status"));
                cacheVO.setModifiedServiceType(rs.getDate("keywordmodified"));
                cacheVO.setModifiedOnServiceKeyword(rs.getDate("keywordtypemodified"));
                cacheVO.setModifiedServiceTypeTimestamp(rs.getTimestamp("keywordmodified"));
                cacheVO.setModifiedOnServiceKeywordTimestamp(rs.getTimestamp("keywordtypemodified"));
                cacheVO.setExternalInterface(rs.getString("extintface"));
                cacheVO.setUnregisteredAccessAllowed(rs.getString("unregistered_access_allowed"));
                cacheVO.setName(rs.getString("name1"));
                cacheVO.setMessageFormat(rs.getString("message_format"));
                cacheVO.setUseInterfaceLanguage(rs.getString("use_interface_language"));
                cacheVO.setGroupType(rs.getString("group_type"));

                if (TypesI.YES.equals(rs.getString("sub_keyword_applicable"))) {
                    cacheVO.setSubKeywordApplicable(true);
                }

                cacheVO.setSubKeyword(rs.getString("sub_keyword"));
                String port[] = servicePort.split(",");

                for (int i = 0; i < port.length; i++) {
                    cacheVO2 = new ServiceKeywordCacheVO(cacheVO);
                    String key = cacheVO2.getKeyword() + "_" + cacheVO2.getModule() + "_" + cacheVO2.getRequestInterfaceType() + "_" + cacheVO2.getServerPort();
                    if (serviceMap.containsKey(key)) {
                        subKeywordList = cacheVO2.getSubKeywordList();
                        if (subKeywordList == null) {
                            subKeywordList = new ArrayList();
                        }
                        subKeywordList.add(cacheVO2);
                        cacheVO2.setSubKeywordList(subKeywordList);
                        serviceMap.put(key, cacheVO2);
                        serviceList.add(cacheVO2);
                    } else {
                        serviceMap.put(key, cacheVO2);
                        serviceList.add(cacheVO2);
                    }
                }
                // String key = cacheVO.getKeyword() + "_" +
                // cacheVO.getModule()+ "_" + cacheVO.getRequestInterfaceType()
                // + "_"+ cacheVO.getServerPort();
                // serviceMap.put(key, cacheVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: Service Map size=" + serviceList.size());
            }
        }
        return serviceList;
    }
			public HashMap<String,WebServiceKeywordCacheVO> loadWebServiceTypeCache() throws BTSLBaseException
			{

				final String methodName = "loadWebServiceTypeCache()";
				if (_log.isDebugEnabled()) _log.debug(methodName, "Entered");

				Connection con = null;
				PreparedStatement pstmt = null;
				ResultSet rs = null;

				HashMap<String,WebServiceKeywordCacheVO> webServiceTypesMap = new HashMap<String,WebServiceKeywordCacheVO>();

				StringBuilder strBuff = new StringBuilder();
				strBuff.append("SELECT ST.WEB_SERVICE_TYPE,ST.DESCRIPTION, ST.RESOURCE_NAME, ");
				strBuff.append(" ST.VALIDATOR_NAME, ST.FORMBEAN_NAME , ST.CONFIG_PATH, ST.WEB_SERVICE_URL, ST.IS_RBA_REQUIRE, ST.IS_DATA_VALIDATION_REQUIRE, ST.ROLE_CODE");
				strBuff.append(" FROM WEB_SERVICES_TYPES ST ");
				String sqlSelect = strBuff.toString();
				if(_log.isDebugEnabled())
					_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);

				try
				{
					con = OracleUtil.getSingleConnection();

					pstmt = con.prepareStatement(sqlSelect);
					rs = pstmt.executeQuery();
					WebServiceKeywordCacheVO cacheVO = null;
		            String key=null;
		            while (rs.next())
					{
						cacheVO = new WebServiceKeywordCacheVO();
						cacheVO.setWebSeviceKeyword(rs.getString("WEB_SERVICE_TYPE"));
					    cacheVO.setBeanName(rs.getString("FORMBEAN_NAME"));
						cacheVO.setValidatorName(rs.getString("VALIDATOR_NAME"));
						cacheVO.setResourceName(rs.getString("RESOURCE_NAME"));
						cacheVO.setMessageResource(rs.getString("CONFIG_PATH"));
						cacheVO.setRequestHandlerClass(rs.getString("DESCRIPTION"));
						cacheVO.setServiceUrl(rs.getString("WEB_SERVICE_URL"));
						cacheVO.setIsRBARequired(rs.getString("IS_RBA_REQUIRE"));
						cacheVO.setIsDataValidationRequired(rs.getString("IS_DATA_VALIDATION_REQUIRE"));
						cacheVO.setRoleCode(rs.getString("ROLE_CODE"));
						if(_log.isDebugEnabled()){
							_log.debug("WEB SERVICE KEYWORD", cacheVO.getWebSeviceKeyword());
						}
						key=cacheVO.getWebSeviceKeyword();
						webServiceTypesMap.put(key, cacheVO);
					}

				}
				catch (SQLException sqe)
				{
					_log.error(methodName, "SQLException : " + sqe);
					_log.errorTrace(methodName, sqe);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ServiceKeywordDAO[loadServiceTypeCache]","","","","SQL Exception:"+sqe.getMessage());
					throw new BTSLBaseException(this, methodName,"error.general.sql.processing",sqe);
				}
				catch (Exception ex)
				{
					if (ex instanceof BTSLBaseException)
						throw (BTSLBaseException) ex;
					_log.error(methodName, "Exception : " + ex);
					_log.errorTrace(methodName, ex);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ServiceKeywordDAO[loadServiceTypeCache]","","","","Exception:"+ex.getMessage());
					throw new BTSLBaseException(this, methodName,"error.general.processing",ex);
				}
				finally
				{
					try{if (rs != null){rs.close();}}catch (Exception e)	{_log.errorTrace(methodName, e);}
					try	{if (pstmt != null){pstmt.close();}}catch (Exception e){_log.errorTrace(methodName, e);}
					try{if (con != null){con.close();}}catch (Exception e){_log.errorTrace(methodName, e);}
					if (_log.isDebugEnabled())
					{
						_log.debug(methodName, "Exiting: Service Map size="+ webServiceTypesMap.size());
					}
				}
				return webServiceTypesMap;
			}
			
			
		    /**
		     * Method fetchServicekeywordByID. This method is used to load the full details
		     * of the specified servicekeywordID.
		     * 
		     * @param p_con
		     *            Connection
		     * @param servicekeywordID
		     *            String
		     * @return ServiceKeywordVO
		     * @throws BTSLBaseException
		     */
		    public ServiceKeywordVO fetchServicekeywordByID(Connection p_con, String serviceKeywordID) throws BTSLBaseException {
		        final String methodName = "fetchServicekeywordByID";
		        if (_log.isDebugEnabled()) {
		            _log.debug(methodName, "Entered:serviceType=" + serviceKeywordID);
		        }
		        
		        PreparedStatement pstmtSelect = null;
		        ResultSet rs = null;
		        ServiceKeywordVO serviceKeywordVO = null;
		        try {
		        	StringBuilder selectQuery = new StringBuilder();
		            selectQuery.append("SELECT LKP.lookup_name module_name, SK.service_type,SK.keyword,SK.req_interface_type,SK.service_port, ");
		            selectQuery.append("SK.name,SK.status,stsLookup.LOOKUP_NAME as statusDesc, menu,sub_menu,allowed_version,SK.modify_allowed,SK.modified_on,service_keyword_id, ");
		            selectQuery.append("ST.sub_keyword_applicable,SK.sub_keyword,ST.REQUEST_PARAM, SK.REQUEST_PARAM GREQUEST_PARAM ");
		            selectQuery.append("FROM service_keywords SK,service_type ST,lookups LKP ,LOOKUPS stsLookup ");
		            selectQuery.append("WHERE SK.service_keyword_id=? ");
		            selectQuery.append("AND ST.service_type=SK.service_type ");
		            selectQuery.append("AND LKP.lookup_code=ST.module AND LKP.lookup_type=? AND SK.status <> ? ");
		            selectQuery.append("AND stsLookup.LOOKUP_CODE =SK.status AND stsLookup.LOOKUP_TYPE ='GSTAT'  ");
		            selectQuery.append("ORDER BY service_type ");
		            String query = selectQuery.toString();
		            if (_log.isDebugEnabled()) {
		                _log.debug(methodName, "Query=" + query);
		            }
		            pstmtSelect = p_con.prepareStatement(query);
		            pstmtSelect.setString(1, serviceKeywordID);
		            pstmtSelect.setString(2, PretupsI.MODULE_TYPE);
		            pstmtSelect.setString(3, PretupsI.STATUS_DELETE);
		            rs = pstmtSelect.executeQuery();
		            while (rs.next()) {
		                serviceKeywordVO = new ServiceKeywordVO();
		                serviceKeywordVO.setServiceType(rs.getString("service_type"));
		                serviceKeywordVO.setKeyword(rs.getString("keyword"));
		                serviceKeywordVO.setInterface(rs.getString("req_interface_type"));
		                serviceKeywordVO.setReceivePort(rs.getString("service_port"));
		                // serviceKeywordVO.setResponseCode(rs.getString("res_interface_code"));
		                serviceKeywordVO.setName(rs.getString("name"));
		                serviceKeywordVO.setStatus(rs.getString("statusDesc"));
		                serviceKeywordVO.setMenu(rs.getString("menu"));
		                serviceKeywordVO.setSubMenu(rs.getString("sub_menu"));
		                serviceKeywordVO.setAllowedVersion(rs.getString("allowed_version"));
		                serviceKeywordVO.setModifyAllowed(rs.getString("modify_allowed"));
		                serviceKeywordVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
		                serviceKeywordVO.setServiceKeywordID(rs.getString("service_keyword_id"));
		                serviceKeywordVO.setModuleDesc(rs.getString("module_name"));

		                serviceKeywordVO.setSubKeyword(rs.getString("sub_keyword"));
		                serviceKeywordVO.setServiceRequestParam(rs.getString("REQUEST_PARAM"));
		                serviceKeywordVO.setGatewayRequestParam(rs.getString("GREQUEST_PARAM"));

		                if (TypesI.YES.equals(rs.getString("sub_keyword_applicable"))) {
		                    serviceKeywordVO.setSubKeywordApplicable(true);
		                }

		                
		            }
		        } catch (SQLException sqe) {
		            _log.error(methodName, "SQLException:" + sqe.getMessage());
		            _log.errorTrace(methodName, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[fetchServicekeywordByID]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR,sqe);
		        } catch (Exception e) {
		            _log.error(methodName, "Exception:" + e.getMessage());
		            _log.errorTrace(methodName, e);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[fetchServicekeywordByID]", "", "", "", "Exception:" + e.getMessage());
		            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR,e);
		        } finally {
		            try {
		                if (rs != null) {
		                    rs.close();
		                }
		            } catch (Exception ex) {
		                _log.errorTrace(methodName, ex);
		            }
		            try {
		                if (pstmtSelect != null) {
		                    pstmtSelect.close();
		                }
		            } catch (Exception ex) {
		                _log.errorTrace(methodName, ex);
		            }
		            
		        }
		        return serviceKeywordVO;
		    }
	
			

		    /**
		     * Method deleteServiceType.
		     * This method is used to delete the data in the table.
		     * 
		     * @param p_con
		     *            Connection
		     * @param p_serviceKeywordVO
		     *            ServiceKeywordVO
		     * @return int
		     * @throws BTSLBaseException
		     */
		    public int deleteServiceType(Connection p_con, ServiceKeywordVO p_serviceKeywordVO) throws BTSLBaseException {
		        final String methodName = "deleteServiceType";
		        if (_log.isDebugEnabled()) {
		            _log.debug(methodName, "Entered:p_serviceKeywordVO=" + p_serviceKeywordVO);
		        }
		        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
		        PreparedStatement pstmtUpdate = null;
		        int updateCount = 0;
		        try {
		        	StringBuilder updateQuery = new StringBuilder();
		            updateQuery.append("UPDATE  service_keywords SET status=?,");
		            updateQuery.append("modified_by=?, " );
		            updateQuery.append("modified_on=? ");
		            updateQuery.append(" WHERE service_keyword_id=? ");
		            String query = updateQuery.toString();
		            if (_log.isDebugEnabled()) {
		                _log.debug(methodName, "Query=" + query);
		            }

		            // commented for DB2 pstmtUpdate = (OraclePreparedStatement)
		            // p_con.prepareStatement(query);
		            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(query);
		            int i = 1;
		            // for multilanguage support
		            pstmtUpdate.setString(i++, p_serviceKeywordVO.getStatus());
		            pstmtUpdate.setString(i++, p_serviceKeywordVO.getModifiedBy());
		            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_serviceKeywordVO.getModifiedOn()));
		            pstmtUpdate.setString(i++, p_serviceKeywordVO.getServiceKeywordID());
		                updateCount = pstmtUpdate.executeUpdate();
		            
		        
		        } catch (SQLException sqe) {
		            _log.error(methodName, "SQLException:" + sqe.getMessage());
		            _log.errorTrace(methodName, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[updateServiceType]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
		        } catch (Exception e) {
		            _log.error(methodName, "Exception:" + e.getMessage());
		            _log.errorTrace(methodName, e);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[updateServiceType]", "", "", "", "Exception:" + e.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
		        } finally {
		            try {
		                if (pstmtUpdate != null) {
		                    pstmtUpdate.close();
		                }
		            } catch (Exception ex) {
		                _log.errorTrace(methodName, ex);
		            }
		            if (_log.isDebugEnabled()) {
		                _log.debug(methodName, "Exiting:return=" + updateCount);
		            }
		        }
		        return updateCount;
		    }
		    
		    
		    

		    /**
		     * Method getServiceTypeDetails. This method is used to load the list of the
		     * service_type from the table.
		     * 
		     * @param p_con
		     *            Connection
		     * @return ArrayList
		     * @throws BTSLBaseException
		     */
		    public  ServiceTypeobjVO  getServiceTypeDetails(Connection p_con,String serviceType) throws BTSLBaseException {
		        final String methodName = "getServiceTypeDetails";
		       
		        if (_log.isDebugEnabled()) {
		            _log.debug(methodName, "Entered:");
		        }
		        
		    PreparedStatement pstmtSelect = null;
		    ResultSet rs = null;
		    boolean valid=false;
		    ServiceTypeobjVO serviceTypeobjVO=null;
		    try {
		        StringBuilder selectQuery = new StringBuilder();
		        selectQuery.append("select  ST.SERVICE_TYPE,ST.NAME,ST.MODULE,ST.SUB_KEYWORD_APPLICABLE from  service_type ST   WHERE ST.SERVICE_TYPE = ?  ");
		        if (_log.isDebugEnabled()) {
		            _log.debug(methodName, "Query=" + selectQuery);
		        }
		        pstmtSelect = p_con.prepareStatement(selectQuery.toString());
		        pstmtSelect.setString(1, serviceType.trim());
		        rs = pstmtSelect.executeQuery();
		        
		        while (rs.next()) {
		        	serviceTypeobjVO= new ServiceTypeobjVO();
		        	serviceTypeobjVO.setServiceType(rs.getString("SERVICE_TYPE"));
		        	serviceTypeobjVO.setServiceTypeName(rs.getString("NAME"));
		        	serviceTypeobjVO.setModule(rs.getString("MODULE"));
		        	serviceTypeobjVO.setSubKeyWordApplicable(rs.getString("SUB_KEYWORD_APPLICABLE"));
		        }
		        
		    } catch (SQLException sqe) {
		        _log.error(methodName, "SQLException:" + sqe.getMessage());
		        _log.errorTrace(methodName, sqe);
		        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[getServiceTypeDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
		        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR,sqe);
		    } catch (Exception e) {
		        _log.error(methodName, "Exception:" + e.getMessage());
		        _log.errorTrace(methodName, e);
		        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[getServiceTypeDetails]", "", "", "", "Exception:" + e.getMessage());
		        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR,e);
		    } finally {
		        try {
		            if (rs != null) {
		                rs.close();
		            }
		        } catch (Exception ex) {
		            _log.errorTrace(methodName, ex);
		        }
		        try {
		            if (pstmtSelect != null) {
		                pstmtSelect.close();
		            }
		        } catch (Exception ex) {
		            _log.errorTrace(methodName, ex);
		        }
		        
		    }
		      return serviceTypeobjVO;
		    }
		    
		    
		    
		    
		    
		    
}

