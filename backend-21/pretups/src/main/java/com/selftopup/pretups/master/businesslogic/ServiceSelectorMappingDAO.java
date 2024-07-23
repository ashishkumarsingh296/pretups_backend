package com.selftopup.pretups.master.businesslogic;

/*
 * ServiceSelectorMappingDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ankit Singhal 22/05/2007 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

/**
 * @author ankit.singhal
 */
public class ServiceSelectorMappingDAO {

    /**
     * Load the service selector mapping cache
     * 
     * @return Arraylist
     */
    private static Log _log = LogFactory.getLog(ServiceSelectorMappingDAO.class.getName());

    public ArrayList loadServiceSelectorCache() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceSelectorCache()", "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        ArrayList serviceSelectorMappingList = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT s.service_type,s.selector_code,s.selector_name,s.sno,s.status,s.created_by,s.created_on,s.modified_by, ");
        strBuff.append(" s.modified_on,s.description,s.sender_bundle_id,s.receiver_bundle_id,");
        strBuff.append(" stype.type,stype.name,submapping.sender_subscriber_type,submapping.receiver_subscriber_type, ");
        strBuff.append(" submapping.mapping_type,submapping.status mapstatus,s.is_default_code FROM service_type_selector_mapping s,service_type stype,subscriber_selector_mapping submapping ");
        strBuff.append(" WHERE s.service_type=stype.service_type AND s.status<>'N'  AND s.sno=submapping.sno AND submapping.status<>'N' ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadServiceSelectorCache", "QUERY sqlSelect=" + sqlSelect);
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            serviceSelectorMappingList = new ArrayList();
            while (rs.next()) {
                serviceSelectorMappingVO = new ServiceSelectorMappingVO();
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
                serviceSelectorMappingVO.setStatus(rs.getString("status"));
                serviceSelectorMappingVO.setMappingType(rs.getString("mapping_type"));
                serviceSelectorMappingVO.setMappingStatus(rs.getString("mapstatus"));
                serviceSelectorMappingVO.setIsDefaultCodeStr(rs.getString("is_default_code"));
                if (PretupsI.YES.equalsIgnoreCase(serviceSelectorMappingVO.getIsDefaultCodeStr()))
                    serviceSelectorMappingVO.setDefaultCode(true);
                serviceSelectorMappingList.add(serviceSelectorMappingVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadServiceSelectorCache()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorMappingDAO[loadServiceSelectorCache]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceSelectorCache()", "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException)
                throw (BTSLBaseException) ex;
            _log.error("loadServiceSelectorCache()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorMappingDAO[loadServiceSelectorCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServiceSelectorCache()", "error.general.processing");
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
                _log.debug("loadTransferRuleCache()", "Exiting: serviceSelectorMappingList size=" + serviceSelectorMappingList.size());
            }
        }
        return serviceSelectorMappingList;
    }

    /**
     * Method to get the List of selectors for a service type
     * 
     * @return
     * @throws BTSLBaseException
     */
    public HashMap loadServiceTypeSelectorMap() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceTypeSelectorMap()", "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        HashMap serviceTypeSelectorMap = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT s.service_type,s.selector_code,s.selector_name,s.sno,s.status,s.created_by,s.created_on,s.modified_by, ");
        strBuff.append(" s.modified_on,s.description,s.sender_bundle_id,s.receiver_bundle_id,");
        strBuff.append(" stype.type,stype.name,s.is_default_code,NVL(el.amount,0)amount,NVL(el.modified_allowed,'Y')modified_allowed ");
        strBuff.append(" FROM service_type_selector_mapping s,service_type stype,selector_amount_mapping el WHERE s.service_type=stype.service_type ");
        strBuff.append(" AND s.status<>'N'AND el.selector_code(+) =s.selector_code AND el.service_type(+) =s.service_type ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypeSelectorMap", "QUERY sqlSelect=" + sqlSelect);
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            serviceTypeSelectorMap = new HashMap();
            while (rs.next()) {
                serviceSelectorMappingVO = new ServiceSelectorMappingVO();
                serviceSelectorMappingVO.setSno(rs.getString("sno"));
                serviceSelectorMappingVO.setServiceType(rs.getString("service_type"));
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
                serviceSelectorMappingVO.setStatus(rs.getString("status"));
                serviceSelectorMappingVO.setIsDefaultCodeStr(rs.getString("is_default_code"));
                serviceSelectorMappingVO.setAmountStr((PretupsBL.getDisplayAmount(rs.getLong("amount"))));
                if (PretupsI.NO.equalsIgnoreCase(rs.getString("modified_allowed")))
                    serviceSelectorMappingVO.setModifiedAllowed(false);
                else
                    serviceSelectorMappingVO.setModifiedAllowed(true);
                if (PretupsI.YES.equalsIgnoreCase(serviceSelectorMappingVO.getIsDefaultCodeStr()))
                    serviceSelectorMappingVO.setDefaultCode(true);
                serviceTypeSelectorMap.put(serviceSelectorMappingVO.getServiceType() + "_" + serviceSelectorMappingVO.getSelectorCode(), serviceSelectorMappingVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadServiceTypeSelectorMap()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorMappingDAO[loadServiceTypeSelectorMap]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeSelectorMap()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadServiceTypeSelectorMap()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceSelectorMappingDAO[loadServiceTypeSelectorMap]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeSelectorMap()", "error.general.processing");
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
                _log.debug("loadServiceTypeSelectorMap()", "Exiting ");
            }
        }
        return serviceTypeSelectorMap;
    }

    // // VASTRIX CHANGES..

    /**
     * To load service type selector mapping details on service type basis.
     * 
     * @param p_con
     * @param p_serviceType
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList<ServiceSelectorMappingVO> loadServiceSelectorMappingDetails(Connection p_con, String p_serviceType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceSelectorMappingDetails", "Entered with p_serviceType= " + p_serviceType);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        ArrayList<ServiceSelectorMappingVO> serviceSelectorMappingList = null;
        LookupsVO lookupsVO = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT stsm.service_type,stsm.selector_code,stsm.selector_name,stsm.sno,stsm.status,stsm.created_by,stsm.created_on,stsm.modified_by,");
        strBuff.append(" stsm.modified_on,stsm.description,st.type,st.name,");
        strBuff.append(" ssm.sender_subscriber_type,ssm.receiver_subscriber_type,ssm.mapping_type,ssm.status mapstatus,stsm.is_default_code,stsm.display_order ");// changed
                                                                                                                                                                  // by
                                                                                                                                                                  // PRS
        strBuff.append(" FROM service_type_selector_mapping stsm,service_type st,subscriber_selector_mapping ssm");
        strBuff.append(" WHERE stsm.service_type=st.service_type AND stsm.status<>'N' AND stsm.sno=ssm.sno");// change
                                                                                                             // for
                                                                                                             // suspended
                                                                                                             // status
        strBuff.append(" AND ssm.status<>'N' and stsm.SERVICE_TYPE=? order by stsm.selector_code");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadServiceSelectorMappingDetails", "QUERY sqlSelect=" + sqlSelect);

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_serviceType);
            rs = pstmt.executeQuery();
            serviceSelectorMappingList = new ArrayList<ServiceSelectorMappingVO>();
            int index = 0;
            while (rs.next()) {
                serviceSelectorMappingVO = new ServiceSelectorMappingVO();
                serviceSelectorMappingVO.setSno(rs.getString("sno"));
                serviceSelectorMappingVO.setServiceType(rs.getString("service_type"));
                serviceSelectorMappingVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                serviceSelectorMappingVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                serviceSelectorMappingVO.setSelectorCode(rs.getString("selector_code"));
                serviceSelectorMappingVO.setSelectorName(rs.getString("selector_name"));
                serviceSelectorMappingVO.setDescription(rs.getString("description"));
                // serviceSelectorMappingVO.setSenderBundleID(rs.getString("sender_bundle_id"));
                // serviceSelectorMappingVO.setReceiverBundleID(rs.getString("receiver_bundle_id"));
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
                serviceSelectorMappingVO.setDisplayOrder(rs.getString("display_order"));
                serviceSelectorMappingVO.setRadioIndex(index);
                lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.STATUS_TYPE, serviceSelectorMappingVO.getStatus());
                serviceSelectorMappingVO.setStatusDesc(lookupsVO.getLookupName());
                if (PretupsI.YES.equalsIgnoreCase(serviceSelectorMappingVO.getIsDefaultCodeStr()))
                    serviceSelectorMappingVO.setDefaultCode(true);
                serviceSelectorMappingList.add(serviceSelectorMappingVO);
                index++;
            }
        } catch (SQLException sqe) {
            _log.error("loadServiceSelectorMappingDetails()", "SQLException : " + sqe);
            sqe.printStackTrace();
            throw new BTSLBaseException(this, "loadServiceSelectorMappingDetails()", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadServiceSelectorMappingDetails()", "Exception : " + e);
            e.printStackTrace();
            throw new BTSLBaseException(this, "loadServiceSelectorMappingDetails()", "error.general.processing");
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

            if (_log.isDebugEnabled())
                _log.debug("loadServiceSelectorMappingDetails()", "Exiting: serviceSelectorMappingList size=" + serviceSelectorMappingList.size());
        }
        return serviceSelectorMappingList;
    }

    /**
     * To populate dropdown for display order.
     * 
     * @param p_con
     * @param p_serviceType
     * @return
     * @throws SQLException
     *             , Exception
     */
    public ArrayList<ServiceSelectorMappingVO> loadLookupDropDown(Connection p_con, String p_serviceType) throws SQLException, Exception {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList newOrderList = null;
        ArrayList selectorCountList = null;

        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(" SELECT count(*) display_order from SERVICE_TYPE_SELECTOR_MAPPING ");
            stringBuffer.append(" where SERVICE_TYPE=? ");
            stringBuffer.append(" and status<>'N' ");
            String sqlSelect = stringBuffer.toString();
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_serviceType);
            rs = pstmt.executeQuery();
            newOrderList = new ArrayList();
            selectorCountList = new ArrayList();
            _log.debug(sqlSelect, "+Query");
            ListValueVO listValueVO = null;
            int displayOrderCounter = 0;

            if (rs.next()) {
                displayOrderCounter = rs.getInt("display_order");
            }

            for (int i = 1; i <= displayOrderCounter; i++) {

                listValueVO = new ListValueVO(String.valueOf(i), String.valueOf(i));
                newOrderList.add(listValueVO);

            }
            _log.debug(selectorCountList.size(), "+selectorCountList.size()");

        } catch (SQLException sqle) {
            // TODO: handle exception
            _log.error("loadLookupDropDown ", "SQLException " + sqle.getMessage());
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (Exception e) {
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("Existing from loadLookupDropDown", " Exiting isExist " + newOrderList.size());
        }
        return newOrderList;
    }

    /**
     * Add mapping to database with new display order.
     * 
     * @param p_con
     * @param p_serviceSelectorVO
     * @return
     * @throws BTSLBaseException
     */
    public boolean saveSelectorsOrders(Connection p_con, ArrayList<ServiceSelectorMappingVO> p_selectorMappingVOList, String p_serviceType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("saveSelectorsOrders()", "Entered with p_selectorMappingVOList= " + p_selectorMappingVOList.size());
        boolean isUpdated = false;
        int updateCount = -1;
        int i = 0;
        ServiceSelectorMappingVO pServiceSelectorMappingVO = null;
        PreparedStatement pstmtSSM = null;
        StringBuffer updateQry = new StringBuffer();
        updateQry.append("update service_type_selector_mapping set display_order=? where  sno=?");
        String sqlQuerySTSM = updateQry.toString();
        if (_log.isDebugEnabled())
            _log.debug("saveSelectorsOrders", "QUERY update updateQry=" + updateQry);

        try {
            pstmtSSM = p_con.prepareStatement(sqlQuerySTSM);
            for (int s = 0; s < p_selectorMappingVOList.size(); s++) {
                i = 1;
                pServiceSelectorMappingVO = (ServiceSelectorMappingVO) p_selectorMappingVOList.get(s);
                pstmtSSM.clearParameters();
                pstmtSSM.setString(i++, pServiceSelectorMappingVO.getNewOrder());
                pstmtSSM.setString(i++, pServiceSelectorMappingVO.getSno());
                updateCount = pstmtSSM.executeUpdate();
            }
            if (_log.isDebugEnabled())
                _log.debug("saveSelectorsOrders", "updateCount=" + updateCount);
            if (updateCount > 0) {
                isUpdated = true;
            }

        } catch (SQLException sqle) {
            _log.error("saveSelectorsOrders", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "saveSelectorsOrders", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("saveSelectorsOrders", " Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "saveSelectorsOrders", "error.general.processing");
        } finally {

            try {
                if (pstmtSSM != null)
                    pstmtSSM.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("saveSelectorsOrders", " Exiting isUpdated " + isUpdated);
        }
        return isUpdated;
    }

    public int getNoOfProductsAlready(Connection p_con, String p_serviceType, String p_selector_code) throws BTSLBaseException {
        int noOfProducts = 0;
        PreparedStatement pstmtSTSM = null;

        StringBuffer strBuffSTSM = new StringBuffer();
        // strBuffSTSM.append("delete from service_type_selector_mapping  where sno=?");
        strBuffSTSM.append("SELECT count(*) row_count FROM SERVICE_TYPE_SELECTOR_MAPPING where service_type=? and status <> 'N'");
        String sqlQuerySTSM = strBuffSTSM.toString();

        if (_log.isDebugEnabled())
            _log.debug("getNoOfProductsAlready", "QUERY sqlQuerySTSM=" + sqlQuerySTSM);
        try {
            pstmtSTSM = p_con.prepareStatement(sqlQuerySTSM);
            pstmtSTSM.setString(1, p_serviceType);
            ResultSet rs = pstmtSTSM.executeQuery();
            if (rs.next())
                noOfProducts = rs.getInt("row_count");

        } catch (SQLException sqle) {
            _log.error("getNoOfProductsAlready", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "getNoOfProductsAlready", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("getNoOfProductsAlready", " Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "getNoOfProductsAlready", "error.general.processing");
        } finally {
            try {
                if (pstmtSTSM != null)
                    pstmtSTSM.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("getNoOfProductsAlready", " Exiting getNoOfProductsAlready noOfProducts " + noOfProducts);
        }
        return noOfProducts;
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
    public boolean isMappingExistSubscriberSelector(Connection p_con, String p_sno, String p_serviceType, String p_selectorCode, String p_selectorName) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isMappingExistSubscriberSelector()", "Entered with p_sno= " + p_sno + "p_serviceType= " + p_serviceType + " p_selectorCode= " + p_selectorCode + " p_selectorName= " + p_selectorName);

        boolean isExist = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("select 1 from SERVICE_TYPE_SELECTOR_MAPPING where SERVICE_TYPE=? and status <> 'N' and sno <> ? and (SELECTOR_CODE=? OR UPPER(SELECTOR_NAME)=UPPER(?)) ");

        String sqlQuery = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("isMappingExist", "QUERY sqlQuery=" + sqlQuery);

        try {
            pstmt = p_con.prepareStatement(sqlQuery);
            pstmt.setString(1, p_serviceType);
            pstmt.setString(2, p_sno);
            pstmt.setString(3, p_selectorCode);
            pstmt.setString(4, p_selectorName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        } catch (SQLException sqle) {
            _log.error("isMappingExistSubscriberSelector", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "isMappingExistSubscriberSelector", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error("isMappingExistSubscriberSelector", " Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "isMappingExistSubscriberSelector", "error.general.processing");
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (Exception e) {
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isMappingExistSubscriberSelector", " Exiting isExist " + isExist);
        }
        return isExist;
    }

    public boolean isExistsServiceInterfaceMapping(Connection p_con, String p_serviceType, String p_selector_code) throws BTSLBaseException {
        boolean isExists = false;
        PreparedStatement pstmtINTRMPNG = null;

        StringBuffer strBuffINTRMPNG = new StringBuffer();
        // strBuffSTSM.append("delete from service_type_selector_mapping  where sno=?");
        strBuffINTRMPNG.append("select 1 from SVC_SETOR_INTFC_MAPPING where service_type=? and selector_code=?");
        String sqlQueryINTRMPNG = strBuffINTRMPNG.toString();

        if (_log.isDebugEnabled())
            _log.debug("isExistsServiceInterfaceMapping", "QUERY sqlQueryINTRMPNG=" + sqlQueryINTRMPNG);
        try {
            pstmtINTRMPNG = p_con.prepareStatement(sqlQueryINTRMPNG);
            pstmtINTRMPNG.setString(1, p_serviceType);
            pstmtINTRMPNG.setString(2, p_selector_code);
            ResultSet rs = pstmtINTRMPNG.executeQuery();
            if (rs.next())
                isExists = true;

        } catch (SQLException sqle) {
            _log.error("isExistsServiceInterfaceMapping", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "isExistsServiceInterfaceMapping", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("isExistsServiceInterfaceMapping", " Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "isExistsServiceInterfaceMapping", "error.general.processing");
        } finally {
            try {
                if (pstmtINTRMPNG != null)
                    pstmtINTRMPNG.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isExistsServiceInterfaceMapping", " Exiting isExistsServiceInterfaceMapping isExists " + isExists);
        }
        return isExists;
    }

    /**
     * Add mapping to database.
     * 
     * @param p_con
     * @param p_serviceSelectorVO
     * @return
     * @throws BTSLBaseException
     * */
    public boolean addServiceSelectorMappingDetails(Connection p_con, ServiceSelectorMappingVO p_serviceSelectorVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addServiceSelectorMappingDetails()", "Entered with p_serviceSelectorVO= " + p_serviceSelectorVO.toString());

        int addcount = -1;
        boolean isaddedSucessful = false;
        ResultSet rsOrder = null;
        PreparedStatement pstmtSTSM = null;
        PreparedStatement pstmtSSM = null;
        PreparedStatement pstmtSSO = null;
        int orderCounter = 1;
        try {

            // for giving order
            StringBuffer strBuffSelectOrder = new StringBuffer();
            strBuffSelectOrder.append(" SELECT MAX(display_order) display_order FROM SERVICE_TYPE_SELECTOR_MAPPING where service_type=? ");
            String selectStrOrder = strBuffSelectOrder.toString();// end

            try {
                pstmtSSO = p_con.prepareStatement(selectStrOrder);// for order
                pstmtSSO.setString(1, p_serviceSelectorVO.getServiceType());
                rsOrder = pstmtSSO.executeQuery();

                if (rsOrder.next()) {

                    orderCounter = rsOrder.getInt("display_order") + 1;
                }
            } catch (Exception e) {
                _log.error("addServiceSelectorMappingDetails", " Exception " + e.getMessage());
                e.printStackTrace();
                throw new BTSLBaseException(this, "addServiceSelectorMappingDetails", "error.general.processing");
            }

            StringBuffer strBuffSTSM = new StringBuffer();
            strBuffSTSM.append(" insert into service_type_selector_mapping (sno, service_type, selector_code, selector_name, status, ");
            strBuffSTSM.append(" created_by, created_on, modified_by, modified_on, is_default_code,display_order )");
            strBuffSTSM.append(" values (?,?,?,?,?,?,?,?,?,?,?)");

            String sqlInsertSTSM = strBuffSTSM.toString();
            if (_log.isDebugEnabled())
                _log.debug("addServiceSelectorMappingDetails", "QUERY sqlInsertSTSM=" + sqlInsertSTSM);

            StringBuffer strBuffSSM = new StringBuffer();
            strBuffSSM.append(" insert into subscriber_selector_mapping (sno, mapping_type, sender_subscriber_type, receiver_subscriber_type,");
            strBuffSSM.append(" status, created_by, created_on, modified_by, modified_on)");
            strBuffSSM.append(" values( ?,?,?,?,?,?,?,?,?) ");

            String sqlInsertSSM = strBuffSSM.toString();
            if (_log.isDebugEnabled())
                _log.debug("addServiceSelectorMappingDetails", "QUERY sqlInsertSSM=" + sqlInsertSSM);

            pstmtSTSM = p_con.prepareStatement(sqlInsertSTSM);
            pstmtSTSM.setString(1, p_serviceSelectorVO.getSno());
            pstmtSTSM.setString(2, p_serviceSelectorVO.getServiceType());
            pstmtSTSM.setString(3, p_serviceSelectorVO.getSelectorCode());
            pstmtSTSM.setString(4, p_serviceSelectorVO.getSelectorName());
            pstmtSTSM.setString(5, p_serviceSelectorVO.getStatus());
            pstmtSTSM.setString(6, p_serviceSelectorVO.getCreatedBy());
            pstmtSTSM.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_serviceSelectorVO.getCreatedOn()));
            pstmtSTSM.setString(8, p_serviceSelectorVO.getModifiedBy());
            pstmtSTSM.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_serviceSelectorVO.getModifiedOn()));
            pstmtSTSM.setString(10, p_serviceSelectorVO.getIsDefaultCodeStr());
            // pstmtSTSM.setString(11, p_serviceSelectorVO.getSenderBundleID());
            // pstmtSTSM.setString(12,
            // p_serviceSelectorVO.getReceiverBundleID());
            pstmtSTSM.setInt(11, orderCounter);
            addcount = pstmtSTSM.executeUpdate();

            if (addcount > 0) {

                addcount = 0;
                pstmtSSM = p_con.prepareStatement(sqlInsertSSM);
                pstmtSSM.setString(1, p_serviceSelectorVO.getSno());
                pstmtSSM.setString(2, p_serviceSelectorVO.getMappingType());
                pstmtSSM.setString(3, p_serviceSelectorVO.getSenderSubscriberType());
                pstmtSSM.setString(4, p_serviceSelectorVO.getReceiverSubscriberType());
                pstmtSSM.setString(5, p_serviceSelectorVO.getStatus());
                pstmtSSM.setString(6, p_serviceSelectorVO.getCreatedBy());
                pstmtSSM.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_serviceSelectorVO.getCreatedOn()));
                pstmtSSM.setString(8, p_serviceSelectorVO.getModifiedBy());
                pstmtSSM.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_serviceSelectorVO.getModifiedOn()));

                addcount = pstmtSSM.executeUpdate();
            }

            if (addcount > 0) {
                // p_con.commit();
                isaddedSucessful = true;
            }
        } catch (SQLException sqle) {
            _log.error("addServiceSelectorMappingDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "addServiceSelectorMappingDetails", "error.general.sql.processing");
        } catch (BTSLBaseException be) {
            _log.error("addServiceSelectorMappingDetails", " Exception " + be.getMessage());
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            _log.error("addServiceSelectorMappingDetails", " Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "addServiceSelectorMappingDetails", "error.general.processing");
        } finally {
            try {
                if (pstmtSTSM != null)
                    pstmtSTSM.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSSM != null)
                    pstmtSSM.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addServiceSelectorMappingDetails", " Exiting isaddedSucessful " + isaddedSucessful);
        }
        return isaddedSucessful;
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
    public boolean modifySubscriberSelector(Connection p_con, ServiceSelectorMappingVO p_serviceSelectorMappingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("modifySubscriberSelector()", "Entered with p_serviceTypeSelectorMappingVO= " + p_serviceSelectorMappingVO);
        boolean isUpdated = false;
        int updateCount = -1;
        PreparedStatement pstmtSTSM_DFLT = null;
        PreparedStatement pstmtSTSM = null;
        PreparedStatement pstmtSSM = null;
        StringBuffer strBuffSTSM_DFLT = new StringBuffer();

        strBuffSTSM_DFLT.append("update service_type_selector_mapping set is_default_code='N' where is_default_code='Y'");
        String sqlQuerySTSM_DFLT = strBuffSTSM_DFLT.toString();

        if (_log.isDebugEnabled())
            _log.debug("modifySubscriberSelector", "QUERY sqlQuerySTSM=" + sqlQuerySTSM_DFLT);

        StringBuffer strBuffSTSM = new StringBuffer();
        strBuffSTSM.append("update service_type_selector_mapping set status=?,selector_name=?,is_default_code=?,display_order=? where sno=?");
        String sqlQuerySTSM = strBuffSTSM.toString();

        if (_log.isDebugEnabled())
            _log.debug("modifySubscriberSelector", "QUERY sqlQuerySTSM=" + sqlQuerySTSM);

        StringBuffer strBuffSSM = new StringBuffer();
        strBuffSSM.append("update subscriber_selector_mapping set status=?, sender_subscriber_type=?, receiver_subscriber_type=?  where sno=? ");
        String sqlQuerySSM = strBuffSSM.toString();

        if (_log.isDebugEnabled())
            _log.debug("modifySubscriberSelector", "QUERY sqlQuerySSM=" + sqlQuerySSM);
        try {
            if ("Y".equals(p_serviceSelectorMappingVO.getIsDefaultCodeStr())) {
                pstmtSTSM_DFLT = p_con.prepareStatement(sqlQuerySTSM_DFLT);
                updateCount = pstmtSTSM_DFLT.executeUpdate();
            }

            pstmtSSM = p_con.prepareStatement(sqlQuerySSM);
            pstmtSSM.setString(1, p_serviceSelectorMappingVO.getStatus());
            pstmtSSM.setString(2, p_serviceSelectorMappingVO.getSenderSubscriberType());
            pstmtSSM.setString(3, p_serviceSelectorMappingVO.getReceiverSubscriberType());
            pstmtSSM.setString(4, p_serviceSelectorMappingVO.getSno());
            updateCount = pstmtSSM.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug("modifySubscriberSelector", "updateCount=" + updateCount);

            if (updateCount > 0) {
                pstmtSTSM = p_con.prepareStatement(sqlQuerySTSM);
                pstmtSTSM.setString(1, p_serviceSelectorMappingVO.getStatus());
                pstmtSTSM.setString(2, p_serviceSelectorMappingVO.getSelectorName());
                pstmtSTSM.setString(3, p_serviceSelectorMappingVO.getIsDefaultCodeStr());
                pstmtSTSM.setString(4, p_serviceSelectorMappingVO.getDisplayOrder());
                pstmtSTSM.setString(5, p_serviceSelectorMappingVO.getSno());
                updateCount = pstmtSTSM.executeUpdate();
            }
            if (_log.isDebugEnabled())
                _log.debug("modifySubscriberSelector", "updateCount=" + updateCount);
            if (updateCount > 0) {
                isUpdated = true;
            }

        } catch (SQLException sqle) {
            _log.error("modifySubscriberSelector", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "modifySubscriberSelector", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("modifySubscriberSelector", " Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "modifySubscriberSelector", "error.general.processing");
        } finally {
            try {
                if (pstmtSTSM != null)
                    pstmtSTSM.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSSM != null)
                    pstmtSSM.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSTSM_DFLT != null)
                    pstmtSTSM_DFLT.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("modifySubscriberSelector", " Exiting isUpdated " + isUpdated);
        }
        return isUpdated;
    }

    /**
     * To delete mapping
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
    public boolean deleteSubscriberSelector(Connection p_con, ServiceSelectorMappingVO p_serviceSelectorMappingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteSubscriberSelector()", "Entered with p_serviceTypeSelectorMappingVO= " + p_serviceSelectorMappingVO);
        boolean isDeleted = false;
        int deleteCount = -1;
        PreparedStatement pstmtSTSM = null;
        PreparedStatement pstmtSSM = null;
        PreparedStatement pstmtSTSM_OU = null;

        StringBuffer strBuffSTSM = new StringBuffer();
        // strBuffSTSM.append("delete from service_type_selector_mapping  where sno=?");
        strBuffSTSM.append("update service_type_selector_mapping set status='N' , display_order=0 , modified_on=sysdate where sno=?");
        String sqlQuerySTSM = strBuffSTSM.toString();

        if (_log.isDebugEnabled())
            _log.debug("deleteSubscriberSelector", "QUERY sqlQuerySTSM=" + sqlQuerySTSM);

        StringBuffer strBuffSSM = new StringBuffer();
        // strBuffSSM.append("delete from subscriber_selector_mapping where sno=? ");
        strBuffSSM.append("update subscriber_selector_mapping set status = 'N' , modified_on=sysdate where sno=? ");
        String sqlQuerySSM = strBuffSSM.toString();

        if (_log.isDebugEnabled())
            _log.debug("deleteSubscriberSelector", "QUERY sqlQuerySSM=" + sqlQuerySSM);

        StringBuffer strBuffSTSM_OU = new StringBuffer();
        strBuffSTSM_OU.append("update service_type_selector_mapping set display_order=display_order - 1 , modified_on=sysdate  where display_order > ?");
        // strBuffSTSM.append("update service_type_selector_mapping outer set outer.display_order=(select display_order - 1 from service_type_selector_mapping iner where iner.sno = outer.sno) where display_order > ?");
        String sqlQuerySTSM_OU = strBuffSTSM_OU.toString();

        if (_log.isDebugEnabled())
            _log.debug("deleteSubscriberSelector", "QUERY sqlQuerySTSM_OU=" + sqlQuerySTSM_OU);

        try {
            pstmtSSM = p_con.prepareStatement(sqlQuerySSM);
            pstmtSSM.setString(1, p_serviceSelectorMappingVO.getSno());
            deleteCount = pstmtSSM.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug("deleteSubscriberSelector", "deleteCount=" + deleteCount);

            if (deleteCount > 0) {
                pstmtSTSM = p_con.prepareStatement(sqlQuerySTSM);
                pstmtSTSM.setString(1, p_serviceSelectorMappingVO.getSno());
                deleteCount = 0;
                deleteCount = pstmtSTSM.executeUpdate();
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteSubscriberSelector", "deleteCount=" + deleteCount);
            if (deleteCount > 0) {
                isDeleted = true;
            }

            if (deleteCount > 0) {
                pstmtSTSM_OU = p_con.prepareStatement(sqlQuerySTSM_OU);
                pstmtSTSM_OU.setString(1, p_serviceSelectorMappingVO.getDisplayOrder());
                deleteCount = 0;
                deleteCount = pstmtSTSM_OU.executeUpdate();
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteSubscriberSelector", "deleteCount=" + deleteCount);

        } catch (SQLException sqle) {
            _log.error("deleteSubscriberSelector", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw new BTSLBaseException(this, "deleteSubscriberSelector", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("deleteSubscriberSelector", " Exception " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "deleteSubscriberSelector", "error.general.processing");
        } finally {
            try {
                if (pstmtSTSM != null)
                    pstmtSTSM.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSSM != null)
                    pstmtSSM.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSTSM_OU != null)
                    pstmtSTSM_OU.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteSubscriberSelector", " Exiting isUpdated " + isDeleted);
        }
        return isDeleted;
    }
}
