package com.web.pretups.preference.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class PreferenceWebDAO {

    /**
     * Commons Logging instance.
     */
    private static Log _log = LogFactory.getLog(PreferenceWebDAO.class.getName());

    /**
     * Method loadServiceClassList.
     * This method is to load the list of the serviceClasses of the specified
     * network code.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceClassList(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadServiceClassList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkCode=" + p_networkCode);
        }
        final ArrayList serviceClassList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT SC.service_class_id,SC.service_class_name,IT.interface_description,IT.interface_id ");
            selectQuery.append("FROM interface_network_mapping INM,service_classes SC ,interfaces IT ");
            selectQuery.append("WHERE INM.network_code=? AND INM.interface_id=SC.interface_id AND SC.status <> 'N' ");
            selectQuery.append("AND SC.interface_id=IT.interface_id AND IT.status <>'N' ");
            selectQuery.append("ORDER BY SC.service_class_name ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_networkCode);
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("service_class_name") + " ( " + rs.getString("interface_description") + " )", rs.getString("service_class_id"));
                listValueVO.setOtherInfo(rs.getString("interface_id"));
                serviceClassList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServiceClassList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServiceClassList]", "", "", "",
                "Exception:" + e.getMessage());
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
     * Method loadSystemPreferenceData.
     * This method is to load the data of the type SYSTEM PREFERENCE and only
     * those records which
     * are allowed to display i.e. display='Y'.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_preferenceType
     *            String
     * @param p_module
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSystemPreferenceData(Connection p_con, String p_preferenceType, String p_module) throws BTSLBaseException {
        final String methodName = "loadSystemPreferenceData";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_preferenceType = ");
        	msg.append(p_preferenceType);
        	msg.append(", p_module = ");
        	msg.append(p_module);

        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreferenceCacheVO preferenceCacheVO = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList preferenceList = new ArrayList();
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT module, preference_code,name,value_type,default_value,min_value, ");
            selectQuery.append("max_value,max_size,modified_allowed,description,modified_on,allowed_values, fixed_value ");
            selectQuery.append("FROM system_preferences  ");
            selectQuery.append("WHERE display='Y' ");
            selectQuery.append("AND type=? AND module = ? ORDER BY name");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());

            pstmtSelect.setString(1, p_preferenceType);
            pstmtSelect.setString(2, p_module);
            rs = pstmtSelect.executeQuery();
            String allowedValues[] = null;
            int i = 0;
            final ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            final ArrayList valueTypeList = LookupsCache.loadLookupDropDown(PretupsI.PREFERENCE_VALUE_TYPE, true);
            ArrayList allowedValuesList = null;
            while (rs.next()) {
                preferenceCacheVO = new PreferenceCacheVO();
                preferenceCacheVO.setModuleCode(rs.getString("module"));
                if (preferenceCacheVO.getModuleCode().equals(PretupsI.ALL)) {
                    preferenceCacheVO.setModuleDescription(PretupsI.ALL);
                } else {

                    preferenceCacheVO.setModuleDescription((BTSLUtil.getOptionDesc(preferenceCacheVO.getModuleCode(), moduleList)).getLabel());
                }
                preferenceCacheVO.setPreferenceCode(rs.getString("preference_code"));
                preferenceCacheVO.setPrefrenceName(rs.getString("name"));
                preferenceCacheVO.setValueType(rs.getString("value_type"));
                preferenceCacheVO.setValue(rs.getString("default_value"));
                preferenceCacheVO.setValueTypeDesc(((ListValueVO) BTSLUtil.getOptionDesc(preferenceCacheVO.getValueType(), valueTypeList)).getLabel());
                if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                    preferenceCacheVO.setValue(PretupsBL.getDisplayAmount(Long.parseLong(preferenceCacheVO.getValue())));
                } else {
                    preferenceCacheVO.setValue(preferenceCacheVO.getValue());
                }
                preferenceCacheVO.setMinValue(rs.getString("min_value"));
                preferenceCacheVO.setMaxValue(rs.getString("max_value"));
                preferenceCacheVO.setMaxSize(rs.getString("max_size"));
                preferenceCacheVO.setDescription(rs.getString("description"));
                preferenceCacheVO.setModifiedAllowed(rs.getString("modified_allowed"));
                if (preferenceCacheVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_YES)) {
                    preferenceCacheVO.setDisableAllow(false);
                } else if (preferenceCacheVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_NO)) {
                    preferenceCacheVO.setDisableAllow(true);
                }
                preferenceCacheVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                preferenceCacheVO.setFixedValue(rs.getString("fixed_value"));
                preferenceCacheVO.setAllowedValues(rs.getString("allowed_values"));
                if (!BTSLUtil.isNullString(preferenceCacheVO.getFixedValue()) && PretupsI.NO.equals(preferenceCacheVO.getFixedValue()) && PretupsI.YES
                    .equals(preferenceCacheVO.getModifiedAllowed())) {
                    allowedValues = preferenceCacheVO.getAllowedValues().split(",");
                    allowedValuesList = new ArrayList();
                    for (i = 0; i < allowedValues.length; i++) {
                        allowedValuesList.add(new ListValueVO(allowedValues[i], allowedValues[i]));
                    }
                    preferenceCacheVO.setAllowedValuesList(allowedValuesList);
                } else {
                    preferenceCacheVO.setAllowedValuesList(null);
                }

                preferenceCacheVO.setAllowAction("N");// for default selection
                // of the radio button
                preferenceList.add(preferenceCacheVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadSystemPreferenceData]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadSystemPreferenceData]", "", "", "",
                "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting:preferenceList size=" + preferenceList.size());
            }
        }
        return preferenceList;
    }

    /**
     * Method updateSystemPreference.
     * This method is to update the record of the SYSTEM PREFERENCE of the
     * specified preferenceCode.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_preferenceList
     *            ArrayList
     * @param p_date
     *            Date
     * @param p_userID
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSystemPreference(Connection p_con, ArrayList p_preferenceList, Date p_date, String p_userID) throws BTSLBaseException {
        final String methodName = "updateSystemPreference";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_preferenceList.size()= ");
        	msg.append(p_preferenceList.size());
        	msg.append(", p_date = ");
        	msg.append(p_date);
        	msg.append(", p_userID = ");
        	msg.append(p_userID);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("UPDATE system_preferences SET default_value=?,modified_by=?,modified_on=? ");
            updateQuery.append("WHERE preference_code=? and modified_allowed='Y'");
            final String query = updateQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtUpdate = p_con.prepareStatement(query);
            PreferenceCacheVO preferenceCacheVO = null;
            int preferenceListSize = p_preferenceList.size();
            for (int i = 0; i < preferenceListSize; i++) {
                preferenceCacheVO = (PreferenceCacheVO) p_preferenceList.get(i);
                if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                    pstmtUpdate.setString(1, String.valueOf(PretupsBL.getSystemAmount(preferenceCacheVO.getValue())));
                } else {
                    pstmtUpdate.setString(1, preferenceCacheVO.getValue().trim());
                }
                pstmtUpdate.setString(2, p_userID);
                pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_date));
                pstmtUpdate.setString(4, preferenceCacheVO.getPreferenceCode());
                // for the checking is the record be modified during the
                // transaction.
                final boolean modified = isRecordModified(p_con, preferenceCacheVO, 1);
                if (modified) {
                    throw new BTSLBaseException(this, methodName, "error.modify.true");
                }
                updateCount += pstmtUpdate.executeUpdate();
                pstmtUpdate.clearParameters();
            }

        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[updateSystemPreference]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[updateSystemPreference]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
     * Method recordModified.
     * This method is used to check is the record modified during the
     * transaction for the various
     * preference tables depending on the value of the flag
     * flag=1 for system preferences table
     * flag=2 for network preferences
     * flag=3 for service class preferences table
     * flag=4 for zone preferences table
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_preferenceCacheVO
     *            PreferenceCacheVO
     * @param p_flag
     *            int
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isRecordModified(Connection p_con, PreferenceCacheVO p_preferenceCacheVO, int p_flag) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_preferenceCacheVO = ");
        	msg.append(p_preferenceCacheVO);
        	msg.append(", p_flag = ");
        	msg.append(p_flag);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final StringBuilder sqlRecordModified = new StringBuilder();
        try {
            if (p_flag == 1)// for system preferences
            {
                sqlRecordModified.append("SELECT modified_on FROM system_preferences ");
                sqlRecordModified.append("WHERE preference_code=?");
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "QUERY=" + sqlRecordModified);
                }
                final String query = sqlRecordModified.toString();
                pstmtSelect = p_con.prepareStatement(query);
                pstmtSelect.setString(1, p_preferenceCacheVO.getPreferenceCode());
            } else if (p_flag == 2) // for network preferences
            {
                sqlRecordModified.append("SELECT modified_on FROM network_preferences ");
                sqlRecordModified.append("WHERE preference_code=? AND network_code=? ");
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "QUERY=" + sqlRecordModified);
                }
                final String query = sqlRecordModified.toString();
                pstmtSelect = p_con.prepareStatement(query);
                pstmtSelect.setString(1, p_preferenceCacheVO.getPreferenceCode());
                pstmtSelect.setString(2, p_preferenceCacheVO.getNetworkCode());
            } else if (p_flag == 3) // for service class preferences
            {
                sqlRecordModified.append("SELECT modified_on FROM service_class_preferences ");
                sqlRecordModified.append("WHERE preference_code=? AND network_code=? AND service_class_id=? ");
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "QUERY=" + sqlRecordModified);
                }
                final String query = sqlRecordModified.toString();
                pstmtSelect = p_con.prepareStatement(query);
                pstmtSelect.setString(1, p_preferenceCacheVO.getPreferenceCode());
                pstmtSelect.setString(2, p_preferenceCacheVO.getNetworkCode());
                pstmtSelect.setString(3, p_preferenceCacheVO.getServiceCode());
            } else if (p_flag == 4) // for zone preferences
            {
                sqlRecordModified.append("SELECT modified_on FROM control_preferences ");
                sqlRecordModified.append("WHERE preference_code=? AND network_code=? AND control_code=? ");
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "QUERY=" + sqlRecordModified);
                }
                final String query = sqlRecordModified.toString();
                pstmtSelect = p_con.prepareStatement(query);
                pstmtSelect.setString(1, p_preferenceCacheVO.getPreferenceCode());
                pstmtSelect.setString(2, p_preferenceCacheVO.getNetworkCode());
                pstmtSelect.setString(3, p_preferenceCacheVO.getControlCode());
            }
            Timestamp newlastModified = null;
            if (p_preferenceCacheVO.getLastModifiedTime() == 0) {
                return false;
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            if (newlastModified.getTime() != p_preferenceCacheVO.getLastModifiedTime()) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[isRecordModified]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[isRecordModified]", "", "", "",
                "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exititng:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method loadNetworkPreferenceData.
     * This method is to load the data of the type NETWORK PREFERENCE and only
     * those records which
     * are allowed to display i.e. display='Y'.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadNetworkPreferenceData(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadNetworkPreferenceData";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: networkCode=" + p_networkCode);
        }
        PreferenceCacheVO preferenceCacheVO = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList preferenceList = new ArrayList();
        try {
        	PreferenceWebQry preferenceWebQry = (PreferenceWebQry)ObjectProducer.getObject(QueryConstants.PREFERENCE_WEB_QRY, QueryConstants.QUERY_PRODUCER);
            String selectQuery = preferenceWebQry.loadNetworkPreferenceDataQry();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, PreferenceI.NETWORK_LEVEL);
            rs = pstmtSelect.executeQuery();
            String allowedValues[] = null;
            int i = 0;
            final ArrayList valueTypeList = LookupsCache.loadLookupDropDown(PretupsI.PREFERENCE_VALUE_TYPE, true);
            final ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            ArrayList allowedValuesList = null;
            while (rs.next()) {
                preferenceCacheVO = new PreferenceCacheVO();
                final String vlaue = rs.getString("value");
                // to get the values form child table or from parent table
                // depending on the existance of the data.
                if (vlaue == null) {
                    preferenceCacheVO.setLastModifiedTime(rs.getTimestamp("s_modified_on").getTime());
                    preferenceCacheVO.setValue(rs.getString("default_value"));
                } else {
                    preferenceCacheVO.setLastModifiedTime(rs.getTimestamp("n_modified_on").getTime());
                    preferenceCacheVO.setValue(rs.getString("value"));
                }
                preferenceCacheVO.setModuleCode(rs.getString("module"));
                if (preferenceCacheVO.getModuleCode().equals(PretupsI.ALL)) {
                    preferenceCacheVO.setModuleDescription(PretupsI.ALL);
                } else {

                    preferenceCacheVO.setModuleDescription((BTSLUtil.getOptionDesc(preferenceCacheVO.getModuleCode(), moduleList)).getLabel());
                }

                preferenceCacheVO.setNetworkCode(p_networkCode);
                preferenceCacheVO.setPreferenceCode(rs.getString("preference_code"));
                preferenceCacheVO.setPrefrenceName(rs.getString("name"));
                preferenceCacheVO.setValueType(rs.getString("value_type"));

                preferenceCacheVO.setValueTypeDesc(((ListValueVO) BTSLUtil.getOptionDesc(preferenceCacheVO.getValueType(), valueTypeList)).getLabel());
                if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                    preferenceCacheVO.setValue(PretupsBL.getDisplayAmount(Long.parseLong(preferenceCacheVO.getValue())));
                } else {
                    preferenceCacheVO.setValue(preferenceCacheVO.getValue());
                }

                preferenceCacheVO.setMinValue(rs.getString("min_value"));
                preferenceCacheVO.setMaxValue(rs.getString("max_value"));
                preferenceCacheVO.setMaxSize(rs.getString("max_size"));
                preferenceCacheVO.setDescription(rs.getString("description"));
                preferenceCacheVO.setModifiedAllowed(rs.getString("modified_allowed"));
                if (preferenceCacheVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_YES)) {
                    preferenceCacheVO.setDisableAllow(false);
                } else if (preferenceCacheVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_NO)) {
                    preferenceCacheVO.setDisableAllow(true);
                }

                preferenceCacheVO.setFixedValue(rs.getString("fixed_value"));
                preferenceCacheVO.setAllowedValues(rs.getString("allowed_values"));
                if (!BTSLUtil.isNullString(preferenceCacheVO.getFixedValue()) && PretupsI.NO.equals(preferenceCacheVO.getFixedValue()) && PretupsI.YES
                    .equals(preferenceCacheVO.getModifiedAllowed())) {
                    allowedValues = preferenceCacheVO.getAllowedValues().split(",");
                    allowedValuesList = new ArrayList();
                    for (i = 0; i < allowedValues.length; i++) {
                        allowedValuesList.add(new ListValueVO(allowedValues[i], allowedValues[i]));
                    }
                    preferenceCacheVO.setAllowedValuesList(allowedValuesList);
                } else {
                    preferenceCacheVO.setAllowedValuesList(null);
                }

                preferenceCacheVO.setAllowAction("N");// for default selection
                // of the radio button
                preferenceList.add(preferenceCacheVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadNetworkPreferenceData]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadNetworkPreferenceData]", "", "", "",
                "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting:preferenceList size =" + preferenceList.size());
            }
        }
        return preferenceList;
    }

    /**
     * Method updateNetworkPreference.
     * This method is to update the record of the NETWORK PREFERENCE
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_preferenceList
     *            ArrayList
     * @param p_date
     *            Date
     * @param p_userID
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int updateNetworkPreference(Connection p_con, ArrayList p_preferenceList, Date p_date, String p_userID) throws BTSLBaseException {
        final String methodName = "updateNetworkPreference";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_preferenceList.size() = ");
        	msg.append(p_preferenceList.size());
        	msg.append(", p_date = ");
        	msg.append(p_date);
        	msg.append(", p_userID = ");
        	msg.append(p_userID);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate1 = null;
        PreparedStatement pstmtUpdate2 = null;
        PreparedStatement pstmtUpdate3 = null;
        ResultSet rs = null;
        int updateCount = 0;
        try {
            final String selectQuery = "SELECT 1 FROM network_preferences WHERE network_code = ? AND preference_code=? ";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "SelectQUERY=" + selectQuery);
            }
            final StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("UPDATE network_preferences SET value=?,modified_by=?,modified_on=? ");
            updateQuery.append("WHERE preference_code=? AND network_code=? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "updateQuery=" + updateQuery);
            }
            final StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO network_preferences (network_code,preference_code,");
            insertQuery.append("value,created_on,created_by,modified_on,modified_by)");
            insertQuery.append(" VALUES (?,?,?,?,?,?,?)");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "InsertQuery=" + insertQuery);
            }
            final StringBuilder deleteQuery = new StringBuilder();
            deleteQuery.append("DELETE FROM network_preferences ");
            deleteQuery.append("WHERE preference_code=? AND network_code=? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "deleteQuery=" + deleteQuery);
            }
            pstmtUpdate = p_con.prepareStatement(selectQuery);
            pstmtUpdate1 = p_con.prepareStatement(updateQuery.toString());
            pstmtUpdate2 = p_con.prepareStatement(deleteQuery.toString());
            pstmtUpdate3 = p_con.prepareStatement(insertQuery.toString());
            PreferenceCacheVO preferenceCacheVO = null;
            int preferenceListSize = p_preferenceList.size();
            for (int i = 0; i < preferenceListSize; i++) {
                preferenceCacheVO = (PreferenceCacheVO) p_preferenceList.get(i);
                pstmtUpdate.setString(1, preferenceCacheVO.getNetworkCode());
                pstmtUpdate.setString(2, preferenceCacheVO.getPreferenceCode());
                rs = pstmtUpdate.executeQuery();
                if (rs.next()) {
                    if ("M".equals(preferenceCacheVO.getAllowAction())) {
                        if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                            pstmtUpdate1.setString(1, String.valueOf(PretupsBL.getSystemAmount(preferenceCacheVO.getValue())));
                        } else {
                            pstmtUpdate1.setString(1, preferenceCacheVO.getValue().trim());
                        }
                        pstmtUpdate1.setString(2, p_userID);
                        pstmtUpdate1.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_date));
                        pstmtUpdate1.setString(4, preferenceCacheVO.getPreferenceCode());
                        pstmtUpdate1.setString(5, preferenceCacheVO.getNetworkCode());
                    } else if ("D".equals(preferenceCacheVO.getAllowAction())) {
                        pstmtUpdate2.setString(1, preferenceCacheVO.getPreferenceCode());
                        pstmtUpdate2.setString(2, preferenceCacheVO.getNetworkCode());
                    }
                    // for checking that is the record is modified during the
                    // transaction.
                    final boolean modified = isRecordModified(p_con, preferenceCacheVO, 2);
                    if (modified) {
                        throw new BTSLBaseException(this, methodName, "error.modify.true");
                    }
                    if ("M".equals(preferenceCacheVO.getAllowAction())) {
                        updateCount += pstmtUpdate1.executeUpdate();
                    } else if ("D".equals(preferenceCacheVO.getAllowAction())) {
                        updateCount += pstmtUpdate2.executeUpdate();
                    }
                    
                } else if ("M".equals(preferenceCacheVO.getAllowAction())) {
                    pstmtUpdate3.setString(1, preferenceCacheVO.getNetworkCode());
                    pstmtUpdate3.setString(2, preferenceCacheVO.getPreferenceCode());
                    if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                        pstmtUpdate3.setString(3, String.valueOf(PretupsBL.getSystemAmount(preferenceCacheVO.getValue())));
                    } else {
                        pstmtUpdate3.setString(3, preferenceCacheVO.getValue().trim());
                    }
                    pstmtUpdate3.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_date));
                    pstmtUpdate3.setString(5, p_userID);
                    pstmtUpdate3.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_date));
                    pstmtUpdate3.setString(7, p_userID);
                    updateCount += pstmtUpdate3.executeUpdate();
                } else if ("D".equals(preferenceCacheVO.getAllowAction())) {
                    updateCount++;
                }
                pstmtUpdate.clearParameters();
                pstmtUpdate1.clearParameters();
                pstmtUpdate2.clearParameters();
                pstmtUpdate3.clearParameters();
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[updateNetworkPreference]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[updateNetworkPreference]", "", "", "",
                "Exception:" + e.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate1 != null) {
                    pstmtUpdate1.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate2 != null) {
                    pstmtUpdate2.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate3 != null) {
                    pstmtUpdate3.close();
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
     * Method loadServiceClassPreferenceData.
     * This method is to load the data of the type SERVICE CLASS PREFERENCE and
     * only those records which
     * are allowed to display i.e. display='Y'.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_serviceClass
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceClassPreferenceData(Connection p_con, String p_networkCode, String p_serviceClass) throws BTSLBaseException {
        final String methodName = "loadServiceClassPreferenceData";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: networkCode = ");
        	msg.append(p_networkCode);
        	msg.append(", ServiceClass = ");
        	msg.append(p_serviceClass);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreferenceCacheVO preferenceCacheVO = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList preferenceList = new ArrayList();
        try {
        	PreferenceWebQry preferenceWebQry = (PreferenceWebQry)ObjectProducer.getObject(QueryConstants.PREFERENCE_WEB_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect= preferenceWebQry.loadServiceClassPreferenceDataQry(p_con,p_networkCode,p_serviceClass);
            
            rs = pstmtSelect.executeQuery();
            final ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            final ArrayList valueTypeList = LookupsCache.loadLookupDropDown(PretupsI.PREFERENCE_VALUE_TYPE, true);
            ArrayList allowedValuesList = null;
            String allowedValues[] = null;
            int i = 0;

            while (rs.next()) {
                preferenceCacheVO = new PreferenceCacheVO();
                final String vlaue = rs.getString("value");
                // load data either form parent table or from child table
                // depending on the existance of the data in the child table.
                if (vlaue == null) {
                    preferenceCacheVO.setLastModifiedTime(rs.getTimestamp("sp_modified_on").getTime());
                    preferenceCacheVO.setValue(rs.getString("default_value"));
                } else {
                    preferenceCacheVO.setLastModifiedTime(rs.getTimestamp("scp_modified_on").getTime());
                    preferenceCacheVO.setValue(rs.getString("value"));
                }
                preferenceCacheVO.setServiceCode(p_serviceClass);
                preferenceCacheVO.setModuleCode(rs.getString("module"));
                if (preferenceCacheVO.getModuleCode().equals(PretupsI.ALL)) {
                    preferenceCacheVO.setModuleDescription(PretupsI.ALL);
                } else {

                    preferenceCacheVO.setModuleDescription((BTSLUtil.getOptionDesc(preferenceCacheVO.getModuleCode(), moduleList)).getLabel());
                }
                preferenceCacheVO.setNetworkCode(p_networkCode);
                preferenceCacheVO.setPreferenceCode(rs.getString("preference_code"));
                preferenceCacheVO.setPrefrenceName(rs.getString("name"));
                preferenceCacheVO.setValueType(rs.getString("value_type"));

                preferenceCacheVO.setValueTypeDesc(((ListValueVO) BTSLUtil.getOptionDesc(preferenceCacheVO.getValueType(), valueTypeList)).getLabel());
                if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                    preferenceCacheVO.setValue(PretupsBL.getDisplayAmount(Long.parseLong(preferenceCacheVO.getValue())));
                } else {
                    preferenceCacheVO.setValue(preferenceCacheVO.getValue());
                }

                preferenceCacheVO.setMinValue(rs.getString("min_value"));
                preferenceCacheVO.setMaxValue(rs.getString("max_value"));
                preferenceCacheVO.setMaxSize(rs.getString("max_size"));
                preferenceCacheVO.setDescription(rs.getString("description"));
                preferenceCacheVO.setModifiedAllowed(rs.getString("modified_allowed"));
                if (preferenceCacheVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_YES)) {
                    preferenceCacheVO.setDisableAllow(false);
                } else if (preferenceCacheVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_NO)) {
                    preferenceCacheVO.setDisableAllow(true);
                }

                preferenceCacheVO.setFixedValue(rs.getString("fixed_value"));
                preferenceCacheVO.setAllowedValues(rs.getString("allowed_values"));
                if (!BTSLUtil.isNullString(preferenceCacheVO.getFixedValue()) && PretupsI.NO.equals(preferenceCacheVO.getFixedValue()) && PretupsI.YES
                    .equals(preferenceCacheVO.getModifiedAllowed())) {
                    allowedValues = preferenceCacheVO.getAllowedValues().split(",");
                    allowedValuesList = new ArrayList();
                    for (i = 0; i < allowedValues.length; i++) {
                        allowedValuesList.add(new ListValueVO(allowedValues[i], allowedValues[i]));
                    }
                    preferenceCacheVO.setAllowedValuesList(allowedValuesList);
                } else {
                    preferenceCacheVO.setAllowedValuesList(null);
                }

                preferenceCacheVO.setAllowAction("N");// for default selection
                // of the radio button
                preferenceList.add(preferenceCacheVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServiceClassPreferenceData]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServiceClassPreferenceData]", "",
                "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting:preferenceList size =" + preferenceList.size());
            }
        }
        return preferenceList;
    }

    /**
     * Method updateServiceClassPreference.
     * This method is to update the record of the SERVICE CLASS PREFERENCE
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_preferenceList
     *            ArrayList
     * @param p_date
     *            Date
     * @param p_userID
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int updateServiceClassPreference(Connection p_con, ArrayList p_preferenceList, Date p_date, String p_userID) throws BTSLBaseException {
        final String methodName = "updateServiceClassPreference";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_preferenceList.size() = ");
        	msg.append(p_preferenceList.size());
        	msg.append(", p_date = ");
        	msg.append(p_date);
        	msg.append(", p_userID = ");
        	msg.append(p_userID);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate1 = null;
        PreparedStatement pstmtUpdate2 = null;
        PreparedStatement pstmtUpdate3 = null;
        ResultSet rs = null;
        int updateCount = 0;
        try {
            final String selectQuery = "SELECT 1 FROM service_class_preferences WHERE network_code = ? AND preference_code=? AND service_class_id=? ";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "SelectQUERY=" + selectQuery);
            }
            final StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("UPDATE service_class_preferences SET value=?,modified_by=?,modified_on=? ");
            updateQuery.append("WHERE preference_code=? AND network_code=? AND service_class_id=?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "updateQuery=" + updateQuery);
            }
            final StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO service_class_preferences (module,network_code,service_class_id,");
            insertQuery.append("preference_code,value,created_on,created_by,modified_on,modified_by)");
            insertQuery.append("VALUES (?,?,?,?,?,?,?,?,?)");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "InsertQuery=" + insertQuery);
            }
            final StringBuilder deleteQuery = new StringBuilder();
            deleteQuery.append("DELETE FROM service_class_preferences ");
            deleteQuery.append("WHERE preference_code=? AND network_code=? AND service_class_id=?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "deleteQuery=" + deleteQuery);
            }
            pstmtUpdate = p_con.prepareStatement(selectQuery);
            pstmtUpdate1 = p_con.prepareStatement(updateQuery.toString());
            pstmtUpdate2 = p_con.prepareStatement(deleteQuery.toString());
            pstmtUpdate3 = p_con.prepareStatement(insertQuery.toString());
            PreferenceCacheVO preferenceCacheVO = null;
            int preferenceListSize = p_preferenceList.size();
            for (int i = 0; i < preferenceListSize; i++) {
                preferenceCacheVO = (PreferenceCacheVO) p_preferenceList.get(i);
                pstmtUpdate.setString(1, preferenceCacheVO.getNetworkCode());
                pstmtUpdate.setString(2, preferenceCacheVO.getPreferenceCode());
                pstmtUpdate.setString(3, preferenceCacheVO.getServiceCode());
                rs = pstmtUpdate.executeQuery();
                if (rs.next()) {
                    if ("M".equals(preferenceCacheVO.getAllowAction())) {
                        if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                            pstmtUpdate1.setString(1, String.valueOf(PretupsBL.getSystemAmount(preferenceCacheVO.getValue())));
                        } else {
                            pstmtUpdate1.setString(1, preferenceCacheVO.getValue().trim());
                        }
                        pstmtUpdate1.setString(2, p_userID);
                        pstmtUpdate1.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_date));
                        pstmtUpdate1.setString(4, preferenceCacheVO.getPreferenceCode());
                        pstmtUpdate1.setString(5, preferenceCacheVO.getNetworkCode());
                        pstmtUpdate1.setString(6, preferenceCacheVO.getServiceCode());
                    } else if ("D".equals(preferenceCacheVO.getAllowAction())) {
                        pstmtUpdate2.setString(1, preferenceCacheVO.getPreferenceCode());
                        pstmtUpdate2.setString(2, preferenceCacheVO.getNetworkCode());
                        pstmtUpdate2.setString(3, preferenceCacheVO.getServiceCode());
                    }
                    // for checking that is the record is modified during the
                    // transaction.
                    final boolean modified = isRecordModified(p_con, preferenceCacheVO, 3);
                    if (modified) {
                        throw new BTSLBaseException(this, methodName, "error.modify.true");
                    }
                    
                    if ("M".equals(preferenceCacheVO.getAllowAction())) {
                        updateCount += pstmtUpdate1.executeUpdate();
                    } else if ("D".equals(preferenceCacheVO.getAllowAction())) {
                        updateCount += pstmtUpdate2.executeUpdate();
                    }
                    
                } else if ("M".equals(preferenceCacheVO.getAllowAction())) {
                    pstmtUpdate3.setString(1, preferenceCacheVO.getModuleCode());
                    pstmtUpdate3.setString(2, preferenceCacheVO.getNetworkCode());
                    pstmtUpdate3.setString(3, preferenceCacheVO.getServiceCode());
                    pstmtUpdate3.setString(4, preferenceCacheVO.getPreferenceCode());

                    if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                        pstmtUpdate3.setString(5, String.valueOf(PretupsBL.getSystemAmount(preferenceCacheVO.getValue())));
                    } else {
                        pstmtUpdate3.setString(5, preferenceCacheVO.getValue().trim());
                    }
                    pstmtUpdate3.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_date));
                    pstmtUpdate3.setString(7, p_userID);
                    pstmtUpdate3.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_date));
                    pstmtUpdate3.setString(9, p_userID);
                    updateCount += pstmtUpdate3.executeUpdate();
                } else if ("D".equals(preferenceCacheVO.getAllowAction())) {
                    updateCount++;
                }
                pstmtUpdate.clearParameters();
                pstmtUpdate1.clearParameters();
                pstmtUpdate2.clearParameters();
                pstmtUpdate3.clearParameters();
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[updateServiceClassPreference]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[updateServiceClassPreference]", "", "",
                "", "Exception:" + e.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate1 != null) {
                    pstmtUpdate1.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }

            try {
                if (pstmtUpdate2 != null) {
                    pstmtUpdate2.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }

            try {
                if (pstmtUpdate3 != null) {
                    pstmtUpdate3.close();
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
     * Method loadControlUnitList.
     * This method is to load the list of the Zones of the specified network
     * code.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadControlUnitList(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadControlUnitList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkCode=" + p_networkCode);
        }
        final ArrayList zoneList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT  GRPHDOMAIN.grph_domain_name name,GRPHDOMAIN.grph_domain_code code ");
            selectQuery.append("FROM geographical_domain_types GRPHTYP,geographical_domains GRPHDOMAIN ");
            selectQuery.append("WHERE GRPHTYP.controlling_unit='Y' AND GRPHDOMAIN.network_code=? ");
            selectQuery.append("AND GRPHDOMAIN.grph_domain_type=GRPHTYP.grph_domain_type AND status <> 'N' ");
            selectQuery.append("ORDER BY name ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_networkCode);
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("code"));
                zoneList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadControlUnitList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadControlUnitList]", "", "", "",
                "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting:list size=" + zoneList.size());
            }
        }
        return zoneList;
    }

    /**
     * Method loadControlUnitPreferenceData.
     * This method is to load the data of the type ZONE PREFERENCE and only
     * those records which
     * are allowed to display i.e. display='Y'.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_zoneCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadControlUnitPreferenceData(Connection p_con, String p_zoneCode) throws BTSLBaseException {
        final String methodName = "loadControlUnitPreferenceData";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_zoneCode=" + p_zoneCode);
        }
        PreferenceCacheVO preferenceCacheVO = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList preferenceList = new ArrayList();
        try {

        	PreferenceWebQry preferenceWebQry = (PreferenceWebQry)ObjectProducer.getObject(QueryConstants.PREFERENCE_WEB_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect= preferenceWebQry.loadControlUnitPreferenceDataQry(p_con, p_zoneCode);
        	
            rs = pstmtSelect.executeQuery();
            final ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            final ArrayList valueTypeList = LookupsCache.loadLookupDropDown(PretupsI.PREFERENCE_VALUE_TYPE, true);
            ArrayList allowedValuesList = null;
            String allowedValues[] = null;
            int i = 0;

            while (rs.next()) {
                preferenceCacheVO = new PreferenceCacheVO();
                final String vlaue = rs.getString("value");
                // load data either form parent table or from child table
                // depending on the existance of the data in the child table.
                if (vlaue == null) {
                    preferenceCacheVO.setLastModifiedTime(rs.getTimestamp("sp_modified_on").getTime());
                    preferenceCacheVO.setValue(rs.getString("default_value"));
                } else {
                    preferenceCacheVO.setLastModifiedTime(rs.getTimestamp("CP_modified_on").getTime());
                    preferenceCacheVO.setValue(rs.getString("value"));
                }
                preferenceCacheVO.setControlCode(p_zoneCode);
                preferenceCacheVO.setModuleCode(rs.getString("module"));
                if (preferenceCacheVO.getModuleCode().equals(PretupsI.ALL)) {
                    preferenceCacheVO.setModuleDescription(PretupsI.ALL);
                } else {
                    preferenceCacheVO.setModuleDescription((BTSLUtil.getOptionDesc(preferenceCacheVO.getModuleCode(), moduleList)).getLabel());
                }
                preferenceCacheVO.setNetworkCode(rs.getString("network_code"));
                preferenceCacheVO.setPreferenceCode(rs.getString("preference_code"));
                preferenceCacheVO.setPrefrenceName(rs.getString("name"));
                preferenceCacheVO.setValueType(rs.getString("value_type"));

                preferenceCacheVO.setValueTypeDesc(((ListValueVO) BTSLUtil.getOptionDesc(preferenceCacheVO.getValueType(), valueTypeList)).getLabel());
                if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                    preferenceCacheVO.setValue(PretupsBL.getDisplayAmount(Long.parseLong(preferenceCacheVO.getValue())));
                } else {
                    preferenceCacheVO.setValue(preferenceCacheVO.getValue());
                }

                preferenceCacheVO.setMinValue(rs.getString("min_value"));
                preferenceCacheVO.setMaxValue(rs.getString("max_value"));
                preferenceCacheVO.setMaxSize(rs.getString("max_size"));
                preferenceCacheVO.setDescription(rs.getString("description"));
                preferenceCacheVO.setModifiedAllowed(rs.getString("modified_allowed"));
                if (preferenceCacheVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_YES)) {
                    preferenceCacheVO.setDisableAllow(false);
                } else if (preferenceCacheVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_NO)) {
                    preferenceCacheVO.setDisableAllow(true);
                }

                preferenceCacheVO.setFixedValue(rs.getString("fixed_value"));
                preferenceCacheVO.setAllowedValues(rs.getString("allowed_values"));
                if (!BTSLUtil.isNullString(preferenceCacheVO.getFixedValue()) && PretupsI.NO.equals(preferenceCacheVO.getFixedValue()) && PretupsI.YES
                    .equals(preferenceCacheVO.getModifiedAllowed())) {
                    allowedValues = preferenceCacheVO.getAllowedValues().split(",");
                    allowedValuesList = new ArrayList();
                    for (i = 0; i < allowedValues.length; i++) {
                        allowedValuesList.add(new ListValueVO(allowedValues[i], allowedValues[i]));
                    }
                    preferenceCacheVO.setAllowedValuesList(allowedValuesList);
                } else {
                    preferenceCacheVO.setAllowedValuesList(null);
                }

                preferenceCacheVO.setAllowAction("N");// for default selection
                // of the radio button
                preferenceList.add(preferenceCacheVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadControlUnitPreferenceData]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadControlUnitPreferenceData]", "", "",
                "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting:preferenceList size =" + preferenceList.size());
            }
        }
        return preferenceList;
    }

    /**
     * Method updateControlUnitPreference.
     * This method is to update the record of the ZONE PREFERENCE
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_preferenceList
     *            ArrayList
     * @param p_date
     *            Date
     * @param p_userID
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int updateControlUnitPreference(Connection p_con, ArrayList p_preferenceList, Date p_date, String p_userID) throws BTSLBaseException {
        final String methodName = "updateControlUnitPreference";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_preferenceList.size() = ");
        	msg.append(p_preferenceList.size());
        	msg.append(", p_date = ");
        	msg.append(p_date);
        	msg.append(", p_userID = ");
        	msg.append(p_userID);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate1 = null;
        PreparedStatement pstmtUpdate2 = null;
        PreparedStatement pstmtUpdate3 = null;
        ResultSet rs = null;
        int updateCount = 0;
        try {
            final String selectQuery = "SELECT 1 FROM control_preferences WHERE preference_code=?  AND control_code = ? ";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "SelectQUERY=" + selectQuery);
            }
            final StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("UPDATE control_preferences SET value=?,modified_by=?,modified_on=? ");
            updateQuery.append("WHERE preference_code=? AND network_code=? AND control_code=? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "updateQuery=" + updateQuery);
            }
            final StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO control_preferences (control_code,network_code,");
            insertQuery.append("preference_code,value,created_on,created_by,modified_on,modified_by)");
            insertQuery.append("VALUES (?,?,?,?,?,?,?,?)");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "InsertQuery=" + insertQuery);
            }
            final StringBuilder deleteQuery = new StringBuilder();
            deleteQuery.append("DELETE FROM control_preferences ");
            deleteQuery.append("WHERE preference_code=? AND network_code=? AND control_code=? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "deleteQuery=" + deleteQuery);
            }
            pstmtUpdate = p_con.prepareStatement(selectQuery);
            pstmtUpdate1 = p_con.prepareStatement(updateQuery.toString());
            pstmtUpdate2 = p_con.prepareStatement(deleteQuery.toString());
            pstmtUpdate3 = p_con.prepareStatement(insertQuery.toString());
            PreferenceCacheVO preferenceCacheVO = null;
            int preferenceListSize = p_preferenceList.size();
            for (int i = 0; i < preferenceListSize; i++) {
                preferenceCacheVO = (PreferenceCacheVO) p_preferenceList.get(i);
                pstmtUpdate.setString(1, preferenceCacheVO.getPreferenceCode());
                pstmtUpdate.setString(2, preferenceCacheVO.getControlCode());
                rs = pstmtUpdate.executeQuery();
                if (rs.next()) {
                    if ("M".equals(preferenceCacheVO.getAllowAction())) {
                        if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                            pstmtUpdate1.setString(1, String.valueOf(PretupsBL.getSystemAmount(preferenceCacheVO.getValue())));
                        } else {
                            pstmtUpdate1.setString(1, preferenceCacheVO.getValue().trim());
                        }
                        pstmtUpdate1.setString(2, p_userID);
                        pstmtUpdate1.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_date));
                        pstmtUpdate1.setString(4, preferenceCacheVO.getPreferenceCode());
                        pstmtUpdate1.setString(5, preferenceCacheVO.getNetworkCode());
                        pstmtUpdate1.setString(6, preferenceCacheVO.getControlCode());
                        updateCount += pstmtUpdate1.executeUpdate();
                    } else if ("D".equals(preferenceCacheVO.getAllowAction())) {
                        pstmtUpdate2.setString(1, preferenceCacheVO.getPreferenceCode());
                        pstmtUpdate2.setString(2, preferenceCacheVO.getNetworkCode());
                        pstmtUpdate2.setString(3, preferenceCacheVO.getControlCode());
                        updateCount += pstmtUpdate2.executeUpdate();
                    }
                    // for checking that is the record is modified during the
                    // transaction.
                    final boolean modified = isRecordModified(p_con, preferenceCacheVO, 4);
                    if (modified) {
                        throw new BTSLBaseException(this, methodName, "error.modify.true");
                    }
                    
                } else if ("M".equals(preferenceCacheVO.getAllowAction())) {
                    pstmtUpdate3.setString(1, preferenceCacheVO.getControlCode());
                    pstmtUpdate3.setString(2, preferenceCacheVO.getNetworkCode());
                    pstmtUpdate3.setString(3, preferenceCacheVO.getPreferenceCode());

                    if (PreferenceI.TYPE_AMOUNT.equals(preferenceCacheVO.getValueType())) {
                        pstmtUpdate3.setString(4, String.valueOf(PretupsBL.getSystemAmount(preferenceCacheVO.getValue())));
                    } else {
                        pstmtUpdate3.setString(4, preferenceCacheVO.getValue().trim());
                    }
                    pstmtUpdate3.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_date));
                    pstmtUpdate3.setString(6, p_userID);
                    pstmtUpdate3.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_date));
                    pstmtUpdate3.setString(8, p_userID);
                    updateCount += pstmtUpdate3.executeUpdate();
                } else if ("D".equals(preferenceCacheVO.getAllowAction())) {
                    updateCount++;
                }
                pstmtUpdate.clearParameters();
                pstmtUpdate1.clearParameters();
                pstmtUpdate2.clearParameters();
                pstmtUpdate3.clearParameters();
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[updateControlUnitPreference]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[updateControlUnitPreference]", "", "",
                "", "Exception:" + e.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate1 != null) {
                    pstmtUpdate1.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate2 != null) {
                    pstmtUpdate2.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate3 != null) {
                    pstmtUpdate3.close();
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
     * Method loadNetworkPreferenceData.
     * This method is to load the data of the type NETWORK PREFERENCE and only
     * those records which
     * are allowed to display i.e. display='Y'.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_preferenceType
     *            String
     * @param p_preferenceCode
     *            String
     * @param p_networkCode
     *            String
     * @return PreferenceCacheVO
     * @throws BTSLBaseException
     */
    public PreferenceCacheVO loadNetworkPreferenceData(Connection p_con, String p_preferenceCode, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadNetworkPreferenceData";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: preferenceCode = ");
        	msg.append(p_preferenceCode);
        	msg.append(", networkCode = ");
        	msg.append(p_networkCode);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreferenceCacheVO preferenceCacheVO = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
         
        	PreferenceWebQry preferenceWebQry = (PreferenceWebQry)ObjectProducer.getObject(QueryConstants.PREFERENCE_WEB_QRY, QueryConstants.QUERY_PRODUCER);
        	String selectQuery= preferenceWebQry.loadNetworkPreferenceDataQuery();
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_preferenceCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                preferenceCacheVO = new PreferenceCacheVO();
                final String vlaue = rs.getString("value");
                // to get the values form child table or from parent table
                // depending on the existance of the data.
                if (vlaue == null) {
                    preferenceCacheVO.setLastModifiedTime(rs.getTimestamp("s_modified_on").getTime());
                    preferenceCacheVO.setValue(rs.getString("default_value"));
                } else {
                    preferenceCacheVO.setLastModifiedTime(rs.getTimestamp("n_modified_on").getTime());
                    preferenceCacheVO.setValue(rs.getString("value"));
                }
                preferenceCacheVO.setModuleCode(rs.getString("module"));
                preferenceCacheVO.setPreferenceCode(rs.getString("preference_code"));
                preferenceCacheVO.setPrefrenceName(rs.getString("name"));
                preferenceCacheVO.setValueType(rs.getString("value_type"));
                preferenceCacheVO.setMinValue(rs.getString("min_value"));
                preferenceCacheVO.setMaxValue(rs.getString("max_value"));
                preferenceCacheVO.setMaxSize(rs.getString("max_size"));
                preferenceCacheVO.setDescription(rs.getString("description"));
                preferenceCacheVO.setModifiedAllowed(rs.getString("modified_allowed"));
                preferenceCacheVO.setFixedValue(rs.getString("fixed_value"));
                preferenceCacheVO.setAllowedValues(rs.getString("allowed_values"));
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadNetworkPreferenceData]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadNetworkPreferenceData]", "", "", "",
                "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting:systemPreferenceVO=" + preferenceCacheVO);
            }
        }
        return preferenceCacheVO;
    }
}
