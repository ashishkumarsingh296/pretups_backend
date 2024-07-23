package com.web.ota.services.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
import com.btsl.ota.services.businesslogic.ServiceSetVO;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.ota.services.businesslogic.SimServiceCategoriesVO;
import com.btsl.ota.services.businesslogic.SmsVO;
import com.btsl.ota.services.businesslogic.UserServicesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class ServicesWebDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private static ServicesWebQry servicesWebQry;
    private static final String EXCEPTION = "Exception :";
    private static final String SQL_EXCEPTION = "SQL Exception :";
    private static final String QUERY_KEY = "Query =";

    /**
     * Constructor for ServicesWebDAO.
     */
    public ServicesWebDAO() {
        super();
        servicesWebQry = (ServicesWebQry) ObjectProducer
				.getObject(QueryConstants.SERVICES_WEB_QRY,
						QueryConstants.QUERY_PRODUCER);
    }

    /**
     * This method is used to load latest minor version corresponding to the
     * service
     * 
     * @param p_con
     *            of Connection type
     * @param p_serviceID
     *            of String type
     * @param p_majorVersion
     *            of String type
     * @return String
     * @exception BTSLBaseException
     */
    public String loadLatestMinorVersion(Connection p_con, String p_serviceID, String p_majorVersion) throws BTSLBaseException {
        final String methodName = "loadLatestMinorVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: serviceID=");
        	loggerValue.append(p_serviceID);
        	loggerValue.append(",p_majorVersion=");
        	loggerValue.append(p_majorVersion);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String minorVersion = null;
        try {
        	final String sqlLoadBuf = servicesWebQry.loadLatestMinorVersionQry();
        	if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf);
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf);
            dbPs.setString(1, p_serviceID.trim());
            dbPs.setString(2, p_majorVersion.trim());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                minorVersion = rs.getString("maxvalue");
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLatestMinorVersion]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLatestMinorVersion]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: minorVersion:");
            	loggerValue.append(minorVersion);
            	_log.debug(methodName, loggerValue);
            }
        }
        return minorVersion;
    }

    /**
     * This method is used to load latest major version corresponding to the
     * service
     * 
     * @param p_con
     *            of Connection type
     * @param p_serviceID
     *            of String type
     * @return String
     * @exception BTSLBaseException
     */
    public String loadLatestMajorVersion(Connection p_con, String p_serviceID) throws BTSLBaseException {
        final String methodName = "loadLatestMajorVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: serviceID=");
        	loggerValue.append(p_serviceID);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String majorVersion = null;
        try {
        	final String sqlLoadBuf = servicesWebQry.loadLatestMajorVersionQry();
        	if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf);
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf);
            dbPs.setString(1, p_serviceID.trim());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                majorVersion = rs.getString("maxvalue");
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLatestMajorVersion]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLatestMajorVersion]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: majorVersion:");
            	loggerValue.append(majorVersion);
            	_log.debug(methodName, loggerValue);
            }
        }
        return majorVersion;
    }

    /**
     * This method is used to load offset and length of User SIM Services
     * 
     * @param p_con
     *            of Connection type
     * @param p_type
     *            of String type
     * @param p_profile
     *            of String type
     * @param p_networkCode
     *            of String type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList loadOffsetLengthUserSIMServiceList(Connection p_con, String p_type, String p_profile, String p_networkCode, String p_simProfileID) throws BTSLBaseException {
        final String methodName = "loadOffsetLengthUserSIMServiceList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_type=");
        	loggerValue.append(p_type);
        	loggerValue.append(",p_profile=");
        	loggerValue.append(p_profile);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_simProfileID=");
        	loggerValue.append(p_simProfileID);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        final ArrayList serviceList = new ArrayList();
        UserServicesVO userServicesVO = null;
        try {

            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT USS.offset,SS.length FROM user_sim_services USS,");
            sqlLoadBuf.append(" sim_services SS,networks L	WHERE USS.service_id=SS.service_id AND USS.major_version=SS.major_version ");
            sqlLoadBuf.append(" AND USS.minor_version=SS.minor_version  AND L.service_set_id=SS.service_set_id AND USS.user_type=? ");
            sqlLoadBuf.append(" AND USS.profile=? AND USS.network_code=? AND USS.sim_profile_id=? AND USS.network_code=L.network_code AND USS.present_status=? ORDER BY USS.offset ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_type);
            dbPs.setString(2, p_profile);
            dbPs.setString(3, p_networkCode);
            dbPs.setString(4, p_simProfileID);
            dbPs.setString(5, PretupsI.DATE_CHECK_CURRENT);
            rs = dbPs.executeQuery();
            while (rs.next()) {
                userServicesVO = new UserServicesVO();
                userServicesVO.setOffset(rs.getLong("offset"));
                userServicesVO.setLength(rs.getLong("length"));
                serviceList.add(userServicesVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadOffsetLengthUserSIMServiceList]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadOffsetLengthUserSIMServiceList]",
                "", "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: serviceList size:");
            	loggerValue.append(serviceList.size());
            	_log.debug(methodName, loggerValue);
            }
        }
        return serviceList;
    }

    /**
     * Method updateUserSIMService
     * This method is used to update User SIM Services
     * 
     * @param p_con
     *            of Connection type
     * @param p_userServiceVO
     *            of UserServicesVO type
     * @return returns the boolean
     * @exception BtslBaseException
     */

    public boolean updateUserSIMService(Connection p_con, UserServicesVO p_userServiceVO) throws BTSLBaseException {

        final String methodName = "updateUserSIMService";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: VO=");
        	loggerValue.append(p_userServiceVO);
        	_log.debug(methodName, loggerValue);
        }


        PreparedStatement dbPs = null;
        PreparedStatement dbPs1 = null;
        PreparedStatement dbPs2 = null;
        PreparedStatement dbPs3 = null;
        ResultSet rs = null;
        boolean isUpdate = false;
        try {
            StringBuilder sqlLoadBuf = new StringBuilder("DELETE FROM  user_sim_services WHERE user_type = ? ");
            sqlLoadBuf.append(" AND network_code = ? AND user_type=? AND profile=? AND position = ? ");
            sqlLoadBuf.append(" AND present_status='PREVIOUS' AND sim_profile_id = ? ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_userServiceVO.getUserType());
            dbPs.setString(2, p_userServiceVO.getLocationCode());
            dbPs.setString(3, p_userServiceVO.getUserType());
            dbPs.setString(4, p_userServiceVO.getProfile());
            dbPs.setInt(5, p_userServiceVO.getPosition());
            dbPs.setString(6, p_userServiceVO.getSimProfileId());
            int updateCount = dbPs.executeUpdate();// here select stmt will be
            // written to check whether
            // record exits or not

            sqlLoadBuf = new StringBuilder("SELECT  position , service_id FROM user_sim_services WHERE user_type = ? ");
            sqlLoadBuf.append(" AND network_code = ? AND user_type=? AND profile=? AND position = ? AND present_status='CURRENT' ");
            sqlLoadBuf.append(" AND sim_profile_id = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query=" + sqlLoadBuf.toString());
            }

            dbPs1 = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs1.setString(1, p_userServiceVO.getUserType());
            dbPs1.setString(2, p_userServiceVO.getLocationCode());
            dbPs1.setString(3, p_userServiceVO.getUserType());
            dbPs1.setString(4, p_userServiceVO.getProfile());
            dbPs1.setInt(5, p_userServiceVO.getPosition());
            dbPs1.setString(6, p_userServiceVO.getSimProfileId());
            rs = dbPs1.executeQuery();
            if (rs.next()) {
                sqlLoadBuf = new StringBuilder("UPDATE  user_sim_services SET present_status='PREVIOUS',modified_by=?, modified_on=? WHERE user_type = ? ");
                sqlLoadBuf.append(" AND network_code = ? AND user_type=? AND profile=? AND position = ? AND present_status='CURRENT' ");
                sqlLoadBuf.append(" AND sim_profile_id = ? ");
                if(_log.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(sqlLoadBuf.toString());
        			_log.debug(methodName, loggerValue);
        		}

                dbPs2 = p_con.prepareStatement(sqlLoadBuf.toString());
                dbPs2.setString(1, p_userServiceVO.getCreatedBy());
                dbPs2.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_userServiceVO.getCreatedOn()));
                dbPs2.setString(3, p_userServiceVO.getUserType());
                dbPs2.setString(4, p_userServiceVO.getLocationCode());
                dbPs2.setString(5, p_userServiceVO.getUserType());
                dbPs2.setString(6, p_userServiceVO.getProfile());
                dbPs2.setInt(7, p_userServiceVO.getPosition());
                dbPs2.setString(8, p_userServiceVO.getSimProfileId());
                updateCount = dbPs2.executeUpdate();
            } else {
                updateCount = 1;
            }
            if (updateCount > 0) {
                updateCount = 0;
                sqlLoadBuf = new StringBuilder("INSERT INTO user_sim_services (user_type,position,network_code,service_id ,");
                sqlLoadBuf.append(" major_version,minor_version,status,profile,\"offset\",created_by,created_on,sim_profile_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Select Query=" + sqlLoadBuf.toString());
                }

                dbPs3 = p_con.prepareStatement(sqlLoadBuf.toString());
                dbPs3.setString(1, p_userServiceVO.getUserType());
                dbPs3.setInt(2, p_userServiceVO.getPosition());
                dbPs3.setString(3, p_userServiceVO.getLocationCode());
                dbPs3.setString(4, p_userServiceVO.getNewServiceID());
                dbPs3.setString(5, p_userServiceVO.getNewMajorVersion());
                dbPs3.setString(6, p_userServiceVO.getNewMinorVersion());
                dbPs3.setString(7, p_userServiceVO.getStatus());
                dbPs3.setString(8, p_userServiceVO.getProfile());
                dbPs3.setLong(9, p_userServiceVO.getOffset());
                dbPs3.setString(10, p_userServiceVO.getCreatedBy());
                dbPs3.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_userServiceVO.getCreatedOn()));
                dbPs3.setString(12, p_userServiceVO.getSimProfileId());
                updateCount = dbPs3.executeUpdate();
                if (updateCount > 0) {
                    isUpdate = true;
                }
            } else {
                throw new BTSLBaseException("updateuserssimservices.unabletoupdate");
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[updateUserSIMService]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[updateUserSIMService]", "", "", "",
                loggerValue.toString());
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
            try {
                if (dbPs1 != null) {
                    dbPs1.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs2 != null) {
                    dbPs2.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs3 != null) {
                    dbPs3.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: isUpdate:");
            	loggerValue.append(isUpdate);
            	_log.debug(methodName, loggerValue);
            }
        }
        return isUpdate;
    }

    /**
     * This method is used to load a SIM Service
     * 
     * @param p_con
     *            of Connection type
     * @param p_serviceID
     *            of String type
     * @param p_majorVersion
     *            of String type
     * @param p_minorVersion
     *            of String type
     * @return returns the ServicesVO
     * @exception BTSLBaseException
     */

    public ServicesVO loadSIMService(Connection p_con, String p_serviceSetID, String p_serviceID, String p_majorVersion, String p_minorVersion) throws BTSLBaseException {
        final String methodName = "loadSIMService";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_serviceSetID=");
        	loggerValue.append(p_serviceSetID);
        	loggerValue.append(",p_serviceID=");
        	loggerValue.append(p_serviceID);
        	loggerValue.append(",p_majorVersion=");
        	loggerValue.append(p_majorVersion);
        	loggerValue.append(",p_minorVersion=");
        	loggerValue.append(p_minorVersion);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        ServicesVO servicesVO = null;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT SS.wml,SS.bytecode,SS.status,SS.allowed_to FROM sim_services SS ");
            sqlLoadBuf.append(" WHERE SS.service_set_id=? AND SS.service_id=? AND SS.major_version=? AND SS.minor_version=?");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_serviceSetID);
            dbPs.setString(2, p_serviceID);
            dbPs.setString(3, p_majorVersion);
            dbPs.setString(4, p_minorVersion);
            rs = dbPs.executeQuery();
            if (rs.next()) {
                servicesVO = new ServicesVO();
                servicesVO.setWml(rs.getString("wml"));
                servicesVO.setByteCode(rs.getString("bytecode"));
                servicesVO.setStatus(rs.getString("status"));
                servicesVO.setAllowedToUsers(BTSLUtil.NullToString(rs.getString("allowed_to")));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSIMService]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSIMService]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: servicesVO:");
            	loggerValue.append(servicesVO);
            	_log.debug(methodName, loggerValue);
            }
        }
        return servicesVO;
    }

    /**
     * Method loadSIMServiceSet.
     * This method is used to load Service Set
     * 
     * @param p_con
     *            Connection
     * @return serviceSetList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadSIMServiceSet(Connection p_con) throws BTSLBaseException {

        final String methodName = "loadSIMServiceSet";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        final ArrayList serviceSetList = new ArrayList();
        ServiceSetVO serviceSetVO = null;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT name,id,lang_1,lang_2 ");
            sqlLoadBuf.append(" FROM service_set WHERE ( status='Y' OR status='y' OR ");
            sqlLoadBuf.append(" status IS NULL ) ORDER BY name ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            rs = dbPs.executeQuery();
            while (rs.next()) {
                serviceSetVO = new ServiceSetVO();
                serviceSetVO.setName(rs.getString("name"));
                serviceSetVO.setLanguage1(rs.getString("lang_1"));
                serviceSetVO.setLanguage2(rs.getString("lang_2"));
                serviceSetVO.setId(rs.getString("id"));
                serviceSetList.add(serviceSetVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSIMServiceSet]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, "loadSIMServiceList", "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSIMServiceSet]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: serviceSetList.size():");
            	loggerValue.append(serviceSetList.size());
            	_log.debug(methodName, loggerValue);
            }
        }
        return serviceSetList;
    }

    /**
     * This method is used to load Next Service ID
     * 
     * @param p_con
     *            of Connection type
     * @param p_serviceSetID
     *            of String type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public int loadNextServiceID(Connection p_con, String p_serviceSetID) throws BTSLBaseException {
        final String methodName = "loadSIMServiceID";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: serviceSetID=");
        	loggerValue.append(p_serviceSetID);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        PreparedStatement dbPs1 = null;
        ResultSet rs = null;
        int serviceID = 0;
        try {
            StringBuilder sqlLoadBuf = new StringBuilder("SELECT last_service_id FROM service_set WHERE id=? AND ( status='Y'  OR status IS NULL )");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_serviceSetID);
            rs = dbPs.executeQuery();
            String serviceIDStr = null;
            if (rs.next()) {
                serviceIDStr = rs.getString("last_service_id");
                if (serviceIDStr == null) {
                    serviceID = 0;
                } else {
                    serviceID = Integer.parseInt(serviceIDStr) + 1;
                }
            }
            sqlLoadBuf = new StringBuilder("UPDATE service_set SET last_service_id =?  WHERE id=?");

            dbPs1 = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs1.setInt(1, serviceID);
            dbPs1.setString(2, p_serviceSetID);
            dbPs1.executeUpdate();
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSIMServiceSet]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, "loadSIMServiceList", "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSIMServiceSet]", "", "", "",
                loggerValue.toString());
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
            try {
                if (dbPs1 != null) {
                    dbPs1.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: serviceID:");
            	loggerValue.append(serviceID);
            	_log.debug(methodName, loggerValue);
            }
        }
        return serviceID;
    }

    /**
     * This method is used to Insert User SIM Services
     * 
     * @param p_con
     *            of Connection type
     * @param p_serviceVO
     *            of ServicesVO type
     * @return returns the int
     * @exception BTSLBaseException
     */

    public int addSIMService(Connection con, ServicesVO serviceVO) throws BTSLBaseException {
        final String methodName = "addSIMService";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement dbPs = null;
        int updateCount = 0;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("INSERT INTO SIM_SERVICES");
            sqlLoadBuf.append("(service_set_id,service_id,major_version,minor_version,status,");
            sqlLoadBuf.append("label1,label2,description,wml,bytecode,user_type,created_by,created_on,length,allowed_to,modified_on,modified_by)");
            sqlLoadBuf.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, serviceVO.getServiceSetID());
            dbPs.setString(2, serviceVO.getServiceID());
            dbPs.setString(3, serviceVO.getMajorVersion());
            dbPs.setString(4, serviceVO.getMinorVersion());
            dbPs.setString(5, serviceVO.getStatus());
            dbPs.setString(6, serviceVO.getLabel1());
            dbPs.setString(7, serviceVO.getLabel2());
            dbPs.setString(8, serviceVO.getDescription());
            dbPs.setString(9, serviceVO.getWml());
            dbPs.setString(10, serviceVO.getByteCode());
            dbPs.setString(11, serviceVO.getUserType());
            dbPs.setString(12, serviceVO.getCreatedBy());
            dbPs.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(serviceVO.getCreatedOn()));
            dbPs.setLong(14, serviceVO.getLength());
            dbPs.setString(15, serviceVO.getAllowedToUsers());
            // Added By Amit
            dbPs.setTimestamp(16, BTSLUtil.getTimestampFromUtilDate(serviceVO.getModifiedOn()));
            dbPs.setString(17, serviceVO.getModifiedBy());

            updateCount = dbPs.executeUpdate();
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[addSIMService]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[addSIMService]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	_log.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }

    /**
     * This method is used to Insert User SIM Service as Draft
     * 
     * @param p_con
     *            of Connection type
     * @param p_serviceVO
     *            of ServicesVO type
     * @return returns the int
     * @exception BTSLBaseException
     */

    public int addSIMServiceAsDraft(Connection p_con, ServicesVO p_serviceVO) throws BTSLBaseException {

        final String methodName = "addSIMServiceAsDraft";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: serviceid=");
        	loggerValue.append(p_serviceVO.getServiceID());
        	loggerValue.append(",Major version=");
        	loggerValue.append(p_serviceVO.getMajorVersion());
        	loggerValue.append(",Minor Version=");
        	loggerValue.append(p_serviceVO.getMinorVersion());
        	_log.debug(methodName, loggerValue);
        }


        PreparedStatement dbPs = null;
        PreparedStatement dbPs1 = null;
        int updateCount = 0;
        try {
            StringBuilder sqlLoadBuf = new StringBuilder("DELETE FROM  sim_services WHERE service_id = ? ");
            sqlLoadBuf.append(" AND major_version = ? AND minor_version = ? ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs1 = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs1.setString(1, p_serviceVO.getServiceID());
            dbPs1.setString(2, p_serviceVO.getMajorVersion());
            dbPs1.setString(3, p_serviceVO.getMinorVersion());
            updateCount = dbPs1.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Delete count=" + updateCount);
            }
            sqlLoadBuf = new StringBuilder("INSERT INTO SIM_SERVICES");
            sqlLoadBuf.append("(service_set_id,service_id,major_version,minor_version,status,");
            sqlLoadBuf.append("label1,label2,description,wml,bytecode,user_type,created_by,created_on,length,allowed_to,modified_on,modified_by)");
            sqlLoadBuf.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query= " + sqlLoadBuf.toString());
            }

            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_serviceVO.getServiceSetID());
            dbPs.setString(2, p_serviceVO.getServiceID());
            dbPs.setString(3, p_serviceVO.getMajorVersion());
            dbPs.setString(4, p_serviceVO.getMinorVersion());
            dbPs.setString(5, p_serviceVO.getStatus());
            dbPs.setString(6, p_serviceVO.getLabel1());
            dbPs.setString(7, p_serviceVO.getLabel2());
            dbPs.setString(8, p_serviceVO.getDescription());
            dbPs.setString(9, p_serviceVO.getWml());
            dbPs.setString(10, p_serviceVO.getByteCode());
            dbPs.setString(11, p_serviceVO.getUserType());
            dbPs.setString(12, p_serviceVO.getCreatedBy());
            dbPs.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(p_serviceVO.getCreatedOn()));
            dbPs.setLong(14, p_serviceVO.getLength());
            dbPs.setString(15, p_serviceVO.getAllowedToUsers());

            dbPs.setTimestamp(16, BTSLUtil.getTimestampFromUtilDate(p_serviceVO.getModifiedOn()));
            dbPs.setString(17, p_serviceVO.getModifiedBy());
            updateCount = dbPs.executeUpdate();
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[addSIMServiceAsDraft]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[addSIMServiceAsDraft]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs1 != null) {
                    dbPs1.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	_log.debug(methodName, loggerValue);            }
        }
        return updateCount;
    }

    /**
     * This method is used to load SIM Profile list
     * 
     * @param p_con
     *            of Connection type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList getMasterSimProfileList(Connection p_con) throws BTSLBaseException {
        final String methodName = "getMasterSimProfileList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        final ArrayList serviceList = new ArrayList();
        ListValueVO listValueVO = null;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT sim_id ,sim_profile_name FROM sim_profile ");
            sqlLoadBuf.append(" WHERE (status='Y' OR status IS NULL) ORDER BY sim_profile_name ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            rs = dbPs.executeQuery();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("sim_profile_name"), rs.getString("sim_id"));
                serviceList.add(listValueVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[getMasterSimProfileList]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, "addSIMServiceAsDraft", "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[getMasterSimProfileList]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: serviceList.size():");
            	loggerValue.append(serviceList.size());
            	_log.debug(methodName, loggerValue);
            }
        }
        return serviceList;
    }

    /**
     * This method is used to user SIM services under a given user type,
     * location code, profile type and sim profile type.Only the latest
     * and 2nd latest are loade and then they are compared in another
     * function as to which version is changed
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
     *            of String type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList loadUserSIMServiceProfileList(Connection p_con, String p_type, String p_profile, String p_networkCode, String p_simProfileId) throws BTSLBaseException {
        final String methodName = "loadUserSIMServiceProfileList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_type=");
        	loggerValue.append(p_type);
        	loggerValue.append(",p_profile=");
        	loggerValue.append(p_profile);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_simProfileId=");
        	loggerValue.append(p_simProfileId);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        final ArrayList serviceList = new ArrayList();
        UserServicesVO userServicesVO = null;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder(
                "SELECT SS.service_set_id serSetID,USS.position, USS.service_id, USS.major_version, USS.minor_version,USS.offset,");
            sqlLoadBuf.append(" USS.status, USS.network_code,L.network_name ,SS.description,SS.label1,SS.label2,SS.length,SS.bytecode ");
            sqlLoadBuf.append(" FROM user_sim_services USS,networks L,sim_services SS");
            sqlLoadBuf.append(" WHERE USS.user_type = ? AND USS.profile = ? AND USS.network_code = ? AND USS.sim_profile_id=? AND USS.network_code=L.network_code ");
            sqlLoadBuf.append(" AND USS.service_id=SS.service_id AND USS.major_version=SS.major_version ");
            sqlLoadBuf.append(" AND SS.service_set_id=L.service_set_id AND (present_status='CURRENT' OR present_status='PREVIOUS') ");
            sqlLoadBuf.append(" AND USS.minor_version=SS.minor_version order by USS.position ,USS.service_id,USS.modified_on desc");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
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
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadUserSIMServiceProfileList]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadUserSIMServiceProfileList]", "",
                "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: serviceList.size():");
            	loggerValue.append(serviceList.size());
            	_log.debug(methodName, loggerValue);
            }
        }
        return serviceList;
    }

    /**
     * This method is used to load SMSC related paramters under a location ,
     * only the latest and 2nd latest are loaded and then they are compared in
     * another
     * function as to which parameter is changed
     * 
     * @param p_con
     *            of Connection type
     * @param p_type
     *            of String type
     * @param p_networkCode
     *            of String type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList loadSmscDetails(Connection con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadSmscDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode=");
        	loggerValue.append(p_networkCode);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        final ArrayList smscList = new ArrayList();
        SmsVO smsVO = null;
        try {
        	final String sqlLoadBuf = servicesWebQry.loadSmscDetailsQry();
        	if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = con.prepareStatement(sqlLoadBuf);
            dbPs.setString(1, p_networkCode);
            dbPs.setString(2, p_networkCode);
            rs = dbPs.executeQuery();
            while (rs.next()) {
                smsVO = new SmsVO();
                smsVO.setSmscId(rs.getString("sms_param_id"));
                smsVO.setSmsc1(rs.getString("smsc1"));
                smsVO.setSmsc2(rs.getString("smsc2"));
                smsVO.setSmsc3(rs.getString("smsc3"));
                smsVO.setPort1(rs.getString("port1"));
                smsVO.setPort2(rs.getString("port2"));
                smsVO.setPort3(rs.getString("port3"));
                smsVO.setVp1(rs.getInt("vp1"));
                smsVO.setVp2(rs.getInt("vp2"));
                smsVO.setVp3(rs.getInt("vp3"));
                smsVO.setStatus(rs.getString("status"));
                smscList.add(smsVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSmscDetails]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSmscDetails]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: smscList.size():");
            	loggerValue.append(smscList.size());
            	_log.debug(methodName, loggerValue);
            }
        }
        return smscList;
    }

    /**
     * This method is used to load language paramters under a location ,
     * only the latest and 2nd latest are loaded and then they are compared in
     * another
     * function as to which parameter is changed
     * 
     * @param p_con
     *            of Connection type
     * @param p_networkCode
     *            of String type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList loadLangParameters(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadLangParameters";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode=");
        	loggerValue.append(p_networkCode);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        final ArrayList langList = new ArrayList();
        SmsVO smsVO = null;
        try {
             StringBuilder sqlLoadBuf =servicesWebQry.loadLangParametersQry();

             if(_log.isDebugEnabled()){
     			loggerValue.setLength(0);
     			loggerValue.append(QUERY_KEY);
     			loggerValue.append(sqlLoadBuf.toString());
     			_log.debug(methodName, loggerValue);
     		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_networkCode);
            dbPs.setString(2, p_networkCode);
            rs = dbPs.executeQuery();
            while (rs.next()) {
                smsVO = new SmsVO();
                smsVO.setLangId(rs.getString("land_id"));
                smsVO.setLangParam1(rs.getString("langparam1"));
                smsVO.setLangParam2(rs.getString("langparam2"));
                smsVO.setLangParam3(rs.getString("langparam3"));
                smsVO.setLangParam4(rs.getString("langparam4"));
                smsVO.setLangParam5(rs.getString("langparam5"));
                smsVO.setStatus(rs.getString("status"));
                langList.add(smsVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLangParameters]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLangParameters]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: langList.size():");
            	loggerValue.append(langList.size());
            	_log.debug(methodName, loggerValue);
            }
        }
        return langList;
    }

    /**
     * This method is used to load language paramters for a particular msisdn
     * based upon its lang_ref parameter
     * 
     * @param p_con
     *            of Connection type
     * @param p_smsVO
     *            SmsVO
     * @exception SQLException
     */

    public void loadLangParameters(Connection con, SmsVO p_smsVO) throws BTSLBaseException {
        final String methodName = "loadLangParameters";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: locationCode=");
        	loggerValue.append(p_smsVO.getLocation());
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT langparam1 , langparam2 ,langparam3, ");
            sqlLoadBuf.append(" langparam4 ,langparam5 FROM  lang_master  ");
            sqlLoadBuf.append(" WHERE land_id = ? and network_code = ? ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_smsVO.getLangId());
            dbPs.setString(2, p_smsVO.getLocation());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                p_smsVO.setLangParam1(rs.getString("langparam1"));
                p_smsVO.setLangParam2(rs.getString("langparam2"));
                p_smsVO.setLangParam3(rs.getString("langparam3"));
                p_smsVO.setLangParam4(rs.getString("langparam4"));
                p_smsVO.setLangParam5(rs.getString("langparam5"));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLangParameters]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLangParameters]", "", "", "",
                loggerValue.toString());
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
                _log.debug(methodName, "Exiting");
            }
        }
    }

    /**
     * This method is used to load language paramters for a particular
     * msisdn based upon its lang_ref parameter
     * 
     * @param con
     *            of Connection type
     * @param smsVO
     *            p_SmsVO
     * @exception BTSLBaseException
     */

    public void loadSmsParameters(Connection con, SmsVO p_smsVO) throws BTSLBaseException {
        final String methodName = "loadSmsParameters";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_smsVO=");
        	loggerValue.append(p_smsVO);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT smsc1 , smsc2 ,smsc3, ");
            sqlLoadBuf.append(" port1 , port2 ,port3, vp1,vp2 ,vp3   ");
            sqlLoadBuf.append(" FROM  sms_master WHERE sms_param_id = ? AND network_code = ? ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_smsVO.getSmscId());
            dbPs.setString(2, p_smsVO.getLocation());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                p_smsVO.setSmsc1(rs.getString("smsc1"));
                p_smsVO.setSmsc2(rs.getString("smsc2"));
                p_smsVO.setSmsc3(rs.getString("smsc3"));
                p_smsVO.setPort1(rs.getString("port1"));
                p_smsVO.setPort2(rs.getString("port2"));
                p_smsVO.setPort3(rs.getString("port3"));
                p_smsVO.setVp1(rs.getInt("vp1"));
                p_smsVO.setVp2(rs.getInt("vp2"));
                p_smsVO.setVp3(rs.getInt("vp3"));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSmsParameters]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSmsParameters]", "", "", "",
                loggerValue.toString());
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
                _log.debug(methodName, "Exiting");
            }
        }
    }

    /**
     * This method is used to get Sim profile Information
     * 
     * @param con
     *            of Connection type
     * @param p_simProId
     *            of String type
     * @return returns the SimProfileVO
     * @exception BTSLBaseException
     */

    public SimProfileVO getSimProfileInfo(Connection p_con, String p_simProId) throws BTSLBaseException {
        final String methodName = "getSimProfileInfo";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_simProId=");
        	loggerValue.append(p_simProId);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        SimProfileVO simProfileVO = null;
        try {
            
        	final String sqlLoadBuf = servicesWebQry.getSimProfileInfoQry();
        	if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf);
            dbPs.setString(1, p_simProId);
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
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[getSimProfileInfo]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[getSimProfileInfo]", "", "", "",
                loggerValue.toString());
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
                _log.debug(methodName, "Exiting");
            }
        }
        return simProfileVO;
    }

    /**
     * This method is used to load SIM Services
     * 
     * @param p_con
     *            of Connection type
     * @param p_type
     *            of String type
     * @param p_networkCode
     *            of String type
     * @param p_serviceSet
     *            of String type
     * @param p_searchStr
     *            of String type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList loadLatestSIMServiceListForSearch(Connection p_con, String p_type, String p_networkCode, String p_serviceSet, String p_searchStr, boolean p_isall) throws BTSLBaseException {
        final String methodName = "loadLatestSIMServiceListForSearch";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_serviceSet=");
        	loggerValue.append(p_serviceSet);
        	loggerValue.append(",p_searchStr=");
        	loggerValue.append(p_searchStr);
        	loggerValue.append(",p_isall=");
        	loggerValue.append(p_isall);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        final ArrayList serviceList = new ArrayList();
        ServicesVO servicesVO = null;
       
        try {
        	final String sqlLoadBuf = servicesWebQry.loadLatestSIMServiceListForSearchQry(p_networkCode, p_isall);
        	if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf);
    			_log.debug(methodName, loggerValue);
    		}

            dbPs = p_con.prepareStatement(sqlLoadBuf);
            int i = 0;
            dbPs.setString(++i, p_type);
            if (!("ALL".equals(p_networkCode))) {
                dbPs.setString(++i, p_networkCode);
            }
            dbPs.setString(++i, p_serviceSet);
            dbPs.setString(++i, p_serviceSet);
            dbPs.setString(++i, p_searchStr);
            rs = dbPs.executeQuery();
            int index = 0;
            while (rs.next()) {
                servicesVO = new ServicesVO();
                servicesVO.setUserType(p_type);
                servicesVO.setServiceID(rs.getString("service_id"));
                servicesVO.setMajorVersion(rs.getString("major_version"));
                servicesVO.setMinorVersion(rs.getString("minor_version"));
                servicesVO.setDescription(rs.getString("description"));
                servicesVO.setLabel1(rs.getString("label1"));
                servicesVO.setLabel2(rs.getString("label2"));
                servicesVO.setLength(rs.getLong("length"));
                servicesVO.setStatus(rs.getString("status"));
                servicesVO.setCreatedBy(rs.getString("created_by"));
                servicesVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                servicesVO.setModifiedBy(rs.getString("modified_by"));
                if (rs.getTimestamp("modified_on") != null) {
                    servicesVO.setModifiedOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("modified_on")));
                }
                servicesVO.setRadioIndex(index);
                serviceList.add(servicesVO);
                index++;
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLatestSIMServiceListForSearch]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLatestSIMServiceListForSearch]",
                "", "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: serviceList.size():");
            	loggerValue.append(serviceList.size());
            	_log.debug(methodName, loggerValue);
            }
        }
        return serviceList;
    }

    /**
     * This method is used to user SIM services under a given user type,location
     * code,
     * profile type and sim profile type.Only the latest and 2nd latest are
     * loaded
     * and then they are compared in another function as to which version is
     * changed
     * 
     * @param con
     *            of Connection type
     * @param type
     *            of String type
     * @param profile
     *            of String type
     * @param locationCode
     *            of String type
     * @param p_simProfileId
     *            of String type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList loadCurrentUserSIMServiceProfileList(Connection p_con, String p_type, String p_profile, String p_networkCode, String p_simProfileId) throws BTSLBaseException {
        final String methodName = "loadCurrentUserSIMServiceProfileList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_type=");
        	loggerValue.append(p_type);
        	loggerValue.append(",p_profile=");
        	loggerValue.append(p_profile);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_simProfileId=");
        	loggerValue.append(p_simProfileId);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        final ArrayList serviceList = new ArrayList();
        UserServicesVO userServicesVO = null;
        try {

            final StringBuilder sqlLoadBuf = new StringBuilder(
                "SELECT SS.service_set_id serSetID,USS.position, USS.service_id, USS.major_version, USS.minor_version,USS.offset,");
            sqlLoadBuf.append(" USS.status, USS.network_code,L.network_name ,SS.description,SS.label1,SS.label2,SS.length,SS.bytecode ");
            sqlLoadBuf.append(" FROM user_sim_services USS,networks L,sim_services SS");
            sqlLoadBuf.append(" WHERE USS.user_type = ? AND USS.profile = ? AND USS.network_code = ? AND USS.sim_profile_id=? AND USS.network_code=L.network_code ");
            sqlLoadBuf.append(" AND USS.service_id=SS.service_id AND USS.major_version=SS.major_version ");
            sqlLoadBuf.append(" AND SS.service_set_id=L.service_set_id AND present_status='CURRENT' ");
            sqlLoadBuf.append(" AND USS.minor_version=SS.minor_version order by USS.position ,USS.service_id,USS.modified_on desc");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
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
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadCurrentUserSIMServiceProfileList]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadLatestSIMServiceListForSearch", "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadCurrentUserSIMServiceProfileList]",
                "", "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: serviceList.size():");
            	loggerValue.append(serviceList.size());
            	_log.debug(methodName, loggerValue);
            }
        }
        return serviceList;
    }

    /**
     * This method is used to get service details for a mobile no
     * 
     * @param con
     *            of Connection type
     * @param p_msisdn
     *            of String type
     * @return returns the ArrayList
     * @exception BTSLBaseException
     */

    public ArrayList getServicesNamesList(Connection con, ArrayList p_serviceList, String p_serviceIdList, String p_networkCode, String p_userType, String p_profile) throws BTSLBaseException {
        final String methodName = "getServicesNamesList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_serviceList.size()=");
        	loggerValue.append(p_serviceList.size());
        	loggerValue.append(",p_serviceIdList=");
        	loggerValue.append(p_serviceIdList);
        	loggerValue.append(",p_networkCode=");
        	loggerValue.append(p_networkCode);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        ServicesVO servicesVO = null;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT SS.label1 label1  FROM sim_services SS, networks ST WHERE ");
            sqlLoadBuf.append(" SS.service_id=? AND ST.service_set_id=SS.service_set_id  ");
            sqlLoadBuf.append(" AND ST.network_code =? AND SS.major_version=? AND SS.minor_version=? ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            int serviceListSize = p_serviceList.size();
            for (int i = 0; i < serviceListSize; i++) {
                servicesVO = (ServicesVO) p_serviceList.get(i);
                dbPs = con.prepareStatement(sqlLoadBuf.toString());
                dbPs.setString(1, servicesVO.getServiceID());
                dbPs.setString(2, p_networkCode);
                dbPs.setString(3, servicesVO.getMajorVersion());
                dbPs.setString(4, servicesVO.getMinorVersion());
                rs = dbPs.executeQuery();
                if (rs.next()) {
                    servicesVO.setName(rs.getString("label1"));
                } else {
                    dbPs.clearParameters();
                    continue;
                }
                dbPs.clearParameters();
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[getServicesNamesList]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[getServicesNamesList]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: p_serviceList.size():");
            	loggerValue.append(p_serviceList.size());
            	_log.debug(methodName, loggerValue);
            }
        }
        return p_serviceList;
    }

    /**
     * Method isPhoneExistsInPosKey
     * This method will return true or false, if mobile number is available
     * in the database
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return boolean
     * @exception BTSLBaseException
     */

    public boolean isPhoneExistsInPosKey(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "isPhoneExistsInPosKey";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn=");
        	loggerValue.append(p_msisdn);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean found = false;
        final String qry = "SELECT msisdn FROM pos_keys WHERE msisdn=? ";
        try {
        	if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qry);
    			_log.debug(methodName, loggerValue);
    		}
            pstmt = p_con.prepareStatement(qry);
            pstmt.setString(1, p_msisdn);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[isPhoneExistsInPosKey]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[isPhoneExistsInPosKey]", "", "", "",
                loggerValue.toString());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: found:");
            	loggerValue.append(found);
            	_log.debug(methodName, loggerValue);
            }
        }
        return found;
    }

    /**
     * This method is used to load Pin and Sim Profile Information for a MSISDN
     * 
     * @param p_con
     *            of Connection type
     * @param p_msisdn
     *            String
     * @return String
     * @exception BTSLBaseException
     */

    public ArrayList loadPinSimProfileInfoMsisdn(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "loadPinSimProfileInfoMsisdn";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        ArrayList pinSimProfile = null;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder(" SELECT sim_profile_id,pin_required FROM pos_keys PK , user_phones SUP ");
            sqlLoadBuf.append(" where PK.msisdn = ? AND  PK.msisdn = SUP.msisdn ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_msisdn);
            rs = dbPs.executeQuery();
            if (rs.next()) {
                pinSimProfile = new ArrayList();
                pinSimProfile.add(rs.getString("pin_required"));
                pinSimProfile.add(rs.getString("sim_profile_id"));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadPinSimProfileInfoMsisdn]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadPinSimProfileInfoMsisdn]", "", "",
                "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: pinSimProfile:");
            	loggerValue.append(pinSimProfile);
            	_log.debug(methodName, loggerValue);
            }
        }
        return pinSimProfile;
    }

    /**
     * This method is used to load latest language paramters under a
     * location (Latest parmaters have there status ='Y' while other
     * are having status='N')
     * 
     * @param p_con
     *            of Connection type
     * @param p_networkCode
     *            of String type
     * @param p_smsVO
     *            SmsVO
     * @exception BTSLBaseException
     */

    public void loadLangLatestParameters(Connection p_con, String p_networkCode, SmsVO p_smsVO) throws BTSLBaseException {
        final String methodName = "loadLangLatestParameters";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_smsVO=");
        	loggerValue.append(p_smsVO);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT land_id, langparam1, langparam2, langparam3, langparam4, langparam5 ");
            sqlLoadBuf.append("FROM   lang_master WHERE network_code = ? AND status='Y'  ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_networkCode);
            rs = dbPs.executeQuery();
            if (rs.next()) {
                p_smsVO.setLangId(rs.getString("land_id"));
                p_smsVO.setLangParam1(rs.getString("langparam1"));
                p_smsVO.setLangParam2(rs.getString("langparam2"));
                p_smsVO.setLangParam3(rs.getString("langparam3"));
                p_smsVO.setLangParam4(rs.getString("langparam4"));
                p_smsVO.setLangParam5(rs.getString("langparam5"));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLangLatestParameters]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadLangLatestParameters]", "", "", "",
                loggerValue.toString());
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
                _log.debug(methodName, " Exiting");
            }
        }
    }

    /**
     * This method is used to load latest SMS paramters under a location
     * (Latest parmaters have there status ='Y' while other are having
     * status='N')
     * 
     * @param p_con
     *            of Connection type
     * @param p_networkCode
     *            of String type
     * @param p_smsVO
     *            SmsVO
     * @exception BTSLBaseException
     */

    public void loadSMSLatestParameters(Connection p_con, String p_networkCode, SmsVO p_smsVO) throws BTSLBaseException {
        final String methodName = "loadSMSLatestParameters";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(",p_smsVO=");
        	loggerValue.append(p_smsVO);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT sms_param_id,smsc1,smsc2,smsc3,port1,port2,port3,vp1,vp2,vp3");
            sqlLoadBuf.append(" FROM sms_master WHERE network_code = ? AND status='Y'  ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_networkCode);
            rs = dbPs.executeQuery();
            if (rs.next()) {
                p_smsVO.setSmscId(rs.getString("sms_param_id"));
                p_smsVO.setSmsc1(rs.getString("smsc1"));
                p_smsVO.setSmsc2(rs.getString("smsc2"));
                p_smsVO.setSmsc3(rs.getString("smsc3"));
                p_smsVO.setPort1(rs.getString("port1"));
                p_smsVO.setPort2(rs.getString("port2"));
                p_smsVO.setPort3(rs.getString("port3"));
                p_smsVO.setVp1(rs.getInt("vp1"));
                p_smsVO.setVp2(rs.getInt("vp2"));
                p_smsVO.setVp3(rs.getInt("vp3"));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSMSLatestParameters]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSMSLatestParameters]", "", "", "",
                loggerValue.toString());
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
                _log.debug(methodName, " Exiting");
            }
        }
    }

    /**
     * This method is used to load user information based on userid
     * 
     * @param p_con
     *            of Connection type
     * @param p_networkCode
     *            of String type
     * @param p_smsVO
     *            SmsVO
     * @exception BTSLBaseException
     */

    public UserVO loadUserInfo(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserInfo";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userId=");
        	loggerValue.append(p_userId);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        final UserVO userVO = new UserVO();
        try {
            final StringBuilder sqlLoadBuf = new StringBuilder("SELECT   login_id  ,user_name ");
            sqlLoadBuf.append("  FROM   users WHERE UPPER(user_id) = UPPER(?) AND status <> 'N' AND status<> 'C' ");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlLoadBuf.toString());
    			_log.debug(methodName, loggerValue);
    		}
            dbPs = p_con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, p_userId);
            rs = dbPs.executeQuery();
            if (rs.next()) {
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setUserName(rs.getString("user_name"));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadUserInfo]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadUserInfo]", "", "", "",
                loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userVO:");
            	loggerValue.append(userVO);
            	_log.debug(methodName, loggerValue);
            }
        }
        return userVO;
    }

    /**
     * Method addSIMServiceCategories.
     * This method is used to add the sim service categories.
     * 
     * @param p_con
     *            Connection
     * @param p_arrVO
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     */

    public int addSIMServiceCategories(Connection p_con, ArrayList p_arrVO) throws BTSLBaseException {
        final String methodName = "addSIMServiceCategories";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_arrVO=");
        	loggerValue.append(p_arrVO);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtInsert = null;
        int insertCount = -1;
        int insertListSize;
        try {
            insertListSize = p_arrVO.size();
            if (insertListSize > 0) {
                int count = 0;
                final StringBuilder insertQuery = new StringBuilder("INSERT INTO sim_service_categories(");
                insertQuery.append("service_id,category_code,service_set_id,major_version,minor_version) VALUES(?,?,?,?,?)");

                if(_log.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(insertQuery.toString());
        			_log.debug(methodName, loggerValue);
        		}

                pstmtInsert = p_con.prepareStatement(insertQuery.toString());
                for (int i = 0; i < insertListSize; i++) {
                    final SimServiceCategoriesVO categoryVO = (SimServiceCategoriesVO) p_arrVO.get(i);
                    pstmtInsert.setString(1, String.valueOf(categoryVO.getServiceID()));
                    pstmtInsert.setString(2, categoryVO.getCategoryCode());
                    pstmtInsert.setString(3, categoryVO.getServiceSetId());
                    pstmtInsert.setString(4, categoryVO.getMajorVersion());
                    pstmtInsert.setString(5, categoryVO.getMinorVersion());
                    insertCount = pstmtInsert.executeUpdate();
                    pstmtInsert.clearParameters();

                    // check the status of the insert
                    if (insertCount > 0) {
                        count++;
                    }
                }
                if (count == p_arrVO.size()) {
                    insertCount = 1;
                } else {
                    insertCount = 0;
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[addSIMServiceCategories]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[addSIMServiceCategories]", "", "", "",
                loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount:");
            	loggerValue.append(insertCount);
            	_log.debug(methodName, loggerValue);
            }
        }
        return insertCount;
    }

    /**
     * Method loadSimServiceCategoryCodeList.
     * This method is used to load the category code from sim service
     * categories.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceID
     *            String
     * @param p_serviceSetId
     *            String
     * @param p_majorVersion
     *            String
     * @param p_minorVersion
     *            String
     * @return int
     * @throws BTSLBaseException
     */

    public ArrayList loadSimServiceCategoryCodeList(Connection p_con, String p_serviceID, String p_serviceSetId, String p_majorVersion, String p_minorVersion) throws BTSLBaseException {
        final String methodName = "loadSimServiceCategoryCodeList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_serviceID=");
        	loggerValue.append(p_serviceID);
        	loggerValue.append(",p_serviceSetId=");
        	loggerValue.append(p_serviceSetId);
        	loggerValue.append(",p_majorVersion=");
        	loggerValue.append(p_majorVersion);
        	loggerValue.append(",p_minorVersion=");
        	loggerValue.append(p_minorVersion);
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ArrayList categoryCodeList = null;
        ResultSet rs = null;
        try {
            final StringBuilder selectQuery = new StringBuilder("SELECT category_code FROM sim_service_categories");
            selectQuery.append(" WHERE service_id=? AND service_set_id=? AND major_version=? AND minor_version=?");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery.toString());
    			_log.debug(methodName, loggerValue);
    		}

            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_serviceID);
            pstmtSelect.setString(2, p_serviceSetId);
            pstmtSelect.setString(3, p_majorVersion);
            pstmtSelect.setString(4, p_minorVersion);
            rs = pstmtSelect.executeQuery();
            categoryCodeList = new ArrayList();
            while (rs.next()) {
                categoryCodeList.add(rs.getString("category_code"));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSimServiceCategoryCodeList]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSimServiceCategoryCodeList]", "",
                "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: categoryCodeList.size():");
            	loggerValue.append(categoryCodeList.size());
            	_log.debug(methodName, loggerValue);
            }
        }
        return categoryCodeList;
    }

    /**
     * Method deleteSimServiceCategories.
     * This method is used to delete category information from sim service
     * categories.
     * 
     * @param p_con
     *            Connection
     * @param p_serviceID
     *            String
     * @param p_serviceSetId
     *            String
     * @param p_majorVersion
     *            String
     * @param p_minorVersion
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean deleteSimServiceCategories(Connection p_con, String p_serviceSetId, String p_serviceId, String p_majorVersion, String p_minorVersion) throws BTSLBaseException {
        final String methodName = "deleteSimServiceCategories";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_serviceID=");
        	loggerValue.append(p_serviceId);
        	loggerValue.append(",p_serviceSetId=");
        	loggerValue.append(p_serviceSetId);
        	loggerValue.append(",p_majorVersion=");
        	loggerValue.append(p_majorVersion);
        	loggerValue.append(",p_minorVersion=");
        	loggerValue.append(p_minorVersion);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        int deleteCount = -1;
        try {
            final StringBuilder deleteQuery = new StringBuilder("DELETE FROM sim_service_categories");
            deleteQuery.append(" WHERE service_set_id=? AND service_id=? AND major_version=? AND minor_version=?");
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(deleteQuery.toString());
    			_log.debug(methodName, loggerValue);
    		}
            pstmtSelect = p_con.prepareStatement(deleteQuery.toString());
            pstmtSelect.setString(1, p_serviceSetId);
            pstmtSelect.setString(2, p_serviceId);
            pstmtSelect.setString(3, p_majorVersion);
            pstmtSelect.setString(4, p_minorVersion);
            deleteCount = pstmtSelect.executeUpdate();
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[deleteSimServiceCategories]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[deleteSimServiceCategories]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: deleteCount:");
            	loggerValue.append(deleteCount);
            	_log.debug(methodName, loggerValue);
            }
        }
        if (deleteCount > 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method loadSimServiceDetailsForAssociation
     * This method is used to load service set id along with service set id its
     * major
     * and minor version for association with categories method is used for
     * generating arraylist of list value VO's
     * 
     * @param p_con
     *            Connection
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadSimServiceDetailsForAssociation(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadSimServiceDetailsForAssociation";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	_log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList profileVO = new ArrayList();
        final StringBuilder strBuff = new StringBuilder(" SELECT service_set_id,service_id,label1,");
        strBuff.append("major_version,minor_version FROM sim_services ORDER BY service_id");
        final String sqlSelect = strBuff.toString();
        if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(strBuff.toString());
			_log.debug(methodName, loggerValue);
		}
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + sqlSelect);
            }
            while (rs.next()) {
                profileVO.add(new ListValueVO(rs.getString("label1") + "(" + rs.getString("major_version") + " " + rs.getString("minor_version") + ")", rs
                    .getString("service_set_id") + ":" + rs.getString("service_id") + ":" + rs.getString("major_version") + ":" + rs.getString("minor_version")));
            }
        }

        catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSimServiceDetailsForAssociation]",
                "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesWebDAO[loadSimServiceDetailsForAssociation]",
                "", "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: profileVO.size():");
            	loggerValue.append(profileVO.size());
            	_log.debug(methodName, loggerValue);
            }
        }

        return profileVO;
    }

    public ServicesVO loadLatestSIMServiceDetails(Connection con, String serviceId,String majorVersion) throws BTSLBaseException {
        final String METHOD_NAME = "loadLatestSIMServiceDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        ServicesVO serviceVO = null;
        StringBuilder selectQueryBuff = new StringBuilder();
        selectQueryBuff.append("SELECT SS.service_set_id, SS.service_id, SS.major_version, SS.minor_version,SS.status, SS.label1,SS.label2, SS.length, SS.wml,SS.bytecode ");
        selectQueryBuff.append(",SS.description FROM sim_services SS WHERE SS.service_id = ? AND SS.created_on = (SELECT MAX(created_on) ");
        selectQueryBuff.append("FROM sim_services SS_inner WHERE SS_inner.service_id = SS.service_id and SS.service_set_id = SS_inner.service_set_id and SS_inner.major_version = ?)");

        String selectQuery = selectQueryBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query " + selectQuery);
        }
        serviceVO = new ServicesVO();
        try (PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);) {
            pstmtSelect.setString(1, serviceId);
            pstmtSelect.setString(2, majorVersion);
            try (ResultSet rs = pstmtSelect.executeQuery();) {
                while (rs.next()) {
                    serviceVO.setServiceID(rs.getString("service_id"));
                    serviceVO.setServiceSetID(rs.getString("service_set_id"));
                    serviceVO.setLabel1(rs.getString("label1"));
                    serviceVO.setLabel2(rs.getString("label2"));
                    serviceVO.setDescription(rs.getString("description"));
                    serviceVO.setStatus(rs.getString("status"));
                    serviceVO.setMajorVersion(rs.getString("major_version"));
                    serviceVO.setMinorVersion(rs.getString("minor_version"));
                    serviceVO.setLength(rs.getLong("length"));
                    serviceVO.setWml(rs.getString("wml"));
                    serviceVO.setByteCode(rs.getString("bytecode"));
                }
            }

        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(METHOD_NAME, " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } finally {

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, PretupsI.EXITED, serviceVO);
            }
        }
        return serviceVO;
    }


    public void deleteSIMServiceCategoriesOnFinalSave(Connection con, ArrayList arrVO) throws BTSLBaseException {
        final String METHOD_NAME = "deleteSIMServiceCategoriesOnFinalSave";
        StringBuilder loggerValue = new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.append(PretupsI.ENTERED).append(arrVO);
            _log.debug(METHOD_NAME, loggerValue);
        }

        PreparedStatement pstmtDeleteCategories = null;
        PreparedStatement pstmtDeleteServices = null;
        SimServiceCategoriesVO categoryVO = null;
        int deleteCount = -1;
        int deleteListSize;
        try {
            deleteListSize = arrVO.size();
            if (deleteListSize > 0) {
                final String deleteCategoriesQuery = "DELETE FROM sim_service_categories WHERE "
                        + "service_id=? AND category_code=? AND service_set_id=? AND major_version=? AND minor_version=?";
                pstmtDeleteCategories = con.prepareStatement(deleteCategoriesQuery);

                final String deleteServicesQuery = "DELETE FROM sim_services WHERE service_id=? AND service_set_id=? AND major_version=? AND minor_version=?";
                pstmtDeleteServices = con.prepareStatement(deleteServicesQuery);

                for (int i = 0; i < deleteListSize; i++) {
                    categoryVO = (SimServiceCategoriesVO) arrVO.get(i);
                    pstmtDeleteCategories.setString(1, String.valueOf(categoryVO.getServiceID()));
                    pstmtDeleteCategories.setString(2, categoryVO.getCategoryCode());
                    pstmtDeleteCategories.setString(3, categoryVO.getServiceSetId());
                    pstmtDeleteCategories.setString(4, categoryVO.getMajorVersion());
                    pstmtDeleteCategories.setString(5, categoryVO.getMinorVersion());
                    deleteCount = pstmtDeleteCategories.executeUpdate();
                    pstmtDeleteCategories.clearParameters();
                }

                pstmtDeleteServices.setString(1, String.valueOf(categoryVO.getServiceID()));
                pstmtDeleteServices.setString(2, categoryVO.getServiceSetId());
                pstmtDeleteServices.setString(3, categoryVO.getMajorVersion());
                pstmtDeleteServices.setString(4, categoryVO.getMinorVersion());
                pstmtDeleteServices.executeUpdate();
            }
        } catch (SQLException sqe) {
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, sqe);
        } finally {
            try {
                if (pstmtDeleteCategories != null) {
                    pstmtDeleteCategories.close();
                }
                if (pstmtDeleteServices != null) {
                    pstmtDeleteServices.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, PretupsI.EXITED);
            }
        }
    }
}

