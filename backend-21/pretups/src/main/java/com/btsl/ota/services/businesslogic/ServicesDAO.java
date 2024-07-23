/*
 * #ServicesDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Dec 19,2003 Gaurav Garg Initial Creation
 * Aug 9, 2005 Amit Ruwali Modified
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.btsl.ota.services.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.util.OracleUtil;

public class ServicesDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Constructor for ServicesDAO.
     */
    public ServicesDAO() {
        super();
    }

    /**
     * This method is used to load User SIM Services
     * 
     * @param p_con
     *            of Connection type
     * @param p_type
     *            of String type
     * @param p_profile
     *            of String type
     * @param p_networkCode
     *            of String type
     * @param p_simProfileId
     *            String
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList loadUserSIMServiceList(Connection p_con, String p_type, String p_profile, String p_networkCode, String p_simProfileId) throws BTSLBaseException {
        final String methodName = "loadUserSIMServiceList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "type=" + p_type + ",profile=" + p_profile + ",locationCode=" + p_networkCode + ",SimProfileId = " + p_simProfileId);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        ArrayList serviceList = new ArrayList();
        UserServicesVO userServicesVO = null;
        try {

            StringBuffer sqlLoadBuf = new StringBuffer("SELECT SS.service_set_id serSetID,USS.position, USS.service_id, USS.major_version, USS.minor_version,USS.offset,");
            sqlLoadBuf.append(" USS.status, USS.network_code,L.network_name ,SS.description,SS.label1,SS.label2,SS.length,SS.bytecode ");
            sqlLoadBuf.append(" FROM user_sim_services USS,networks L,sim_services SS");
            sqlLoadBuf.append(" WHERE USS.user_type = ? AND USS.profile = ? AND USS.network_code = ? ");
            sqlLoadBuf.append(" AND USS.sim_profile_id = ? AND USS.network_code=L.network_code ");
            sqlLoadBuf.append(" AND USS.service_id=SS.service_id AND USS.major_version=SS.major_version ");
            sqlLoadBuf.append(" AND SS.service_set_id=L.service_set_id AND present_status='CURRENT' ");
            sqlLoadBuf.append(" AND USS.minor_version=SS.minor_version order by USS.position");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query=" + sqlLoadBuf.toString());
            }
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_type);
            dbPs.setString(2, p_profile);
            dbPs.setString(3, p_networkCode);
            dbPs.setString(4, p_simProfileId);
            rs = dbPs.executeQuery();
            while (rs.next()) {
                userServicesVO = new UserServicesVO();
                userServicesVO.setUserType(p_type);
                userServicesVO.setServiceSetID(rs.getString("serSetID"));
                userServicesVO.setPosition(rs.getInt("position"));
                userServicesVO.setByteCode(rs.getString("bytecode"));
                userServicesVO.setServiceID(rs.getString("service_id"));
                userServicesVO.setMajorVersion(rs.getString("major_version"));
                userServicesVO.setMinorVersion(rs.getString("minor_version"));
                userServicesVO.setStatus(rs.getString("status"));
                userServicesVO.setLocationName(rs.getString("network_name"));
                userServicesVO.setLocationCode(rs.getString("network_code"));
                userServicesVO.setDescription(rs.getString("description"));
                userServicesVO.setLabel1(rs.getString("label1"));
                userServicesVO.setLabel2(rs.getString("label2"));
                userServicesVO.setOffset(rs.getLong("offset"));
                userServicesVO.setLength(rs.getLong("length"));
                serviceList.add(userServicesVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesDAO[loadUserSIMServiceList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesDAO[loadUserSIMServiceList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                	dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "serviceList size=" + serviceList.size());
            }
        }
        return serviceList;
    }

    /**
     * This method is used to get Sim profile inforamtion based upon mobileNo
     * 
     * @param p_con
     *            of Connection type
     * @param p_simProId
     *            of String type
     * @return returns the SimProfileVO
     * @exception BTSLBaseException
     */

    public SimProfileVO loadSimProfileInfo(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "loadSimProfileInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_msisdn=" + p_msisdn);
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        SimProfileVO simProfileVO = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer();
            /*
             * sqlLoadBuf.append(
             * "SELECT SP.SIM_ID, SP.SIM_APP_VERSION, SP.SIM_VENDOR_NAME,SP.SIM_TYPE,SP.BYTECODE_FILE_SIZE,"
             * );
             * sqlLoadBuf.append(
             * " SP.NO_OF_MENU_OPTIONS,  SP.MENU_RECORD_LEN, SP.MAX_CONCAT_SMS, "
             * );
             * sqlLoadBuf.append(
             * " SP.UNI_FILE_RECORD_LEN, SP.KEY_SET_NO, SP.APPLET_TAR_VALUE, SP.ENCRYPT_ALGO, "
             * );
             * sqlLoadBuf.append(
             * "  SP.ENCRYPT_MODE,  SP.ENCRYPT_PADDING,SP.STATUS FROM sim_profile SP , pos_keys PK where  "
             * );
             * sqlLoadBuf.append(" PK.msisdn = ?  ");
             * sqlLoadBuf.append("  AND PK.sim_profile_id = SP.sim_id  ");
             */
            sqlLoadBuf.append("SELECT SP.sim_id, SP.sim_app_version, SP.sim_vendor_name,SP.sim_type,SP.bytecode_file_size,");
            sqlLoadBuf.append("SP.no_of_menu_options,  SP.menu_record_len, SP.max_concat_sms, ");
            sqlLoadBuf.append("SP.uni_file_record_len, SP.key_set_no, SP.applet_tar_value, SP.encrypt_algo, ");
            sqlLoadBuf.append("SP.encrypt_mode,  SP.encrypt_padding,SP.status FROM sim_profile SP , pos_keys PK ");
            sqlLoadBuf.append("WHERE PK.msisdn = ?  ");
            sqlLoadBuf.append("AND PK.sim_profile_id = SP.sim_id  ");

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query= " + sqlLoadBuf.toString());
            }
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_msisdn);
            rs = dbPs.executeQuery();
            if (rs.next()) {
                simProfileVO = new SimProfileVO();
                simProfileVO.setSimID(rs.getString("sim_id"));
                simProfileVO.setSimAppVersion(rs.getString("sim_app_version"));
                simProfileVO.setSimVendorName(rs.getString("sim_vendor_name"));
                simProfileVO.setSimType(rs.getString("sim_type"));
                simProfileVO.setByteCodeFileSize(rs.getInt("bytecode_file_size"));
                simProfileVO.setNoOfmenus(rs.getInt("no_of_menu_options"));
                simProfileVO.setMenuSize(rs.getInt("menu_record_len"));
                simProfileVO.setMaxContSMSSize(rs.getInt("max_concat_sms"));
                simProfileVO.setUniCodeFileSize(rs.getInt("uni_file_record_len"));
                simProfileVO.setKeySetNo(rs.getInt("key_set_no"));
                simProfileVO.setAppletTarValue(rs.getLong("applet_tar_value"));
                simProfileVO.setEncryptALGO(rs.getString("encrypt_algo"));
                simProfileVO.setEncryptMode(rs.getString("encrypt_mode"));
                simProfileVO.setEncryptPad(rs.getString("encrypt_padding"));
                simProfileVO.setStatus(rs.getString("status"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesDAO[loadSimProfileInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesDAO[loadSimProfileInfo]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                	dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting :::simProfileVO:" + simProfileVO);
            }
        }
        return simProfileVO;
    }

    /**
     * Method to load the Sim Profile List
     * 
     * @return
     * @throws BTSLBaseException
     */
    public HashMap loadSimProfileList() throws BTSLBaseException {
        final String methodName = "loadSimProfileList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        PreparedStatement pstmtSelect = null;
        HashMap simProfileMap = new HashMap();
        // RequestInterfaceDetailVO requestInterfaceDetailVO=null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getSingleConnection();
            ServicesQry servicesQry = (ServicesQry)ObjectProducer.getObject(QueryConstants.SERVICES_QRY, QueryConstants.QUERY_PRODUCER);
			String selectQueryBuff  = servicesQry.loadSimProfileListQry();

            String selectQuery = selectQueryBuff;
            SimProfileVO simProfileVO = null;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query= " + selectQuery);
            }
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, TypesI.YES);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                simProfileVO = new SimProfileVO();
                simProfileVO.setSimID(rs.getString("sim_id"));
                simProfileVO.setSimAppVersion(rs.getString("sim_app_version"));
                simProfileVO.setSimVendorName(rs.getString("sim_vendor_name"));
                simProfileVO.setSimType(rs.getString("sim_type"));
                simProfileVO.setByteCodeFileSize(rs.getInt("bytecode_file_size"));
                simProfileVO.setNoOfmenus(rs.getInt("no_of_menu_options"));
                simProfileVO.setMenuSize(rs.getInt("menu_record_len"));
                simProfileVO.setMaxContSMSSize(rs.getInt("max_concat_sms"));
                simProfileVO.setUniCodeFileSize(rs.getInt("uni_file_record_len"));
                simProfileVO.setKeySetNo(rs.getInt("key_set_no"));
                simProfileVO.setAppletTarValue(rs.getLong("applet_tar_value"));
                simProfileVO.setEncryptALGO(rs.getString("encrypt_algo"));
                simProfileVO.setEncryptMode(rs.getString("encrypt_mode"));
                simProfileVO.setEncryptPad(rs.getString("encrypt_padding"));
                simProfileVO.setStatus(rs.getString("status"));
                simProfileMap.put(simProfileVO.getSimID(), simProfileVO);
            }
            return simProfileMap;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                	pstmtSelect.close();
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
                _log.debug(methodName, "Exiting simProfileMap.size:" + simProfileMap.size());
            }
        }// end of finally
    }

    /**
     * @author ankur.dhawan
     *         Description : This method loads the default services available
     *         for the new channel users
     *         Method : loadServicesCache
     * @throws BTSLBaseException
     * @return HashMap
     */

    // for User default Configuration BY ANUPAM MALVIYA FOR USER DEFAULT
    // MANAGEMENT CONFIGURATION

    public HashMap<String, Object> loadServicesCache() throws BTSLBaseException {
        final String methodName = "loadServicesCache";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        HashMap<String, Object> servicesMap = new HashMap<String, Object>();
        ArrayList<ServiceTypeVO> serviceList = null;
        ServiceTypeVO serviceTypeVO = null;
        Connection con = null;
        String sqlSelectServices = null;
        PreparedStatement pstmtSelectServices = null;
        ResultSet rs = null;
        String key = null;
        String prevKey = null;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT distinct cst.category_code as category_code,ns.sender_network as sender_network,ns.receiver_network as receiver_network ,st.service_type as service_type,st.name as name,st.type as type ");
        strBuff.append(" FROM service_type st,network_services ns,category_service_type cst,categories c ");
        strBuff.append(" WHERE st.external_interface = 'Y' AND st.status = 'Y' AND st.service_type = ns.service_type ");
        strBuff.append(" AND st.module =? AND CST.service_type=st.service_type AND c.category_code=cst.category_code ");
        strBuff.append(" AND c.status='Y' ORDER BY category_code,sender_network,receiver_network ");
        sqlSelectServices = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Query sqlSelectServices:" + sqlSelectServices);
        }

        try {
            con = OracleUtil.getSingleConnection();
            pstmtSelectServices = con.prepareStatement(sqlSelectServices);
            pstmtSelectServices.setString(1, PretupsI.C2S_MODULE);
            rs = pstmtSelectServices.executeQuery();
            while (rs.next()) {
                key = rs.getString("category_code") + "_" + rs.getString("sender_network") + "_" + rs.getString("receiver_network");
                serviceTypeVO = new ServiceTypeVO();
                serviceTypeVO.setServiceType(rs.getString("service_type"));
                serviceTypeVO.setServiceName(rs.getString("name"));
                serviceTypeVO.setType(rs.getString("type"));
                // changed for optimization
                if (prevKey == null) {
                    serviceList = new ArrayList<ServiceTypeVO>();
                } else if (!prevKey.equals(key)) {
                    servicesMap.put(prevKey, serviceList);
                    serviceList = new ArrayList<ServiceTypeVO>();
                }
                serviceList.add(serviceTypeVO);
                prevKey = key;
            }
            if (prevKey != null && prevKey.equals(key)) {
                servicesMap.put(prevKey, serviceList);
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException : " + sqle);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesDAO[loadServicesCache]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesDAO[loadServicesCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectServices != null) {
                	pstmtSelectServices.close();
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
                _log.debug(methodName, "Exiting: serviceCache.size()=" + servicesMap.size());
            }
        }
        return servicesMap;
    }
}
