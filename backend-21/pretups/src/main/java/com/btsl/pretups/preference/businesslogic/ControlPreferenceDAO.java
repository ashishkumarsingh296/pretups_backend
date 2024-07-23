/**
 * @(#)ControlPreferenceDAO.java
 *                               Copyright(c) 2005, Bharti Telesoft Ltd.
 *                               All Rights Reserved
 * 
 *                               <description>
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               shishupal.singh Feb 23, 2007 Initital Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 * 
 */

package com.btsl.pretups.preference.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.web.pretups.preference.businesslogic.PreferenceWebDAO;
/**
 * @author shishupal.singh
 * 
 */
public class ControlPreferenceDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadControlPreferenceData.
     * This method is to load the data of the type CONTROL PREFERENCE and only
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
    public ArrayList loadControlPreferenceData(Connection p_con, String p_controlType, String p_module, String p_preferenceCode, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadControlPreferenceData", "Entered: p_controlType=" + p_controlType + ", p_module=" + p_module + ", p_networkCode=" + p_networkCode);
        }
        final String METHOD_NAME = "loadControlPreferenceData";
        ControlPreferenceVO controlPreferenceVO = null;
        PreparedStatement pstmtSelectCategory = null;
        ResultSet rsCtrlType = null;
        PreparedStatement pstmtContPrefSelect = null;
        ResultSet contPref_rs = null;
        PreparedStatement pstmtSysPrefSelect = null;
        ResultSet rs = null;
        ArrayList preferenceList = new ArrayList();
        try {
            StringBuilder selectCtrlTypeQuery = new StringBuilder();
            if (PretupsI.LOOKUP_TYPE_CONTROL_CAT.equals(p_controlType)) {
                selectCtrlTypeQuery.append("SELECT category_code,category_name, domain_code FROM categories ");
                if(PreferenceI.TWO_FA_REQ.equals(p_preferenceCode)){
                	selectCtrlTypeQuery.append("WHERE category_code NOT IN('SUADM','OPT','MONTR') and domain_code='OPT' and STATUS <>'N' ORDER BY domain_code ");
                }
                else{
                selectCtrlTypeQuery.append("WHERE category_code NOT IN('SUADM','OPT') and STATUS <>'N' ORDER BY domain_code ");
                }
            } else if (PretupsI.LOOKUP_TYPE_CONTROL_INTERFACE.equals(p_controlType)) {
                selectCtrlTypeQuery.append("SELECT I.interface_id,I.interface_description FROM interfaces I,interface_network_mapping INM ");
                selectCtrlTypeQuery.append("WHERE I.interface_id=INM.interface_id AND INM.network_code='" + p_networkCode + "' AND I.status<>'N' ");
            } else if (PretupsI.LOOKUP_TYPE_CONTROL_ZONE.equals(p_controlType)) {
                selectCtrlTypeQuery.append("SELECT GD.grph_domain_code, GD.grph_domain_name FROM geographical_domains GD, geographical_domain_types GDT ");
                selectCtrlTypeQuery.append("WHERE GD.grph_domain_type=GDT.grph_domain_type AND GD.network_code='" + p_networkCode + "' AND GD.status<>'N' AND GDT.grph_domain_type='ZO' ");
            } else if (PretupsI.LOOKUP_TYPE_CONTROL_SERVICE_TYPE.equals(p_controlType)) {
                
                selectCtrlTypeQuery.append("SELECT DISTINCT ST.service_type, ST.name FROM service_type ST, network_services NS WHERE NS.module_code=ST.module AND ST.service_type=NS.service_type ");
                selectCtrlTypeQuery.append("AND NS.status<>'N' AND ST.status<>'N' AND ST.module='" + p_module + "' AND NS.sender_network='" + p_networkCode + "'");

            }

            StringBuilder selectContPrefQuery = new StringBuilder();
            selectContPrefQuery.append("SELECT control_code,network_code,preference_code,value,created_on,created_by, ");
            selectContPrefQuery.append("modified_on,modified_by,type from control_preferences WHERE ");
            selectContPrefQuery.append("control_code = ? AND preference_code = ? AND network_code = ? ");

            StringBuilder selectSysPrefQuery = new StringBuilder();
            selectSysPrefQuery.append("SELECT module, preference_code,name,value_type,default_value,min_value, ");
            selectSysPrefQuery.append("max_value,max_size,modified_allowed,description,modified_on,modified_by, ");
            selectSysPrefQuery.append("allowed_values, fixed_value,type FROM system_preferences  ");
            selectSysPrefQuery.append("WHERE display='Y' AND type=? AND module = ? AND preference_code = ? ");

            if (_log.isDebugEnabled()) {
                _log.debug("loadControlPreferenceData", "Query=" + selectCtrlTypeQuery);
            }
            pstmtSelectCategory = p_con.prepareStatement(selectCtrlTypeQuery.toString());
            rsCtrlType = pstmtSelectCategory.executeQuery();
            while (rsCtrlType.next()) {

                if (_log.isDebugEnabled()) {
                    _log.debug("loadControlPreferenceData", "Query=" + selectSysPrefQuery);
                }
                pstmtSysPrefSelect = p_con.prepareStatement(selectSysPrefQuery.toString());
                pstmtSysPrefSelect.setString(1, p_controlType);
                pstmtSysPrefSelect.setString(2, p_module);
                pstmtSysPrefSelect.setString(3, p_preferenceCode);
                rs = pstmtSysPrefSelect.executeQuery();
                String allowedValues[] = null;
                int i = 0;
                ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
                ArrayList valueTypeList = LookupsCache.loadLookupDropDown(PretupsI.PREFERENCE_VALUE_TYPE, true);
                ArrayList allowedValuesList = null;
                if (rs.next()) {
                    controlPreferenceVO = new ControlPreferenceVO();
                    controlPreferenceVO.setCategoryCode(rsCtrlType.getString(1));
                    controlPreferenceVO.setCategoryName(rsCtrlType.getString(2));

                    controlPreferenceVO.setModuleCode(rs.getString("module"));
                    controlPreferenceVO.setModuleDescription((BTSLUtil.getOptionDesc(controlPreferenceVO.getModuleCode(), moduleList)).getLabel());

                    controlPreferenceVO.setPreferenceCode(rs.getString("preference_code"));
                    controlPreferenceVO.setPrefrenceName(rs.getString("name"));
                    controlPreferenceVO.setValueType(rs.getString("value_type"));

                    if (_log.isDebugEnabled()) {
                        _log.debug("loadControlPreferenceData", "Query=" + selectContPrefQuery);
                    }
                    pstmtContPrefSelect = p_con.prepareStatement(selectContPrefQuery.toString());
                    pstmtContPrefSelect.setString(1, controlPreferenceVO.getCategoryCode());
                    pstmtContPrefSelect.setString(2, p_preferenceCode);
                    pstmtContPrefSelect.setString(3, p_networkCode);
                    contPref_rs = pstmtContPrefSelect.executeQuery();
                    if (contPref_rs.next()) {
                        controlPreferenceVO.setValue(contPref_rs.getString("value"));
                        controlPreferenceVO.setLastModifiedTime(contPref_rs.getTimestamp("modified_on").getTime());
                        controlPreferenceVO.setModifiedBy(contPref_rs.getString("modified_by"));
                    } else {
                        controlPreferenceVO.setValue(rs.getString("default_value"));
                        controlPreferenceVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                        controlPreferenceVO.setModifiedBy(rs.getString("modified_by"));
                    }
                    pstmtContPrefSelect.clearParameters();
                    controlPreferenceVO.setValueTypeDesc((BTSLUtil.getOptionDesc(controlPreferenceVO.getValueType(), valueTypeList)).getLabel());
                    if (PreferenceI.TYPE_AMOUNT.equals(controlPreferenceVO.getValueType())) {
                        controlPreferenceVO.setValue(PretupsBL.getDisplayAmount(Long.parseLong(controlPreferenceVO.getValue())));
                    } else {
                        controlPreferenceVO.setValue(controlPreferenceVO.getValue());
                    }
                    controlPreferenceVO.setMinValue(rs.getString("min_value"));
                    controlPreferenceVO.setMaxValue(rs.getString("max_value"));
                    controlPreferenceVO.setMaxSize(rs.getString("max_size"));
                    controlPreferenceVO.setDescription(rs.getString("description"));
                    controlPreferenceVO.setModifiedAllowed(rs.getString("modified_allowed"));
                    if (controlPreferenceVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_YES)) {
                        controlPreferenceVO.setDisableAllow(false);
                    } else if (controlPreferenceVO.getModifiedAllowed().equalsIgnoreCase(PretupsI.MODIFY_ALLOWED_NO)) {
                        controlPreferenceVO.setDisableAllow(true);
                    }
                    controlPreferenceVO.setFixedValue(rs.getString("fixed_value"));
                    controlPreferenceVO.setAllowedValues(rs.getString("allowed_values"));
                    controlPreferenceVO.setType(rs.getString("type"));
                    if (!BTSLUtil.isNullString(controlPreferenceVO.getFixedValue()) && PretupsI.NO.equals(controlPreferenceVO.getFixedValue()) && PretupsI.YES.equals(controlPreferenceVO.getModifiedAllowed())) {
                        allowedValues = controlPreferenceVO.getAllowedValues().split(",");
                        allowedValuesList = new ArrayList();
                        for (i = 0; i < allowedValues.length; i++) {
                            allowedValuesList.add(new ListValueVO(allowedValues[i], allowedValues[i]));
                        }
                        controlPreferenceVO.setAllowedValuesList(allowedValuesList);
                    } else {
                        controlPreferenceVO.setAllowedValuesList(null);
                    }

                    controlPreferenceVO.setAllowAction("N");// for default
                                                            // selection of the
                                                            // radio button
                    preferenceList.add(controlPreferenceVO);
                }
                pstmtSysPrefSelect.clearParameters();
            }
        } catch (SQLException sqe) {
            _log.error("loadControlPreferenceData", "SQLException:" + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ControlPreferenceDAO[loadControlPreferenceData]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadControlPreferenceData", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadControlPreferenceData", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ControlPreferenceDAO[loadControlPreferenceData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadControlPreferenceData", "error.general.processing");
        } finally {
        	 try{
        	        if (contPref_rs!= null){
        	        	contPref_rs.close();
        	        }
        	      }
        	      catch (SQLException e){
        	    	  _log.error("An error occurred closing result set.", e);
        	      }
        	try{
        		if (pstmtContPrefSelect!= null){
        			pstmtContPrefSelect.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	
        	try{
        		if (pstmtSysPrefSelect!= null){
        			pstmtSysPrefSelect.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (rsCtrlType!= null){
            		rsCtrlType.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
        	try{
        		if (pstmtSelectCategory!= null){
        			pstmtSelectCategory.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        if (_log.isDebugEnabled()) {
                _log.debug("loadControlPreferenceData", "Exiting:preferenceList size=" + preferenceList.size());
            }
        }
        return preferenceList;
    }

    /**
     * Method updateControlPreference.
     * This method is to update the record of the CONTROL PREFERENCE of the
     * specified preferenceCode.
     * 
     * @author shishupal.singh
     * @param p_con
     *            Connection
     * @param p_controlPreferenceList
     *            ArrayList
     * @param p_date
     *            Date
     * @param p_userID
     *            String
     * @param p_networkCode
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int updateControlPreference(Connection p_con, ArrayList p_controlPreferenceList, Date p_date, String p_userID, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateControlPreference", "Entered:p_preferenceList.size()=" + p_controlPreferenceList.size() + ",p_date=" + p_date + ",p_userID=" + p_userID + ",p_networkCode=" + p_networkCode);
        }
        final String METHOD_NAME = "updateControlPreference";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtDelete = null;
        PreparedStatement pstmtInsert = null;
        PreferenceWebDAO preferencewebDAO = null;
        int updateCount = 0;
        try {
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT PREFERENCE_CODE FROM control_preferences ");
            selectQuery.append("WHERE preference_code = ? AND control_code = ? AND network_code = ? ");
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
           

            StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO control_preferences (control_code, network_code, preference_code, ");
            insertQuery.append(" value, created_on, created_by, modified_on, modified_by, type) VALUES(?,?,?,?,?,?,?,?,?)");
            pstmtInsert = p_con.prepareStatement(insertQuery.toString());

            StringBuilder deleteQuery = new StringBuilder();
            deleteQuery.append("DELETE FROM control_preferences ");
            deleteQuery.append("WHERE preference_code = ? AND control_code = ? AND network_code = ? ");
            pstmtDelete = p_con.prepareStatement(deleteQuery.toString());

            preferencewebDAO = new PreferenceWebDAO();
            ControlPreferenceVO controlPreferenceVO = null;
            Iterator itr = p_controlPreferenceList.iterator();
            int updatedcount = 0;
            while (itr.hasNext()) {
            	//Added by Lalit to fix bug DEF840
            	 StringBuilder updateQuery = new StringBuilder();
                 updateQuery.append("UPDATE control_preferences SET value = ? , modified_by = ? , modified_on = ? ");
                 updateQuery.append("WHERE preference_code = ? AND control_code = ? ");
                 
                controlPreferenceVO = (ControlPreferenceVO) itr.next();
                controlPreferenceVO.setControlCode(controlPreferenceVO.getCategoryCode());
                controlPreferenceVO.setNetworkCode(p_networkCode);
                if (_log.isDebugEnabled()) {
                    _log.debug("updateControlPreference", "selectQuery=" + selectQuery);
                }
                pstmtSelect.setString(1, controlPreferenceVO.getPreferenceCode());
                pstmtSelect.setString(2, controlPreferenceVO.getCategoryCode());
                pstmtSelect.setString(3, controlPreferenceVO.getNetworkCode());
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    if (!(rs.getString("PREFERENCE_CODE").equalsIgnoreCase(PreferenceI.INTRFC_MAX_NODES))) {
                        updateQuery.append(" AND network_code = ? ");

                    }

                    pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
                    if ("M".equals(controlPreferenceVO.getAllowAction())) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("updateControlPreference", "updateQuery = " + updateQuery);
                        }
                        if (PreferenceI.TYPE_AMOUNT.equals(controlPreferenceVO.getValueType())) {
                            pstmtUpdate.setString(1, String.valueOf(PretupsBL.getSystemAmount(controlPreferenceVO.getValue())));
                        } else {
                            pstmtUpdate.setString(1, controlPreferenceVO.getValue().trim());
                        }
                        pstmtUpdate.setString(2, p_userID);
                        pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_date));
                        pstmtUpdate.setString(4, controlPreferenceVO.getPreferenceCode());
                        pstmtUpdate.setString(5, controlPreferenceVO.getCategoryCode());
                        if (!(rs.getString("PREFERENCE_CODE").equalsIgnoreCase(PreferenceI.INTRFC_MAX_NODES))) {
                            pstmtUpdate.setString(6, p_networkCode);
                        }
                        // ////////////for the checking is the record be
                        // modified during the transaction.
                        boolean modified = preferencewebDAO.isRecordModified(p_con, controlPreferenceVO, 4);
                        if (modified) {
                            throw new BTSLBaseException(this, "updateControlPreference", "error.modify.true");
                        }
                        updateCount += pstmtUpdate.executeUpdate();
                        if (rs.getString("PREFERENCE_CODE").equalsIgnoreCase(PreferenceI.INTRFC_MAX_NODES)) {
                            updateCount = ++updatedcount;
                        }
                        try{
                    		if (pstmtUpdate!= null){
                    			pstmtUpdate.close();
                    		}
                    	}
                    	catch (SQLException e){
                    		_log.error("An error occurred closing result set.", e);
                    	}
                        
                    } else if ("D".equals(controlPreferenceVO.getAllowAction())) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("updateControlPreference", "deleteQuery=" + deleteQuery);
                        }
                        pstmtDelete.setString(1, controlPreferenceVO.getPreferenceCode());
                        pstmtDelete.setString(2, controlPreferenceVO.getCategoryCode());
                        pstmtDelete.setString(3, controlPreferenceVO.getNetworkCode());
                        // ////////////for the checking is the record be
                        // modified during the transaction.
                        boolean modified = preferencewebDAO.isRecordModified(p_con, controlPreferenceVO, 4);
                        if (modified) {
                            throw new BTSLBaseException(this, "updateControlPreference", "error.modify.true");
                        }
                        updateCount += pstmtDelete.executeUpdate();
                        pstmtDelete.clearParameters();
                    }
                } else {
                    if ("M".equals(controlPreferenceVO.getAllowAction())) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("updateControlPreference", "insertQuery=" + insertQuery);
                        }
                        pstmtInsert.setString(1, controlPreferenceVO.getCategoryCode());
                        pstmtInsert.setString(2, p_networkCode);
                        pstmtInsert.setString(3, controlPreferenceVO.getPreferenceCode());
                        if (PreferenceI.TYPE_AMOUNT.equals(controlPreferenceVO.getValueType())) {
                            pstmtInsert.setString(4, String.valueOf(PretupsBL.getSystemAmount(controlPreferenceVO.getValue())));
                        } else {
                            pstmtInsert.setString(4, controlPreferenceVO.getValue().trim());
                        }
                        pstmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_date));
                        pstmtInsert.setString(6, p_userID);
                        pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_date));
                        pstmtInsert.setString(8, p_userID);
                        pstmtInsert.setString(9, controlPreferenceVO.getType());
                        updateCount += pstmtInsert.executeUpdate();
                        pstmtInsert.clearParameters();
                    } else if ("D".equals(controlPreferenceVO.getAllowAction())) {
                        updateCount++;
                    }
                }
                pstmtSelect.clearParameters();
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error("updateControlPreference", "SQLException:" + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ControlPreferenceDAO[updateControlPreference]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateControlPreference", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updateControlPreference", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ControlPreferenceDAO[updateControlPreference]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateControlPreference", "error.general.processing");
        } finally {
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtUpdate!= null){
        			pstmtUpdate.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
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
                _log.debug("updateControlPreference", "Exiting:return=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method getOptionDesc.
     * This method is to load the data of the type SYSTEM PREFERENCE and only
     * those records which
     * are allowed to display i.e. display='Y'.
     * 
     * @author shishupal.singh
     * @param pcode
     *            String
     * @param plist
     *            ArrayList
     * @return PreferenceCacheVO
     */
    public PreferenceCacheVO getOptionDesc(String pcode, ArrayList plist) {

        if (_log.isDebugEnabled()) {
            _log.debug("getOptionDesc", "Entered: p_code=" + pcode + " p_list size =" + plist.size());
        }
        PreferenceCacheVO vo = null;
        boolean flag = false;
        if (plist != null && plist.size() > 0) {
            for (int i = 0, j = plist.size(); i < j; i++) {
                vo = (PreferenceCacheVO) plist.get(i);
                if (vo.getPreferenceCode().equalsIgnoreCase(pcode)) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            vo = new PreferenceCacheVO();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getOptionDesc", "Exited: vo=" + vo.toString());
        }
        return vo;
    }

}
