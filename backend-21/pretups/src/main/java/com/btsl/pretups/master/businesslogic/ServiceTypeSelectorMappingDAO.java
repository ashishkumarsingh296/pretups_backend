package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class ServiceTypeSelectorMappingDAO {
    private static final Log _log = LogFactory.getLog(ServiceTypeSelectorMappingDAO.class.getName());

    /**
     * To load service type selector mapping details on service type basis.
     * 
     * @param p_con
     * @param p_serviceType
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList<ServiceTypeSelectorMappingVO> loadServiceSelectorMappingDetails(Connection con, String serviceType) throws BTSLBaseException {
        final String METHOD_NAME = "loadServiceSelectorMappingDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceSelectorMappingDetails()", "Entered with p_serviceType= " + serviceType);
        }
         
         
        ServiceTypeSelectorMappingVO serviceSelectorMappingVO = null;
        ArrayList<ServiceTypeSelectorMappingVO> serviceSelectorMappingList = null;
        LookupsVO lookupsVO = null;

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT stsm.service_type,stsm.selector_code,stsm.selector_name,stsm.sno,stsm.status,stsm.created_by,stsm.created_on,stsm.modified_by,");
        strBuff.append(" stsm.modified_on,stsm.description,stsm.sender_bundle_id,stsm.receiver_bundle_id, st.type,st.name,");
        strBuff.append(" ssm.sender_subscriber_type,ssm.receiver_subscriber_type,ssm.mapping_type,ssm.status mapstatus,stsm.is_default_code");
        strBuff.append(" FROM service_type_selector_mapping stsm,service_type st,subscriber_selector_mapping ssm");
        strBuff.append(" WHERE stsm.service_type=st.service_type AND stsm.status<>'N'  AND stsm.sno=ssm.sno");
        strBuff.append(" AND ssm.status<>'N' and stsm.SERVICE_TYPE=? order by stsm.selector_code");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceSelectorMappingDetails", "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, serviceType);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            serviceSelectorMappingList = new ArrayList<>();
            int index = 0;
            while (rs.next()) {
                serviceSelectorMappingVO = new ServiceTypeSelectorMappingVO();
                serviceSelectorMappingVO.setSno(rs.getString("sno"));
                serviceSelectorMappingVO.setServiceType(rs.getString("service_type"));
                serviceSelectorMappingVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                serviceSelectorMappingVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                serviceSelectorMappingVO.setSelectorCode(rs.getString("selector_code"));
                serviceSelectorMappingVO.setSelectorName(rs.getString("selector_name"));
                serviceSelectorMappingVO.setDescription(rs.getString("description"));
                serviceSelectorMappingVO.setSenderBundleID(rs.getString("sender_bundle_id"));
                serviceSelectorMappingVO.setReceiverBundleID(rs.getString("receiver_bundle_id"));
                serviceSelectorMappingVO.setCreatedBy(rs.getString("created_by"));
                serviceSelectorMappingVO.setType(rs.getString("type"));
                serviceSelectorMappingVO.setServiceName(rs.getString("name"));
                serviceSelectorMappingVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                serviceSelectorMappingVO.setModifiedBy(rs.getString("modified_by"));
                serviceSelectorMappingVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                serviceSelectorMappingVO.setStatus(rs.getString("mapstatus"));
                serviceSelectorMappingVO.setMappingType(rs.getString("mapping_type"));
                serviceSelectorMappingVO.setMappingStatus(rs.getString("mapstatus"));
                serviceSelectorMappingVO.setIsDefaultCodeStr(rs.getString("is_default_code"));
                serviceSelectorMappingVO.setRadioIndex(index);
                lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.STATUS_TYPE, serviceSelectorMappingVO.getStatus());
                serviceSelectorMappingVO.setStatusDesc(lookupsVO.getLookupName());
                if (PretupsI.YES.equalsIgnoreCase(serviceSelectorMappingVO.getIsDefaultCodeStr())) {
                    serviceSelectorMappingVO.setDefaultCode(true);
                }
                serviceSelectorMappingList.add(serviceSelectorMappingVO);
                index++;
            }
        } 
        }catch (SQLException sqe) {
            _log.error("loadServiceSelectorMappingDetails()", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "loadServiceSelectorMappingDetails()", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadServiceSelectorMappingDetails()", "Exception : " + e);
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "loadServiceSelectorMappingDetails()", "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
            	
                _log.debug("loadServiceSelectorMappingDetails()", "Exiting: serviceSelectorMappingList size=" + serviceSelectorMappingList.size());
            }
        }
        return serviceSelectorMappingList;
    }

    /**
     * Add mapping to database.
     * 
     * @param p_con
     * @param p_serviceSelectorVO
     * @return
     * @throws BTSLBaseException
     */
    public boolean addServiceSelectorMappingDetails(Connection con, ServiceTypeSelectorMappingVO serviceSelectorVO) throws BTSLBaseException {
        final String METHOD_NAME = "addServiceSelectorMappingDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("addServiceSelectorMappingDetails()", "Entered with p_serviceSelectorVO= " + serviceSelectorVO);
        }

        int addcount = -1;
        String selSNO = null;
        boolean isaddedSucessful = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PreparedStatement pstmtSTSM = null;
        PreparedStatement pstmtSSM = null;

        StringBuilder strBuffSelect = new StringBuilder();
        strBuffSelect.append(" select sno from service_type_selector_mapping where service_type=? and selector_code=? and status<>'N'");

        String selectStr = strBuffSelect.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("addServiceSelectorMappingDetails", "QUERY strBuffSelect=" + selectStr);
        }

        StringBuilder strBuffSTSM = new StringBuilder();
        strBuffSTSM.append(" insert into service_type_selector_mapping (sno, service_type, selector_code, selector_name, status, ");
        strBuffSTSM.append(" created_by, created_on, modified_by, modified_on, is_default_code, sender_bundle_id, receiver_bundle_id)");
        strBuffSTSM.append(" values (?,?,?,?,?,?,?,?,?,?,?,?)");

        String sqlInsertSTSM = strBuffSTSM.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("addServiceSelectorMappingDetails", "QUERY sqlInsertSTSM=" + sqlInsertSTSM);
        }

        StringBuilder strBuffSSM = new StringBuilder();
        strBuffSSM.append(" insert into subscriber_selector_mapping (sno, mapping_type, sender_subscriber_type, receiver_subscriber_type,");
        strBuffSSM.append(" status, created_by, created_on, modified_by, modified_on)");
        strBuffSSM.append(" values( ?,?,?,?,?,?,?,?,?) ");

        String sqlInsertSSM = strBuffSSM.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("addServiceSelectorMappingDetails", "QUERY sqlInsertSSM=" + sqlInsertSSM);
        }

        try {
            pstmtSelect = con.prepareStatement(selectStr);
            pstmtSelect.setString(1, serviceSelectorVO.getServiceType());
            pstmtSelect.setString(2, serviceSelectorVO.getSelectorCode());
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                selSNO = rs.getString("sno");
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addServiceSelectorMappingDetails", "selSNO=" + selSNO);
            }
            if (BTSLUtil.isNullString(selSNO)) {
                pstmtSTSM = con.prepareStatement(sqlInsertSTSM);
                pstmtSTSM.setString(1, serviceSelectorVO.getSno());
                pstmtSTSM.setString(2, serviceSelectorVO.getServiceType());
                pstmtSTSM.setString(3, serviceSelectorVO.getSelectorCode());
                pstmtSTSM.setString(4, serviceSelectorVO.getSelectorName());
                pstmtSTSM.setString(5, serviceSelectorVO.getStatus());
                pstmtSTSM.setString(6, serviceSelectorVO.getCreatedBy());
                pstmtSTSM.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(serviceSelectorVO.getCreatedOn()));
                pstmtSTSM.setString(8, serviceSelectorVO.getModifiedBy());
                pstmtSTSM.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(serviceSelectorVO.getModifiedOn()));
                pstmtSTSM.setString(10, serviceSelectorVO.getIsDefaultCodeStr());
                pstmtSTSM.setString(11, serviceSelectorVO.getSenderBundleID());
                pstmtSTSM.setString(12, serviceSelectorVO.getReceiverBundleID());
                addcount = pstmtSTSM.executeUpdate();
            }

            if (!BTSLUtil.isNullString(selSNO) || (BTSLUtil.isNullString(selSNO) && addcount > 0)) {
                addcount = 0;
                pstmtSSM = con.prepareStatement(sqlInsertSSM);
                if (BTSLUtil.isNullString(selSNO)) {
                    pstmtSSM.setString(1, serviceSelectorVO.getSno());
                } else {
                    pstmtSSM.setString(1, selSNO);
                }
                pstmtSSM.setString(2, serviceSelectorVO.getMappingType());
                pstmtSSM.setString(3, serviceSelectorVO.getSenderSubscriberType());
                pstmtSSM.setString(4, serviceSelectorVO.getReceiverSubscriberType());
                pstmtSSM.setString(5, serviceSelectorVO.getStatus());
                pstmtSSM.setString(6, serviceSelectorVO.getCreatedBy());
                pstmtSSM.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(serviceSelectorVO.getCreatedOn()));
                pstmtSSM.setString(8, serviceSelectorVO.getModifiedBy());
                pstmtSSM.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(serviceSelectorVO.getModifiedOn()));

                addcount = pstmtSSM.executeUpdate();
            }
            if (addcount > 0) {
                con.commit();
                isaddedSucessful = true;
            } else {
                con.rollback();
            }
        } catch (SQLException sqle) {
            _log.error("addServiceSelectorMappingDetails", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "addServiceSelectorMappingDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("addServiceSelectorMappingDetails", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "addServiceSelectorMappingDetails", "error.general.processing");
        } finally {
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmtSTSM!= null){
                	pstmtSTSM.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmtSSM!= null){
                	pstmtSSM.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
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
                _log.debug("addServiceSelectorMappingDetails", " Exiting isaddedSucessful " + isaddedSucessful);
            }
        }
        return isaddedSucessful;
    }

    /**
     * To check whether the given values already exist in mapping or not.
     * 
     * @param p_con
     * @param p_Sn
     * @param p_mappingType
     * @param p_senderSubType
     * @param p_receiverType
     * @return
     * @throws BTSLBaseException
     */
    public boolean isMappingExistSubscriberSelector(Connection con, String Sn, String serviceType, String selectorCode, String selectorName) throws BTSLBaseException {
        final String METHOD_NAME = "isMappingExistSubscriberSelector";
        if (_log.isDebugEnabled()) {
            _log.debug("isMappingExist()", "Entered with p_Sn= " + Sn + " p_serviceType= " + serviceType + " p_selectorCode= " + selectorCode + " p_selectorName= " + selectorName);
        }

        boolean isExist = false;
        
        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("select 1 from SERVICE_TYPE_SELECTOR_MAPPING where SNO=? and SERVICE_TYPE=? and SELECTOR_CODE=? and SELECTOR_NAME=?");

        String sqlQuery = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("isMappingExist", "QUERY sqlQuery=" + sqlQuery);
        }

        try (PreparedStatement pstmt = con.prepareStatement(sqlQuery);){
            
            pstmt.setString(1, Sn);
            pstmt.setString(2, serviceType);
            pstmt.setString(3, selectorCode);
            pstmt.setString(4, selectorName);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            while (rs.next()) {
                isExist = true;
            }
        } 
        }catch (SQLException sqle) {
            _log.error("isMappingExist", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "isMappingExist", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isMappingExist", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "isMappingExist", "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug("isMappingExist", " Exiting isExist " + isExist);
            }
        }
        return isExist;
    }

    /**
     * To update mapping
     * 
     * @param p_con
     * @param p_selectorName
     * @param p_senderSubscriberType
     * @param p_receiverSubscriberType
     * @param p_default
     * @param p_status
     * @param p_Sn
     * @param p_serviceType
     * @return
     * @throws BTSLBaseException
     */
    public boolean modifySubscriberSelector(Connection con, ServiceTypeSelectorMappingVO serviceTypeSelectorMappingVO) throws BTSLBaseException {
        final String METHOD_NAME = "modifySubscriberSelector";
        if (_log.isDebugEnabled()) {
            _log.debug("modifySubscriberSelector()", "Entered with p_serviceTypeSelectorMappingVO= " + serviceTypeSelectorMappingVO);
        }
        boolean isUpdated = false;
        int updateCount = -1;
        PreparedStatement pstmtSTSM = null;
        PreparedStatement pstmtSSM = null;

        StringBuilder strBuffSTSM = new StringBuilder();
        strBuffSTSM.append("update service_type_selector_mapping set is_default_code=? where sno=? and service_type=? and selector_code=?");
        String sqlQuerySTSM = strBuffSTSM.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("modifySubscriberSelector", "QUERY sqlQuerySTSM=" + sqlQuerySTSM);
        }

        StringBuilder strBuffSSM = new StringBuilder();
        strBuffSSM.append("update subscriber_selector_mapping set status=? where sender_subscriber_type=? and receiver_subscriber_type=? and sno=? ");
        String sqlQuerySSM = strBuffSSM.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("modifySubscriberSelector", "QUERY sqlQuerySSM=" + sqlQuerySSM);
        }
        try {
            pstmtSSM = con.prepareStatement(sqlQuerySSM);
            pstmtSSM.setString(1, serviceTypeSelectorMappingVO.getStatus());
            pstmtSSM.setString(2, serviceTypeSelectorMappingVO.getSenderSubscriberType());
            pstmtSSM.setString(3, serviceTypeSelectorMappingVO.getReceiverSubscriberType());
            pstmtSSM.setString(4, serviceTypeSelectorMappingVO.getSno());
            updateCount = pstmtSSM.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug("modifySubscriberSelector", "updateCount=" + updateCount);
            }

            if (updateCount > 0) {
                pstmtSTSM = con.prepareStatement(sqlQuerySTSM);
                pstmtSTSM.setString(1, serviceTypeSelectorMappingVO.getIsDefaultCodeStr());
                pstmtSTSM.setString(2, serviceTypeSelectorMappingVO.getSno());
                pstmtSTSM.setString(3, serviceTypeSelectorMappingVO.getServiceType());
                pstmtSTSM.setString(4, serviceTypeSelectorMappingVO.getSelectorCode());
                updateCount = pstmtSTSM.executeUpdate();
            }
            if (_log.isDebugEnabled()) {
                _log.debug("modifySubscriberSelector", "updateCount=" + updateCount);
            }
            if (updateCount > 0) {
                isUpdated = true;
                con.commit();
            } else {
                con.rollback();
            }
        } catch (SQLException sqle) {
            _log.error("modifySubscriberSelector", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "modifySubscriberSelector", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("modifySubscriberSelector", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "modifySubscriberSelector", "error.general.processing");
        } finally {
        	try{
                if (pstmtSTSM!= null){
                	pstmtSTSM.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmtSSM!= null){
                	pstmtSSM.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
            if (_log.isDebugEnabled()) {
                _log.debug("modifySubscriberSelector", " Exiting isUpdated " + isUpdated);
            }
        }
        return isUpdated;
    }

    /**
     * To check whether default mapping is available for the service.
     * 
     * @param p_con
     * @param p_serviceType
     * @return
     * @throws BTSLBaseException
     */
    public boolean isDefaultExistForService(Connection con, String serviceType, String sN) throws BTSLBaseException {
        final String METHOD_NAME = "isDefaultExistForService";
        if (_log.isDebugEnabled()) {
            _log.debug("isDefaultExistForService()", "Entered with p_serviceType= " + serviceType);
        }

        boolean isExist = false;
       
        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("select 1 from SERVICE_TYPE_SELECTOR_MAPPING where SERVICE_TYPE=? and IS_DEFAULT_CODE='Y' and SNO <> ?");

        String sqlQuery = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("isDefaultExistForService", "QUERY sqlQuery=" + sqlQuery);
        }

        try(PreparedStatement pstmt = con.prepareStatement(sqlQuery);) {
           
            pstmt.setString(1, serviceType);
            pstmt.setString(2, sN);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                isExist = true;
            }
        }
        }catch (SQLException sqle) {
            _log.error("isDefaultExistForService", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, "isDefaultExistForService", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isDefaultExistForService", " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "isDefaultExistForService", "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug("isDefaultExistForService", " Exiting isExist " + isExist);
            }
        }
        return isExist;
    }
}
