package com.web.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListSorterUtil;
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
import com.btsl.pretups.master.businesslogic.GeographicalDomainCellsVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainTypeVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

public class GeographicalDomainWebDAO {

    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private final Log _log = LogFactory.getLog(GeographicalDomainWebDAO.class.getName());

    /**
     * Constructor for GeographicalDomainDAO.
     */
    public GeographicalDomainWebDAO() {
        super();
    }

    /**
     * Method loadDomainTypeList.
     * This method loads all the domain type except of which has sequence no 1
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadDomainTypeList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadDomainTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        final ArrayList domainTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT grph_domain_type,grph_domain_type_name FROM geographical_domain_types ");
            selectQuery.append("WHERE sequence_no > 1 ");
            selectQuery.append("ORDER BY sequence_no ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("grph_domain_type_name"), rs.getString("grph_domain_type"));
                domainTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadDomainTypeList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadDomainTypeList]", "", "",
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
                _log.debug(methodName, "Exiting:list size=" + domainTypeList.size());
            }
        }
        return domainTypeList;
    }

    /**
     * varun
     * Method loadGeoDomainList.
     * This method loads all the domain name
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadGeoDomainList(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadGeoDomainList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_networkCode=" + p_networkCode);
        }
        final ArrayList domainParentList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        GeographicalDomainVO geographicalDomainVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT network_code, grph_domain_code, grph_domain_name, parent_grph_domain_code, grph_domain_short_name, ");
            selectQuery.append("description, status, grph_domain_type, modified_on, is_default ");
            selectQuery.append("FROM geographical_domains ");
            selectQuery.append("WHERE network_code=? AND status <> ? ");
            selectQuery.append("ORDER BY grph_domain_name ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_DELETE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                geographicalDomainVO = new GeographicalDomainVO();
                geographicalDomainVO.setNetworkCode(rs.getString("network_code"));
                geographicalDomainVO.setGrphDomainCode(rs.getString("grph_domain_code"));
                geographicalDomainVO.setGrphDomainName(rs.getString("grph_domain_name"));
                geographicalDomainVO.setParentDomainCode(rs.getString("parent_grph_domain_code"));
                geographicalDomainVO.setGrphDomainShortName(rs.getString("grph_domain_short_name"));
                geographicalDomainVO.setDescription(rs.getString("description"));
                geographicalDomainVO.setStatus(rs.getString("status"));
                geographicalDomainVO.setGrphDomainType(rs.getString("grph_domain_type"));
                geographicalDomainVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                geographicalDomainVO.setIsDefault(rs.getString("is_default"));
                domainParentList.add(geographicalDomainVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadGeoDomainList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadGeoDomainList]", "", "",
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
                _log.debug(methodName, "Exiting:list size=" + domainParentList.size());
            }
        }
        return domainParentList;
    }

    /**
     * Method loadParentTypeList.
     * this method loads the information of the all parents (in the parent
     * hierarchy) domains of the input domain type
     * 
     * @param p_con
     *            Connection
     * @param p_domainType
     *            String
     * @return ListValueVO
     * @throws BTSLBaseException
     */
    public ArrayList loadParentTypeList(Connection p_con, String p_domainType) throws BTSLBaseException {
        final String methodName = "loadParentTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_domainType=" + p_domainType);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList parentList = new ArrayList();
        ListValueVO listValueVO = null;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT PRNT.grph_domain_type,PRNT.grph_domain_type_name ");
            selectQuery.append("FROM geographical_domain_types CHLD,geographical_domain_types PRNT ");
            selectQuery.append("WHERE PRNT.sequence_no < CHLD.sequence_no AND CHLD.grph_domain_type=? ");
            selectQuery.append("AND PRNT.sequence_no >0 ");
            selectQuery.append("ORDER BY PRNT.sequence_no ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_domainType);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("grph_domain_type_name"), rs.getString("grph_domain_type"));
                parentList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadParentTypeList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadParentTypeList]", "", "",
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
                _log.debug(methodName, "Exiting:parentList.size()=" + parentList.size());
            }
        }
        return parentList;
    }

    /**
     * Method loadParentDomainList.
     * This method loads the list of perents of the all active domains which
     * parent doamin code is passed as input
     * parameter and which domain type is passed as input parameter.
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_parentDomainCode
     *            String
     * @param p_domainType
     *            String
     * @param p_domainName
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadParentDomainList(Connection p_con, String p_networkCode, String p_parentDomainCode, String p_domainType, String p_domainName) throws BTSLBaseException {
        final String methodName = "loadParentDomainList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                "Entered:p_networkCode=" + p_networkCode + ",p_parentDomainCode=" + p_parentDomainCode + ",p_domainType=" + p_domainType + ",p_domainName" + p_domainName);
        }
        final ArrayList domainParentList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        GeographicalDomainVO geographicalDomainVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT grph_domain_code,grph_domain_name ");
            selectQuery.append("FROM geographical_domains ");
            selectQuery.append("WHERE network_code=? AND grph_domain_type=? AND parent_grph_domain_code=? ");
            selectQuery.append("AND status=? AND UPPER(grph_domain_name) LIKE UPPER(?) ");
            selectQuery.append("ORDER BY grph_domain_name ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_domainType);
            pstmtSelect.setString(3, p_parentDomainCode);
            pstmtSelect.setString(4, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            // for multilanguage support
            // pstmtSelect.setFormOfUse(5, PreparedStatement.FORM_NCHAR);
            pstmtSelect.setString(5, p_domainName);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                geographicalDomainVO = new GeographicalDomainVO();
                geographicalDomainVO.setParentDomainCode(rs.getString("grph_domain_code"));
                geographicalDomainVO.setParentDomainName(rs.getString("grph_domain_name"));
                domainParentList.add(geographicalDomainVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadParentDomainList]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadParentDomainList]", "",
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
                _log.debug(methodName, "Exiting:list size=" + domainParentList.size());
            }
        }
        return domainParentList;
    }

    /**
     * Method loadDomainList.
     * This method loads the list of all active domains which parent doamin code
     * is passed as input
     * parameter and which domain type is passed as input parameter.
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_parentDomainCode
     *            String
     * @param p_domainType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadDomainList(Connection p_con, String p_networkCode, String p_parentDomainCode, String p_domainType) throws BTSLBaseException {
        final String methodName = "loadDomainList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_networkCode=" + p_networkCode + ",p_parentDomainCode=" + p_parentDomainCode + ",p_domainType=" + p_domainType);
        }
        final ArrayList domainParentList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        GeographicalDomainVO geographicalDomainVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT network_code, grph_domain_code, grph_domain_name, parent_grph_domain_code, grph_domain_short_name, ");
            selectQuery.append("description, status, grph_domain_type, modified_on,is_default ");
            selectQuery.append("FROM geographical_domains ");
            selectQuery.append("WHERE network_code=? AND grph_domain_type=? AND parent_grph_domain_code=? AND status <> ? ");
            selectQuery.append("ORDER BY grph_domain_name ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_domainType);
            pstmtSelect.setString(3, p_parentDomainCode);
            pstmtSelect.setString(4, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_DELETE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                geographicalDomainVO = new GeographicalDomainVO();
                geographicalDomainVO.setNetworkCode(rs.getString("network_code"));
                geographicalDomainVO.setGrphDomainCode(rs.getString("grph_domain_code"));
                geographicalDomainVO.setGrphDomainName(rs.getString("grph_domain_name"));
                geographicalDomainVO.setParentDomainCode(rs.getString("parent_grph_domain_code"));
                geographicalDomainVO.setGrphDomainShortName(rs.getString("grph_domain_short_name"));
                geographicalDomainVO.setDescription(rs.getString("description"));
                geographicalDomainVO.setStatus(rs.getString("status"));
                geographicalDomainVO.setGrphDomainType(rs.getString("grph_domain_type"));
                geographicalDomainVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                geographicalDomainVO.setIsDefault(rs.getString("is_default"));
                final LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, geographicalDomainVO.getIsDefault());
                geographicalDomainVO.setIsDefaultDesc(lookupsVO.getLookupName());
                domainParentList.add(geographicalDomainVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadDomainList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadDomainList]", "", "", "",
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
                _log.debug(methodName, "Exiting:list size=" + domainParentList.size());
            }
        }
        return domainParentList;
    }

    /**
     * Method addGeographicalDomain.
     * 
     * @param p_con
     *            Connection
     * @param p_geographicalDomainVO
     *            GeographicalDomainVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addGeographicalDomain(Connection p_con, GeographicalDomainVO p_geographicalDomainVO) throws BTSLBaseException {
        final String methodName = "addGeographicalDomain";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_geographicalDomainVO=" + p_geographicalDomainVO);
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            final StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO geographical_domains(grph_domain_code, network_code, grph_domain_name,");
            insertQuery.append("parent_grph_domain_code, grph_domain_short_name, description, status, grph_domain_type, created_on, created_by, modified_on, modified_by ");
            if (!BTSLUtil.isNullString(p_geographicalDomainVO.getIsDefault())) {
                insertQuery.append(" , is_default) VALUES(UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?) ");
            } else {
                insertQuery.append(" ) VALUES(UPPER(?),?,?,?,?,?,?,?,?,?,?,?)");
            }
            final String query = insertQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtInsert = p_con.prepareStatement(query);

            pstmtInsert.setString(1, p_geographicalDomainVO.getGrphDomainCode());
            pstmtInsert.setString(2, p_geographicalDomainVO.getNetworkCode());

            // for multilanguage support
            // pstmtInsert.setFormOfUse(3, PreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(3, p_geographicalDomainVO.getGrphDomainName());

            pstmtInsert.setString(4, p_geographicalDomainVO.getParentDomainCode());

            // for multilanguage support
            // pstmtInsert.setFormOfUse(5, PreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(5, p_geographicalDomainVO.getGrphDomainShortName());

            // for multilanguage support
            // pstmtInsert.setFormOfUse(6, PreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(6, p_geographicalDomainVO.getDescription());

            pstmtInsert.setString(7, p_geographicalDomainVO.getStatus());
            pstmtInsert.setString(8, p_geographicalDomainVO.getGrphDomainType());
            pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_geographicalDomainVO.getCreatedOn()));
            pstmtInsert.setString(10, p_geographicalDomainVO.getModifiedBy());
            pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_geographicalDomainVO.getModifiedOn()));
            pstmtInsert.setString(12, p_geographicalDomainVO.getModifiedBy());
            if (!BTSLUtil.isNullString(p_geographicalDomainVO.getIsDefault())) {
                pstmtInsert.setString(13, p_geographicalDomainVO.getIsDefault());
            }
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[addGeographicalDomain]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[addGeographicalDomain]", "",
                "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting:return=" + addCount);
            }
        }
        return addCount;
    }

    /**
     * Method updateGeographicalDomain.
     * 
     * @param p_con
     *            Connection
     * @param p_geographicalDomainVO
     *            GeographicalDomainVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateGeographicalDomain(Connection p_con, GeographicalDomainVO p_geographicalDomainVO) throws BTSLBaseException {
        final String methodName = "updateGeographicalDomain";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_geographicalDomainVO=" + p_geographicalDomainVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final StringBuilder UpdateQuery = new StringBuilder();
            UpdateQuery.append("UPDATE geographical_domains SET grph_domain_name=?,grph_domain_short_name=?, ");
            UpdateQuery.append("description=?, status=?,modified_on=?, modified_by=? ");
            if (!BTSLUtil.isNullString(p_geographicalDomainVO.getIsDefault())) {
                UpdateQuery.append(", is_default=? ");
            }
            UpdateQuery.append("WHERE grph_domain_code=? AND network_code= ?");
            final String query = UpdateQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }
            pstmtUpdate = p_con.prepareStatement(query);

            int i = 1;
            // for multilanguage support
       
            pstmtUpdate.setString(i++, p_geographicalDomainVO.getGrphDomainName());

            pstmtUpdate.setString(i++, p_geographicalDomainVO.getGrphDomainShortName());

            pstmtUpdate.setString(i++, p_geographicalDomainVO.getDescription());

            pstmtUpdate.setString(i++, p_geographicalDomainVO.getStatus());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_geographicalDomainVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_geographicalDomainVO.getModifiedBy());

            if (!BTSLUtil.isNullString(p_geographicalDomainVO.getIsDefault())) {
                pstmtUpdate.setString(i++, p_geographicalDomainVO.getIsDefault());
            }

            pstmtUpdate.setString(i++, p_geographicalDomainVO.getGrphDomainCode());
            pstmtUpdate.setString(i++, p_geographicalDomainVO.getNetworkCode());

            // for the checking is the record modified during the transaction.
            final boolean modified = this.isRecordModified(p_con, p_geographicalDomainVO.getLastModifiedTime(), p_geographicalDomainVO.getGrphDomainCode());
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[updateGeographicalDomain]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[updateGeographicalDomain]",
                "", "", "", "Exception:" + e.getMessage());
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
     * Method isRecordModified.
     * This method is used to check that is the record modified during the
     * processing.
     * 
     * @param p_con
     *            Connection
     * @param p_oldlastModified
     *            Long
     * @param p_grphDomainCode
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection p_con, long p_oldlastModified, String p_grphDomainCode) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_oldlastModified=" + p_oldlastModified + ",p_grphDomainCode=" + p_grphDomainCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final StringBuilder sqlRecordModified = new StringBuilder();
        sqlRecordModified.append("SELECT modified_on FROM geographical_domains ");
        sqlRecordModified.append("WHERE grph_domain_code=? ");
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            final String query = sqlRecordModified.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_grphDomainCode);
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
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[isRecordModified]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[isRecordModified]", "", "",
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
                _log.debug(methodName, "Exititng:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method isGeographicalDomainActive.
     * 
     * @param p_con
     *            Connection
     * @param p_grphDomainCode
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isGeographicalDomainActive(Connection p_con, String p_grphDomainCode) throws BTSLBaseException {
        final String methodName = "isGeographicalDomainActive";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_grphDomainCode=" + p_grphDomainCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        final StringBuilder sqlRecordExist = new StringBuilder();
        try {
            sqlRecordExist.append("SELECT 1 FROM  geographical_domains ");
            sqlRecordExist.append("WHERE parent_grph_domain_code=? AND status <> ?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordExist);
            }
            pstmtSelect = p_con.prepareStatement(sqlRecordExist.toString());
            pstmtSelect.setString(1, p_grphDomainCode);
            pstmtSelect.setString(2, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_DELETE);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[isGeographicalDomainActive]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[isGeographicalDomainActive]",
                "", "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exititng:isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }// end isGeographicalDomainActive

    /**
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int updatedeDefaultGeography(Connection p_con, String p_networkCode, String p_parentGeographyCode) throws BTSLBaseException {
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        final String methodName = "updatedeDefaultGeography";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_parentGeographyCode= " + p_parentGeographyCode + " p_networkCode: " + p_networkCode);
        }
        try {
            final String updateQuery = "UPDATE GEOGRAPHICAL_DOMAINS set is_default=? WHERE network_code=? AND parent_grph_domain_code=? AND is_default=? ";

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlUpdate:" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            int i = 1;
            pstmtUpdate.setString(i++, PretupsI.NO);
            pstmtUpdate.setString(i++, p_networkCode);
            pstmtUpdate.setString(i++, p_parentGeographyCode);
            pstmtUpdate.setString(i++, PretupsI.YES);
            updateCount = pstmtUpdate.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[updatedeDefaultGeography]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[updatedeDefaultGeography]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
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
     * Method loadGeographicalSequenceNumber.
     * 
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int loadGeographicalSequenceNumber(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadCategoryGeographicalSequenceVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        final StringBuilder strBuff = new StringBuilder("SELECT G.sequence_no GRPH_SEQ ");
        strBuff.append("FROM categories C,geographical_domain_types G WHERE C.domain_code=? AND ");
        strBuff.append("C.grph_domain_type=G.grph_domain_type AND C.status <> 'N'  AND ");
        strBuff.append("C.sequence_no = (SELECT max(sequence_no) FROM categories WHERE ");
        strBuff.append("domain_code=C.domain_code AND status<>'N' AND category_type<>? )");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadGeographicalSequenceNumber", "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            pstmtSelect.setString(2, PretupsI.CATEGORY_TYPE_AGENT);
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {

                return rs.getInt("GRPH_SEQ");
            } else {
                return 2;
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryGeographicalSequenceVO]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadGeographicalSequenceNumber", "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryGeographicalSequenceVO]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadGeographicalSequenceNumber", "error.general.processing");

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
                _log.debug("loadGeographicalSequenceNumber", "Exiting ");
            }
        }
    }

    /**
     * Sanjeew
     * Date: 12/03/07
     * Method loadGeographicalDomainCode.
     * This method loads all the Geographical domain Code
     * 
     * @param p_con
     *            Connection
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadGeographicalDomainCode(Connection p_con, String p_geodomaintype) throws BTSLBaseException {
        final String methodName = "loadGeographicalDomainCode";
        if (_log.isDebugEnabled()) {
            _log.debug("loadGeoDomainCode", "Entered:p_geodomaintype=" + p_geodomaintype);
        }
        final ArrayList domainParentList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        GeographicalDomainVO geographicalDomainVO = null;
        ResultSet rs = null;
        try {

            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT GRPH_DOMAIN_CODE, GRPH_DOMAIN_NAME ");
            selectQuery.append("FROM geographical_domains ");
            selectQuery.append("WHERE GRPH_DOMAIN_TYPE =? ");
            if (_log.isDebugEnabled()) {
                _log.debug("loadGeoDomainCode", "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_geodomaintype);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                geographicalDomainVO = new GeographicalDomainVO();
                geographicalDomainVO.setGrphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_CODE")));
                geographicalDomainVO.setGrphDomainName(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_NAME")));
                domainParentList.add(geographicalDomainVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadGeoDomainCode]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadDomainList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadGeoDomainCode]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadGeoDomainCode", "error.general.processing");
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
                _log.debug("loadGeoDomainCode", "Exiting:list size=" + domainParentList.size());
            }
        }
        return domainParentList;
    }

    public ArrayList loadGeographicalDomainCodebyNetwork(Connection p_con, String p_geodomaintype,String network) throws BTSLBaseException {
        final String methodName = "loadGeographicalDomainCodebyNetwork";
        if (_log.isDebugEnabled()) {
            _log.debug("loadGeographicalDomainCodebyNetwork", "Entered:p_geodomaintype=" + p_geodomaintype);
        }
        final ArrayList domainParentList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        GeographicalDomainVO geographicalDomainVO = null;
        ResultSet rs = null;
        try {

            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT GRPH_DOMAIN_CODE ");
            selectQuery.append("FROM geographical_domains ");
            selectQuery.append("WHERE GRPH_DOMAIN_TYPE =? and network_code =? ");
            if (_log.isDebugEnabled()) {
                _log.debug("loadGeoDomainCode", "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_geodomaintype);
            pstmtSelect.setString(2, network);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                geographicalDomainVO = new GeographicalDomainVO();
                geographicalDomainVO.setGrphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_CODE")));
                domainParentList.add(geographicalDomainVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadGeoDomainCode]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadDomainList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadGeoDomainCode]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadGeographicalDomainCodebyNetwork", "error.general.processing");
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
                _log.debug("loadGeoDomainCode", "Exiting:list size=" + domainParentList.size());
            }
        }
        return domainParentList;
    }

    /**
     * Method :loadMasterGeographyList
     * This method load list of Geographies on the basis of category code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            java.lang.String
     * @param p_networkCode
     *            java.lang.String
     * @return ArrayList<GeographicalDomainVO>
     * @throws BTSLBaseException
     * @author shashank.gaur
     */
    public ArrayList<GeographicalDomainVO> loadMasterGeographyList(Connection p_con, String p_categoryCode, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadMasterGeographyList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_categoryCode=" + p_categoryCode + " p_networkCode=" + p_networkCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder("SELECT C.category_code, gd.GRPH_DOMAIN_CODE, gd.GRPH_DOMAIN_NAME,gd.GRPH_DOMAIN_TYPE");
        strBuff
            .append(" FROM categories C,GEOGRAPHICAL_DOMAINS gd WHERE C.category_code = CASE ? WHEN 'ALL' THEN category_code ELSE ? END AND gd.GRPH_DOMAIN_TYPE = C.GRPH_DOMAIN_TYPE");
        strBuff.append("  and gd.network_code=? AND gd.status=? ORDER BY sequence_no");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList<GeographicalDomainVO> geoDomainList = null;
        try {
            int i = 1;
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(i++, p_categoryCode);
            pstmtSelect.setString(i++, p_categoryCode);
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            rs = pstmtSelect.executeQuery();
            geoDomainList = new ArrayList<GeographicalDomainVO>();
            GeographicalDomainVO geoDomainVO = null;
            while (rs.next()) {
                geoDomainVO = new GeographicalDomainVO();
                geoDomainVO.setcategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                geoDomainVO.setGrphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_CODE")));
                geoDomainVO.setGrphDomainName(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_NAME")));// method name changed from setgrphDomainName to setGrphDomainName
                geoDomainVO.setGrphDomainType(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_TYPE")));
                geoDomainList.add(geoDomainVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadMasterGeographyList]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadMasterGeographyList]",
                "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: categoryList size =" + geoDomainList.size());
            }
        }
        return geoDomainList;
    }

    /*
     * this method loads the last geographical domain in the hierarchy and its
     * parent
     * 
     * @param p_con
     * 
     * @return GeographicalDomainCellsVO list
     * 
     * @author Ashutosh
     */
    public ArrayList<GeographicalDomainCellsVO> getGeogCodeDetailsList(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "getGeogCodeParentGeogCodeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        final ArrayList<GeographicalDomainCellsVO> cellGeogList = new ArrayList<GeographicalDomainCellsVO>();
        PreparedStatement pstmtSelect = null;
        GeographicalDomainCellsVO geogVO = null;

        ResultSet rs = null;
        try {
           
        	GeographicalDomainWebQry geographicalDomainWebQry=(GeographicalDomainWebQry)ObjectProducer.getObject(QueryConstants.GEO_DOMAIN_WEB_QRY,QueryConstants.QUERY_PRODUCER);
            String selectQuery=geographicalDomainWebQry.getGeogCodeDetailsListQry();
        	if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_networkCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                geogVO = new GeographicalDomainCellsVO();
                geogVO.setGrphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_CODE")));
                geogVO.setGrphDomainName(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_NAME")));
                geogVO.setParentDomainCode(SqlParameterEncoder.encodeParams(rs.getString("PARENT_GRPH_DOMAIN_CODE")));
                cellGeogList.add(geogVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "GeographicalDomainWebDAO[getGeogCodeParentGeogCodeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "GeographicalDomainWebDAO[getGeogCodeParentGeogCodeList]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getCellGroupList", "Exiting:list size=" + cellGeogList.size());
            }
        }

        return cellGeogList;
    }

    /*
     * this method loads the cell id details from the GEOGRAPHICAL_DOMAIN_CELLS
     * table
     * 
     * @param p_con
     * 
     * @return GeographicalDomainCellsVO list
     * 
     * @author Ashutosh
     */
    public ArrayList<GeographicalDomainCellsVO> loadGeogCellidDeatilsVOList(Connection p_con, String networkCode) throws BTSLBaseException {
        final String methodName = "loadGeogCellidDeatilsVOList ";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered NetworkCode = " + networkCode);
        }
        ArrayList<GeographicalDomainCellsVO> detailsList = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        GeographicalDomainCellsVO geogVO = null;
        try {
            final StringBuilder strBuff = new StringBuilder("SELECT gdc.GRPH_CELLID,gdc.GRPH_CELL_NAME,gdc.grph_domain_code,gdc.status");
            strBuff.append(" from GEOGRAPHICAL_DOMAIN_CELLS gdc, GEOGRAPHICAL_DOMAINS gd");
            strBuff.append(" where gdc.grph_domain_code=gd.GRPH_DOMAIN_CODE  and gdc.status<>'N' and gd.status<>'N' and gd.NETWORK_CODE=?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " SQL Query " + strBuff.toString());
            }
            pstmt = p_con.prepareStatement(strBuff.toString());
            pstmt.setString(1, networkCode);
            rs = pstmt.executeQuery();
            detailsList = new ArrayList<GeographicalDomainCellsVO>();
            while (rs.next()) {
                geogVO = new GeographicalDomainCellsVO();
                geogVO.setCellId(SqlParameterEncoder.encodeParams(rs.getString("GRPH_CELLID")));
                geogVO.setCellName(SqlParameterEncoder.encodeParams(rs.getString("GRPH_CELL_NAME")));
                geogVO.setGrphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_CODE")));
                geogVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("STATUS")));
                detailsList.add(geogVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception sqe) {
                _log.errorTrace(methodName, sqe);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting " + detailsList);
        }
        return detailsList;
    }

    /*
     * this method inserts data in the GEOGRAPHICAL_DOMAIN_CELLS table
     * 
     * @author Ashutosh
     */
    public void addGeogAndCellIdMapping(Connection p_con, ArrayList<GeographicalDomainCellsVO> p_cellIdVOList, MessageResources p_messages, Locale p_locale, UserVO p_userVO, String p_fileName) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addGeogAndCellIdMapping ", " Entered " + p_fileName);
        }
        int commitCounter = 0;
        PreparedStatement pstmtInsertCellID = null, pstmtDeleteCells = null;
        final StringBuilder deleteQuery = new StringBuilder("delete  FROM GEOGRAPHICAL_DOMAIN_CELLS");

        final StringBuilder insertCellIdTable = new StringBuilder("INSERT INTO GEOGRAPHICAL_DOMAIN_CELLS (GRPH_CELLID, GRPH_CELL_NAME, GRPH_DOMAIN_CODE, STATUS)");
        insertCellIdTable.append(" VALUES(?,?,?,?)");
        final String methodName = "addGeogAndCellIdMapping";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "InsertCellID Query =" + insertCellIdTable);
        }
        try {
            pstmtDeleteCells = p_con.prepareStatement(deleteQuery.toString());
            pstmtDeleteCells.executeUpdate();
            pstmtInsertCellID = p_con.prepareStatement(insertCellIdTable.toString());
            final ListSorterUtil sort = new ListSorterUtil();
            p_cellIdVOList = (ArrayList<GeographicalDomainCellsVO>) sort.doSort("cellId", null, p_cellIdVOList);
            GeographicalDomainCellsVO geogVO = null;
            int cellIdVOListSize = p_cellIdVOList.size();
            for (int i = 0; i < cellIdVOListSize; i++) {
                geogVO = p_cellIdVOList.get(i);
                pstmtInsertCellID.setString(1, geogVO.getCellId());
                pstmtInsertCellID.setString(2, geogVO.getCellName());
                pstmtInsertCellID.setString(3, geogVO.getGrphDomainCode());
                pstmtInsertCellID.setString(4, geogVO.getStatus());
                if (pstmtInsertCellID.executeUpdate() > 0) {
                    p_con.commit();
                    commitCounter++;
                    continue;
                } else {
                    p_con.rollback();
                    continue;
                }
            }
        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[addGeogAndCellIdMapping]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[addGeogAndCellIdMapping]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtInsertCellID != null) {
                    pstmtInsertCellID.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtDeleteCells != null) {
                	pstmtDeleteCells.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addGeogAndCellIdMapping ", " Exiting count for inserted cell id mapping = " + commitCounter);
        }
        return;
    }

    /**
     * Method for loading Geographical Domain Types.
     * 
     * Used in (UserAction, ChannelUserAction)
     * 
     * @author mohit.goel
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_sessionUserDomainSeqNo
     *            int
     * @param p_newUserDomainSeqNo
     *            int
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadDomainTypes(Connection p_con, int p_sessionUserDomainSeqNo, int p_newUserDomainSeqNo) throws BTSLBaseException {
        final String methodName = "loadDomainTypes";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  p_sessionUserDomainSeqNo=" + p_sessionUserDomainSeqNo, " p_newUserDomainSeqNo=" + p_newUserDomainSeqNo);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT grph_domain_type,grph_domain_type_name,grph_domain_parent, ");
        strBuff.append("controlling_unit, sequence_no FROM geographical_domain_types ");
        strBuff.append("WHERE sequence_no > ? AND sequence_no < ? ");
        strBuff.append("ORDER BY sequence_no ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setInt(1, p_sessionUserDomainSeqNo);
            pstmt.setInt(2, p_newUserDomainSeqNo);

            rs = pstmt.executeQuery();
            GeographicalDomainTypeVO typeVO = null;
            while (rs.next()) {
                typeVO = new GeographicalDomainTypeVO();
                typeVO.setGrphDomainType(rs.getString("grph_domain_type"));
                typeVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));
                typeVO.setGrphDomainParent(rs.getString("grph_domain_parent"));
                typeVO.setGrphDomainSequenceNo(rs.getString("sequence_no"));

                list.add(typeVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadDomainTypes]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadDomainTypes]", "", "",
                "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: geographyList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Geographical List.
     * 
     * Used in(userAction, ChannelUserAction)
     * 
     * @author mohit.goel
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_parentGrphDomainCode
     *            String
     * @param p_grphDomainName
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadGeographyList(Connection p_con, String p_networkCode, String p_parentGrphDomainCode, String p_grphDomainName) throws BTSLBaseException {
        final String methodName = "loadGeographyList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  p_networkCode=" + p_networkCode + " p_parentGrphDomainCode=" + p_parentGrphDomainCode + " p_grphDomainName=" + p_grphDomainName);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT gd.grph_domain_code, gd.grph_domain_name, gd.parent_grph_domain_code, gd.status,");
        strBuff.append(" gdt.grph_domain_type_name, gdt.sequence_no FROM geographical_domains gd,geographical_domain_types gdt ");
        strBuff.append(" WHERE gd.status <> 'N' AND gd.network_code = ? AND gd.parent_grph_domain_code = ? ");
        strBuff.append(" AND gdt.grph_domain_type = gd.grph_domain_type ");
        strBuff.append(" AND upper(grph_domain_name) like upper(?) ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_parentGrphDomainCode);
            pstmt.setString(3, p_grphDomainName);

            rs = pstmt.executeQuery();
            UserGeographiesVO geographyVO = null;
            while (rs.next()) {
                // ListValueVO vo = new


                geographyVO = new UserGeographiesVO();
                geographyVO.setGraphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_code")));
                geographyVO.setGraphDomainName(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_name")));
                geographyVO.setParentGraphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("parent_grph_domain_code")));
                geographyVO.setGraphDomainTypeName(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type_name")));
                geographyVO.setGraphDomainSequenceNumber(rs.getInt("sequence_no"));
                geographyVO.setStatus(rs.getString("status"));
                list.add(geographyVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadGeographyList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainWebDAO[loadGeographyList]", "", "",
                "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: geographyList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method loadGeographicalDomainTypeList.
     * This method is used to load geographical domain types
     * 
     * @param p_con
     *            Connection
     * @return geographicalDomainList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadGeographicalDomainTypeList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadGeographicalDomainTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList geographicalDomainList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT grph_domain_type,grph_domain_type_name");
        strBuff.append(" FROM geographical_domain_types WHERE sequence_no > 1 ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + sqlSelect);
            }

            while (rs.next()) {
                geographicalDomainList.add(new ListValueVO(rs.getString("grph_domain_type_name"), rs.getString("grph_domain_type")));
            }
        }

        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadGeographicalDomainTypeList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadGeographicalDomainTypeList]", "", "",
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
                _log.debug(methodName, "Exiting size=" + geographicalDomainList.size());
            }
        }

        return geographicalDomainList;
    }

    /**
     * Method loadGeographicalDomainTypeListBySequence.
     * This method is used to load geographical domain types
     * 
     * @param p_con
     *            Connection
     * @param p_sequenceNumber
     *            int
     * @return geographicalDomainList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList<ListValueVO> loadGeographicalDomainTypeListBySequence(Connection p_con, int p_sequenceNumber) throws BTSLBaseException {
        final String methodName = "loadGeographicalDomainTypeListBySequence";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList geographicalDomainList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT grph_domain_type,grph_domain_type_name,sequence_no");
        strBuff.append(" FROM geographical_domain_types WHERE sequence_no >= ? ORDER BY grph_domain_type_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setInt(1, p_sequenceNumber);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + sqlSelect);
            }

            while (rs.next()) {
                final ListValueVO listValueVO = new ListValueVO(rs.getString("grph_domain_type_name"), rs.getString("grph_domain_type"));
                listValueVO.setOtherInfo(rs.getString("sequence_no"));
                geographicalDomainList.add(listValueVO);
            }
        }

        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadGeographicalDomainTypeListBySequence]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadGeographicalDomainTypeListBySequence]",
                "", "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting size=" + geographicalDomainList.size());
            }
        }

        return geographicalDomainList;
    }
    
    /**
	 * Method to load network list for super network admin and super cce
	 * used in UserAction
	 * @param p_con
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList<UserGeographiesVO> loadNetworkList(Connection p_con) throws BTSLBaseException{
		
		final String methodName = "loadNetworkList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered " );
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuilder strBuff = new StringBuilder();

		strBuff.append("select distinct(network_code), grph_domain_name  from geographical_domains where parent_grph_domain_code=?");
		
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		ArrayList<UserGeographiesVO> list = new ArrayList<UserGeographiesVO>();
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1,PretupsI.ROOT_PARENT_ID);
			rs = pstmt.executeQuery();
			
			UserGeographiesVO geographyVO = null;
			while (rs.next()) {

				geographyVO = new UserGeographiesVO();
				geographyVO.setGraphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("network_code")));
				geographyVO.setGraphDomainName(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_name")));
				list.add(geographyVO);
			}
		} catch (SQLException sqe) {
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"GeographicalDomainWebDAO[loadNetworkList]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,"GeographicalDomainWebDAO[loadNetworkList]", "", "", "","Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.processing");
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
				_log.debug(methodName,
						"Exiting: networklist size=" + list.size());
			}
		}
		return list;
		}
	
	/**
	 * Method for loading Geographical List for super channel admin irrespective of network code
	 * 
	 * Used in(userAction)
	 * 
	 * @author mohit.goel
	 * 
	 * @param p_con
	 *            java.sql.Connection
	 * @return java.util.ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList<UserGeographiesVO> loadGeographyListForSuperChannelAdmin(Connection p_con, String grphDomainPrt)
			throws BTSLBaseException {
		final String methodName = "loadGeographyListForChannelAdmin";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuilder strBuff = new StringBuilder();

		strBuff.append("SELECT gd.grph_domain_code, gd.grph_domain_name, gd.parent_grph_domain_code,");
		strBuff.append(" gdt.grph_domain_type_name FROM geographical_domains gd,geographical_domain_types gdt ");
		strBuff.append(" WHERE gd.status <> 'N'");
		strBuff.append(" AND gdt.grph_domain_type = gd.grph_domain_type and gdt.GRPH_DOMAIN_PARENT=? ");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		ArrayList<UserGeographiesVO> list = new ArrayList<UserGeographiesVO>();
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, grphDomainPrt);
			rs = pstmt.executeQuery();
			UserGeographiesVO geographyVO = null;
			while (rs.next()) {

				geographyVO = new UserGeographiesVO();
				geographyVO.setGraphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_code")));
				geographyVO.setGraphDomainName(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_name")));
				geographyVO.setParentGraphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("parent_grph_domain_code")));
				geographyVO.setGraphDomainTypeName(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type_name")));
				list.add(geographyVO);
			}
		} catch (SQLException sqe) {
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,"GeographicalDomainWebDAO[loadGeographyListForChannelAdmin]", "", "", "","SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.sql.processing");
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,"GeographicalDomainWebDAO[loadGeographyListForChannelAdmin]", "", "", "","Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.processing");
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
				_log.debug(methodName,
						"Exiting: geographyList size=" + list.size());
			}
		}
		return list;
	}
	
	
	public ArrayList loadGeographyList(Connection p_con,String p_geographyDomainType) throws BTSLBaseException
	{
		final String methodName = "loadGeographyList";
		if (_log.isDebugEnabled())
		{
			_log.debug(methodName, "Entered  " +"p_geographyDomainType="+ p_geographyDomainType);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuilder strBuff = new StringBuilder();
		
		
		strBuff.append("SELECT gd.grph_domain_code, gd.grph_domain_name, gd.parent_grph_domain_code, gd.network_code, nt.network_name,");
		strBuff.append(" gdt.grph_domain_type_name FROM geographical_domains gd,geographical_domain_types gdt, networks nt ");
		strBuff.append(" WHERE gd.status <> 'N'");
		strBuff.append(" AND gdt.grph_domain_type = gd.grph_domain_type ");
		strBuff.append(" AND gd.GRPH_DOMAIN_TYPE = ? AND gd.NETWORK_CODE = nt.NETWORK_CODE ");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled())
		{
		    _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		ArrayList list = new ArrayList();
		try
		{
			pstmt = p_con.prepareStatement(sqlSelect);
			
			pstmt.setString(1, p_geographyDomainType);
			
			
			rs = pstmt.executeQuery();
			UserGeographiesVO geographyVO = null;
			while (rs.next())
			{

				
			    geographyVO = new UserGeographiesVO();
			    geographyVO.setGraphDomainCode(rs.getString("grph_domain_code"));
			    geographyVO.setGraphDomainName(rs.getString("grph_domain_name"));
			    geographyVO.setParentGraphDomainCode(rs.getString("parent_grph_domain_code"));
			    geographyVO.setGraphDomainTypeName(rs.getString("grph_domain_type_name"));
			    geographyVO.setNetworkName(rs.getString("network_name"));
			    
			    list.add(geographyVO);
			}
		} catch (SQLException sqe)
		{
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"GeographicalDomainWebDAO[loadGeographyList]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"GeographicalDomainWebDAO[loadGeographyList]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
			try{if (rs != null){rs.close();}} catch (Exception e){_log.errorTrace(methodName, e);}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){_log.errorTrace(methodName, e);}
			if (_log.isDebugEnabled())
			{
				_log.debug(methodName, "Exiting: geographyList size=" + list.size());
			}
		}
		return list;
	}
	
	/**
     * Method for loading User geogrophical domain data.(That are assigned to
     * the user)
     * From the table geographical_domains
     * 
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @param p_networkCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public UserGeographiesVO getGeographyDomainData(Connection p_con, String p_grphDomainCode) throws BTSLBaseException {
        final String methodName = "loadUserGeographyDomainData";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_grphDomainCode=" + p_grphDomainCode);
        }
        
      
        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT gd.parent_grph_domain_code, gd.is_default ");
        strBuff.append(" FROM geographical_domains gd ");
        strBuff.append("WHERE gd.grph_domain_code = ? ");
		

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        UserGeographiesVO geoVO = null;
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_grphDomainCode);
           try( ResultSet  rs = pstmt.executeQuery();)
           {
            while (rs.next()) {
            	geoVO = new UserGeographiesVO();
            	geoVO.setParentGraphDomainCode( rs.getString("parent_grph_domain_code") );
            	geoVO.setIsDefault( rs.getString("is_default") );
            }
        } 
        }catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: parent_grph_domain_code");
            }
        }
        return geoVO;
    }
    
    
    
    
    
    /**
     * Method for loading User geogrophical domain data.(That are assigned to
     * the user)
     * From the table geographical_domains
     * 
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @param p_networkCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public boolean isGeographyDomainTypeValid(Connection p_con, String p_grphDomainType) throws BTSLBaseException {
        final String methodName = "getGeographyDomainType";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_grphDomainType=" + p_grphDomainType);
        }
        
      
        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT gt.grph_domain_type, gt.grph_domain_type_name ");
        strBuff.append(" FROM geographical_domain_types gt ");
        strBuff.append("WHERE gt.grph_domain_type = ? ");
		

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        boolean validGeographyType =false;

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_grphDomainType);
           try( ResultSet  rs = pstmt.executeQuery();)
           {
            if (rs.next()) {
            	validGeographyType=true;
            }
        } 
        }catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainDAO[loadUserGeographyList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: parent_grph_domain_code");
            }
        }
        return validGeographyType;
    }

    
    
    public int loadParentGeographicalSequenceNumber(Connection p_con, String p_domainCode,String parentcategoryCode) throws BTSLBaseException {
        final String methodName = "loadCategoryGeographicalSequenceVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        final StringBuilder strBuff = new StringBuilder("SELECT G.sequence_no GRPH_SEQ ");
        strBuff.append("FROM categories C,geographical_domain_types G WHERE C.domain_code=? AND ");
        strBuff.append("C.grph_domain_type=G.grph_domain_type   AND ");
        strBuff.append("C.sequence_no = (SELECT max(sequence_no) FROM categories WHERE ");
        strBuff.append("domain_code=C.domain_code AND CATEGORY_CODE =?  AND category_type<>? )");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadGeographicalSequenceNumber", "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            pstmtSelect.setString(2, parentcategoryCode);
            pstmtSelect.setString(3, PretupsI.CATEGORY_TYPE_AGENT);
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {

                return rs.getInt("GRPH_SEQ");
            } else {
                return 2;
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryGeographicalSequenceVO]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadGeographicalSequenceNumber", "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryGeographicalSequenceVO]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadGeographicalSequenceNumber", "error.general.processing");

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
                _log.debug("loadGeographicalSequenceNumber", "Exiting ");
            }
        }
    }


    
    
    

    
    /**
     * Method loadGeographicalDomainTypeListBySequence.
     * This method is used to load geographical domain types
     * 
     * @param p_con
     *            Connection
     * @param p_sequenceNumber
     *            int
     * @return geographicalDomainList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList<ListValueVO> sortGeographicalDomainTypeListBySequenceNo(Connection p_con, int p_sequenceNumber) throws BTSLBaseException {
        final String methodName = "sortGeographicalDomainTypeListBySequenceNo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList geographicalDomainList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder("SELECT grph_domain_type,grph_domain_type_name,sequence_no");
        strBuff.append(" FROM geographical_domain_types WHERE sequence_no >= ? ORDER BY sequence_no ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setInt(1, p_sequenceNumber);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + sqlSelect);
            }

            while (rs.next()) {
                final ListValueVO listValueVO = new ListValueVO(rs.getString("grph_domain_type_name"), rs.getString("grph_domain_type"));
                listValueVO.setOtherInfo(rs.getString("sequence_no"));
                geographicalDomainList.add(listValueVO);
            }
        }

        catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[sortGeographicalDomainTypeListBySequenceNo]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[sortGeographicalDomainTypeListBySequenceNo]",
                "", "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting size=" + geographicalDomainList.size());
            }
        }

        return geographicalDomainList;
    }

    
	
}
