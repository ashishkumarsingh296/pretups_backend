package com.txn.pretups.preference.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class PreferenceTxnDAO {
    /**
     * Commons Logging instance.
     */
    private static Log _log = LogFactory.getLog(PreferenceTxnDAO.class.getName());
    private PreferenceTxnQry PreferenceTxnQry;

    public PreferenceTxnDAO() {
        PreferenceTxnQry = (PreferenceTxnQry) ObjectProducer.getObject(QueryConstants.PREFERNCE_TXN_QRY,
                QueryConstants.QUERY_PRODUCER);
    }

    /**
     * Load the list service Prefereences list
     * 
     * @param p_con
     * @param p_serviceClassID
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServicePreferencesList(Connection p_con, String p_serviceClassID) throws BTSLBaseException {
        final String methodName = "loadServicePreferencesList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_serviceClassID: " + p_serviceClassID);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreferenceCacheVO cacheVO = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT ");
        strBuff.append(" scp.preference_code, scp.network_code,  scp.service_class_id, scp.module, ");
        strBuff.append(" scp.value, scp.modified_on  ");
        strBuff.append(" FROM service_class_preferences scp ");
        strBuff.append(" where scp.SERVICE_CLASS_ID = ?  ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicePreferencesList()", "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_serviceClassID);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                cacheVO = new PreferenceCacheVO();
                cacheVO.setPreferenceCode(rs.getString("preference_code"));
                cacheVO.setNetworkCode(rs.getString("network_code"));
                cacheVO.setServiceCode(rs.getString("service_class_id"));
                cacheVO.setModuleCode(rs.getString("module")); // to be changed
                cacheVO.setValue(rs.getString("value"));
                cacheVO.setModifiedOn(rs.getDate("modified_on"));
                // cacheVO.setValueType(rs.getString("value_type"));
                cacheVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));
                list.add(cacheVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "PreferenceDAO[loadServicePreferencesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicePreferencesList()", "error.general.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "PreferenceDAO[loadServicePreferencesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicePreferencesList()", "error.general.processing");
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
            if (_log.isDebugEnabled()) {
                _log.debug("loadServicePreferencesList()", "Exited: list size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method loadServiceClassIDList. This method is to load the list of the
     * serviceClassesID of the specified network code.
     * 
     * @author Vikas Jauhari
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceClassIDList(Connection p_con, String p_networkCode, String p_serviceClassCode)
            throws BTSLBaseException {
        final String methodName = "loadServiceClassIDList";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" ,p_serviceClassCode=");
        	loggerValue.append(p_serviceClassCode);
            _log.debug(methodName, loggerValue);
        }
        ArrayList serviceClassList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery
                    .append("SELECT SC.service_class_id,SC.service_class_name,SC.SERVICE_CLASS_CODE,IT.interface_description,IT.interface_id, ITY.INTERFACE_CATEGORY ");
            selectQuery
                    .append("FROM interface_network_mapping INM,service_classes SC ,interfaces IT, interface_types ITY ");
            selectQuery.append("WHERE INM.network_code=? AND INM.interface_id=SC.interface_id AND SC.status <> 'N' ");
            selectQuery
                    .append("AND SC.interface_id=IT.interface_id AND IT.status <>'N' AND IT.INTERFACE_TYPE_ID=ITY.INTERFACE_TYPE_ID AND SC.SERVICE_CLASS_CODE=? ");
            selectQuery.append("ORDER BY SC.service_class_name ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_serviceClassCode);
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("SERVICE_CLASS_CODE"), (rs.getString("service_class_id")
                        + "_" + rs.getString("INTERFACE_CATEGORY")));
                serviceClassList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "PreferenceDAO[loadServiceClassIDList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceClassList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "PreferenceDAO[loadServiceClassIDList]", "", "", "", "Exception:" + e.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:list size=" + serviceClassList.size());
            }
        }
        return serviceClassList;
    }

    /**
     * Method loadPreferenceByServiceClassId. This method is to load the data of
     * the type SERVICE CLASS PREFERENCE and only those records which are
     * allowed to display i.e. display='Y'.
     * 
     * @author Vikas Jauhari
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_serviceClass
     *            String
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap loadPreferenceByServiceClassId(String p_networkCode, String p_serviceClassId)
            throws BTSLBaseException {
        final String methodName = "loadPreferenceByServiceClassId";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_serviceClassId=");
        	loggerValue.append(p_serviceClassId);
             _log.debug(methodName, loggerValue);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        PreferenceCacheVO preferenceCacheVO = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        HashMap map = new HashMap();
        try {

            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            pstmtSelect = PreferenceTxnQry.loadPreferenceByServiceTypeQry(con, p_networkCode, p_serviceClassId);

            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                preferenceCacheVO = new PreferenceCacheVO();
                preferenceCacheVO.setModuleCode(rs.getString("module"));
                preferenceCacheVO.setPreferenceCode(rs.getString("preference_code"));
                preferenceCacheVO.setPrefrenceName(rs.getString("name"));
                preferenceCacheVO.setValueType(rs.getString("value_type"));
                String value = rs.getString("value");
                if (BTSLUtil.isNullString(value) || value == null) {
                    preferenceCacheVO.setValue(rs.getString("default_value"));
                } else {
                    preferenceCacheVO.setValue(value);
                }
                preferenceCacheVO.setMinValue(rs.getString("min_value"));
                preferenceCacheVO.setMaxValue(rs.getString("max_value"));

                if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                    preferenceCacheVO
                            .setValue(PretupsBL.getDisplayAmount(Long.parseLong(preferenceCacheVO.getValue())));
                } else {
                    preferenceCacheVO.setValue(preferenceCacheVO.getValue());
                }

                String key = preferenceCacheVO.getPreferenceCode() + "_" + p_networkCode + "_" + p_serviceClassId;
                if (!BTSLUtil.isNullString(p_serviceClassId)) {
                    map.put(key, preferenceCacheVO);
                }
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "PreferenceDAO[loadPreferenceByServiceClassId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "PreferenceDAO[loadPreferenceByServiceClassId]", "", "", "", "Exception:" + e.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (mcomCon != null) {
                    mcomCon.close("PreferenceTxnDAO#loadPreferenceByServiceClassId");
                    mcomCon = null;
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:preferenceMap size =" + map.size());
            }
        }
        return map;
    }

    public HashMap loadPreferenceByServiceType(String p_networkCode, String p_serviceType) throws BTSLBaseException {
        final String methodName = "loadPreferenceByServiceType";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_serviceType=");
        	loggerValue.append(p_serviceType);
            _log.debug(methodName, loggerValue);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        PreferenceCacheVO preferenceCacheVO = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        HashMap map = new HashMap();
        try {

            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            pstmtSelect = PreferenceTxnQry.loadPreferenceByServiceTypeQry(con, p_networkCode, p_serviceType);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                preferenceCacheVO = new PreferenceCacheVO();
                preferenceCacheVO.setModuleCode(rs.getString("control_code"));
                preferenceCacheVO.setPreferenceCode(rs.getString("preference_code"));
                preferenceCacheVO.setPrefrenceName(rs.getString("name"));
                preferenceCacheVO.setValueType(rs.getString("value_type"));
                String value = rs.getString("value");
                if (BTSLUtil.isNullString(value) || value == null) {
                    preferenceCacheVO.setValue(rs.getString("default_value"));
                } else {
                    preferenceCacheVO.setValue(value);
                }
                preferenceCacheVO.setMinValue(rs.getString("min_value"));
                preferenceCacheVO.setMaxValue(rs.getString("max_value"));

                if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                    preferenceCacheVO
                            .setValue(PretupsBL.getDisplayAmount(Long.parseLong(preferenceCacheVO.getValue())));
                } else {
                    preferenceCacheVO.setValue(preferenceCacheVO.getValue());
                }

                String key = preferenceCacheVO.getPreferenceCode() + "_" + p_networkCode + "_" + p_serviceType;
                if (!BTSLUtil.isNullString(p_serviceType)) {
                    map.put(key, preferenceCacheVO);
                }
            }

        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "PreferenceDAO[loadPreferenceByServiceClassId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "PreferenceDAO[loadPreferenceByServiceClassId]", "", "", "", "Exception:" + e.getMessage());
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
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (mcomCon != null) {
                mcomCon.close("PreferenceTxnDAO#loadPreferenceByServiceType");
                mcomCon = null;
            }
        }
        return map;
    }

}
