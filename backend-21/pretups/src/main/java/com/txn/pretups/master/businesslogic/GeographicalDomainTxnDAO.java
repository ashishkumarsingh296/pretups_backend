package com.txn.pretups.master.businesslogic;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.user.businesslogic.UserGeographiesVO;

public class GeographicalDomainTxnDAO {

    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private Log _log = LogFactory.getLog(GeographicalDomainTxnDAO.class.getName());
    private GeographicalDomainTxnQry geographicalDomainTxnQry ; 
   /**
     * Constructor for GeographicalDomainDAO.
     */
    public GeographicalDomainTxnDAO() {
        super();
        geographicalDomainTxnQry = (GeographicalDomainTxnQry)ObjectProducer.getObject(QueryConstants.GEO_DOMAIN_TXN_QRY, QueryConstants.QUERY_PRODUCER);
    }

    /**
     * This method will load Geographical Domain Code Heirarchy.
     * 
     * @param p_con
     * @param p_geodomaintype
     * @param p_geodomainCode
     * @param p_isTopToBottom
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public ArrayList loadGeoDomainCodeHeirarchyForOpt(Connection p_con, String p_geodomaintype, String p_geodomainCodes, boolean p_isTopToBottom) throws BTSLBaseException {

        final String methodName = "loadGeoDomainCodeHeirarchyForOpt";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:p_geodomaintype=");
        	loggerValue.append(p_geodomaintype);
        	loggerValue.append("p_geodomainCodes");
        	loggerValue.append(p_geodomainCodes);
            _log.debug(methodName, loggerValue);
        }
        ArrayList domainParentList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        GeographicalDomainVO geographicalDomainVO = null;
        ResultSet rs = null;
        try {
            pstmtSelect=geographicalDomainTxnQry.loadGeoDomainCodeHeirarchyForOptQry(p_con, p_isTopToBottom, p_geodomainCodes, p_geodomaintype);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                geographicalDomainVO = new GeographicalDomainVO();
                geographicalDomainVO.setGrphDomainCode(rs.getString("GRPH_DOMAIN_CODE"));
                geographicalDomainVO.setNetworkCode(rs.getString("NETWORK_CODE"));
                geographicalDomainVO.setGrphDomainName(rs.getString("GRPH_DOMAIN_NAME"));
                geographicalDomainVO.setParentDomainCode(rs.getString("PARENT_GRPH_DOMAIN_CODE"));
                geographicalDomainVO.setGrphDomainShortName(rs.getString("GRPH_DOMAIN_SHORT_NAME"));
                geographicalDomainVO.setDescription(rs.getString("DESCRIPTION"));
                geographicalDomainVO.setStatus(rs.getString("STATUS"));
                geographicalDomainVO.setGrphDomainType(rs.getString("GRPH_DOMAIN_TYPE"));
                geographicalDomainVO.setCreatedOn(rs.getDate("CREATED_ON"));
                geographicalDomainVO.setCreatedBy(rs.getString("CREATED_BY"));
                geographicalDomainVO.setModifiedOn(rs.getDate("MODIFIED_ON"));
                geographicalDomainVO.setModifiedBy(rs.getString("MODIFIED_BY"));
                geographicalDomainVO.setIsDefault(rs.getString("is_default"));
                domainParentList.add(geographicalDomainVO);
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeoDomainCodeHeirarchyForOpt]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeoDomainCodeHeirarchyForOpt]", "", "", "", "Exception:" + e.getMessage());
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
     * This method loads the list of geographies for the owner user
     * 
     * @param p_con
     * @param p_categoryCode
     * @throws BTSLBaseException
     * @return ArrayList
     * @author ankur.dhawan
     */
    public ArrayList loadGeographiesForOwner(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadGeographiesForOwner";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_categoryCode=" + p_categoryCode);
        }

        ArrayList geoList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ListValueVO listVO = null;
        ResultSet rs = null;

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT gd.grph_domain_code,gd.grph_domain_name ");
        strBuff.append("FROM geographical_domains gd WHERE gd.status='Y' AND gd.grph_domain_type = ");
        strBuff.append("(SELECT c.grph_domain_type FROM categories c WHERE category_code=?) ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadGeographiesForOwner," + "Query : ", sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_categoryCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("grph_domain_name"), rs.getString("grph_domain_code"));
                geoList.add(listVO);
            }
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeographiesForOwner]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeographiesForOwner]", "", "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting: geoList.size= " + geoList.size());
            }
        }
        return geoList;
    }

    /**
     * This method loads the list of geographies based on parent geography
     * 
     * @param p_con
     * @param p_geoCode
     * @param p_categoryCode
     * @throws BTSLBaseException
     * @return ArrayList
     * @author ankur.dhawan
     */
    public ArrayList loadGeographiesForAPI(Connection p_con, String p_geoCode, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadGeographiesForAPI";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:p_geoCode=");
        	loggerValue.append(p_geoCode);
        	loggerValue.append(" p_categoryCode=");
        	loggerValue.append(p_categoryCode);
            _log.debug(methodName, loggerValue);
        }

        ArrayList geoList = new ArrayList();
        ListValueVO listVO = new ListValueVO();
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelectGeography = null;
        ResultSet rs = null;
        ResultSet rsGeography = null;
        int i;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT (1) FROM chnl_transfer_rules ctr WHERE ctr.parent_association_allowed='Y' ");
        strBuff.append("AND ctr.to_category=? AND ctr.from_category in ");
        strBuff.append("(SELECT c.category_code FROM categories c,geographical_domains gd ");
        strBuff.append("WHERE c.grph_domain_type=gd.grph_domain_type AND c.domain_code= ");
        strBuff.append("(SELECT c.domain_code FROM categories c WHERE category_code=?)  ");
        strBuff.append("AND gd.grph_domain_code=?) ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadGeographiesForAPI," + "Query : ", sqlSelect);
        }
        strBuff.delete(0, strBuff.length());


        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            i = 1;
            pstmtSelect.setString(i, p_categoryCode);
            pstmtSelect.setString(++i, p_categoryCode);
            pstmtSelect.setString(++i, p_geoCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
               
                pstmtSelectGeography=geographicalDomainTxnQry.loadGeographiesForAPIQry(p_con, p_geoCode, p_categoryCode);
                rsGeography = pstmtSelectGeography.executeQuery();
                if (rsGeography.next()) {
                    listVO = new ListValueVO(rsGeography.getString("grph_domain_name"), rsGeography.getString("grph_domain_code"));
                    geoList.add(listVO);
                }
            } else {
                String strarr[] = { p_categoryCode };
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_GRPH_HIERARCHY_ERROR, 0, strarr, null);
            }

        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeographiesForAPI]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (BTSLBaseException e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeographiesForAPI]", "", "", "", "Exception:" + e.getMessage());
            throw e;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeographiesForAPI]", "", "", "", "Exception:" + e.getMessage());
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
                if (rsGeography != null) {
                	rsGeography.close();
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
                _log.debug(methodName, "Exiting: geoList.size= " + geoList.size());
            }
        }
        return geoList;
    }

    /**
     * This method loads the list of geographies by parent msisdn
     * 
     * @param p_con
     * @param p_categoryCode
     * @param p_msisdn
     * @throws BTSLBaseException
     * @return ArrayList
     * @author ankur.dhawan
     */
    public ArrayList loadGeographiesForAPIByParent(Connection p_con, String p_msisdn, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadGeographiesForAPIByParent";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(" p_categoryCode=");
        	loggerValue.append(p_categoryCode);
            _log.debug(methodName,loggerValue);
        }

        ArrayList geoList = new ArrayList();
        ListValueVO listVO = null;
        PreparedStatement pstmtSelectParent = null;
        PreparedStatement pstmtSelectGeography = null;
        ResultSet rsParent = null;
        ResultSet rsGeography = null;

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT distinct ug.grph_domain_code as parent_grph_domain_code,gd.grph_domain_type as parent_grph_domain_type,gd.grph_domain_name as parent_grph_domain_name ");
        strBuff.append("FROM chnl_transfer_rules ctr,users u,user_geographies ug,geographical_domains gd ");
        strBuff.append("WHERE u.user_id=ug.user_id AND u.category_code=ctr.from_category ");
        strBuff.append("AND ug.grph_domain_code=gd.grph_domain_code ");
        strBuff.append("AND u.msisdn='" + p_msisdn + "' AND ctr.to_category='" + p_categoryCode + "' AND ctr.parent_association_allowed='Y' ");
        String sqlSelectParent = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadGeographiesForAPIByParent," + "Query : ", sqlSelectParent);
        }
        strBuff.delete(0, strBuff.length());

        try {
            pstmtSelectParent = p_con.prepareStatement(sqlSelectParent);
            rsParent = pstmtSelectParent.executeQuery();
            int i;
            if (rsParent.next()) {
                // if parent geography type is sub area then return the same
                // area
                if (rsParent.getString("parent_grph_domain_type").equals(PretupsI.SUB_AREA_TYPE)) {
                    listVO = new ListValueVO(rsParent.getString("parent_grph_domain_name"), rsParent.getString("parent_grph_domain_code"));
                    geoList.add(listVO);
                } else {
                    String rsSelectParent=rsParent.getString("parent_grph_domain_code");
                    pstmtSelectGeography=geographicalDomainTxnQry.loadGeographiesForAPIByParentQry(p_con, rsSelectParent, p_categoryCode);
                    rsGeography = pstmtSelectGeography.executeQuery();
                    while (rsGeography.next()) {
                        listVO = new ListValueVO(rsGeography.getString("grph_domain_name"), rsGeography.getString("grph_domain_code"));
                        geoList.add(listVO);
                    }
                }
            } else {
                String strarr[] = { p_categoryCode };
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_GRPH_HIERARCHY_ERROR, 0, strarr, null);
            }
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeographiesForAPIByParent]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeographiesForAPIByParent]", "", "", "", "Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeographiesForAPIByParent]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rsParent != null) {
                    rsParent.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (rsGeography != null) {
                    rsGeography.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectParent != null) {
                    pstmtSelectParent.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelectGeography != null) {
                    pstmtSelectGeography.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: geoList.size= " + geoList.size());
            }
        }
        return geoList;
    }

    /**
     * This method validates whether the geography code for the external channel
     * user exists
     * and is at the appropriate level
     * 
     * @param p_con
     * @param p_geoCode
     * @param p_categoryCode
     * @throws BTSLBaseException
     * @return boolean
     * @author ankur.dhawan
     */
    public boolean validateGeography(Connection p_con, String p_geoCode, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "validateGeography";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_geoCode=" + p_geoCode + " p_categoryCode=" + p_categoryCode);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isValid = false;

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT (1) FROM geographical_domains gd,categories c ");
        strBuff.append("WHERE c.grph_domain_type=gd.grph_domain_type AND gd.grph_domain_code=? ");
        strBuff.append("AND c.category_code=? ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("validateGeographyOfParent," + "Query : ", sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i, p_geoCode);
            pstmtSelect.setString(++i, p_categoryCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isValid = true;
            }
        } catch (SQLException sqle) {
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[validateGeography]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[validateGeography]", "", "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting: isValid=" + isValid);
            }
        }
        return isValid;
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
    public ArrayList loadDefaultGeographyList(Connection p_con, String p_networkCode, String p_parentGrphDomainCode, String p_grphDomainName) throws BTSLBaseException {
        final String methodName = "loadGeographyList";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_parentGrphDomainCode=");
        	loggerValue.append(p_parentGrphDomainCode);
        	loggerValue.append(" p_grphDomainName=");
        	loggerValue.append(p_grphDomainName);
            _log.debug(methodName,loggerValue);
        }
            StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT gd.grph_domain_code, gd.grph_domain_name, gd.parent_grph_domain_code,");
        strBuff.append(" gdt.grph_domain_type_name FROM geographical_domains gd,geographical_domain_types gdt ");
        strBuff.append(" WHERE gd.status <> 'N' AND gd.network_code = ? AND gd.parent_grph_domain_code = ? ");
        strBuff.append(" AND gdt.grph_domain_type = gd.grph_domain_type ");
        strBuff.append(" AND upper(grph_domain_name) like upper(?) and IS_DEFAULT=? ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_parentGrphDomainCode);
            pstmt.setString(3, p_grphDomainName);
            pstmt.setString(4, PretupsI.YES);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            UserGeographiesVO geographyVO = null;
            while (rs.next()) {
                // ListValueVO vo = new
                // ListValueVO(rs.getString("grph_domain_name"),rs.getString("grph_domain_code"));

                geographyVO = new UserGeographiesVO();
                geographyVO.setGraphDomainCode(rs.getString("grph_domain_code"));
                geographyVO.setGraphDomainName(rs.getString("grph_domain_name"));
                geographyVO.setParentGraphDomainCode(rs.getString("parent_grph_domain_code"));
                geographyVO.setGraphDomainTypeName(rs.getString("grph_domain_type_name"));

                list.add(geographyVO);
            }
        } 
        }catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeographyList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeographyList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: geographyList size=" + list.size());
            }
        }
        return list;
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
    public ArrayList loadParentGeographicalDomainCode(Connection p_con, String p_geodomaintype) throws BTSLBaseException {
        final String methodName = "loadParentGeographicalDomainCode";
        if (_log.isDebugEnabled()) {
            _log.debug("loadGeoDomainCode", "Entered:p_geodomaintype=" + p_geodomaintype);
        }
        ArrayList domainParentList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        GeographicalDomainVO geographicalDomainVO = null;
        ResultSet rs = null;
        try {

            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT PARENT_GRPH_DOMAIN_CODE ");
            selectQuery.append("FROM geographical_domains ");
            selectQuery.append("WHERE GRPH_DOMAIN_TYPE =? and status=?");
            if (_log.isDebugEnabled()) {
                _log.debug("loadGeoDomainCode", "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_geodomaintype);
            pstmtSelect.setString(2, PretupsI.YES);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                domainParentList.add(rs.getString("PARENT_GRPH_DOMAIN_CODE"));
                break;
            }
        } catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeoDomainCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadDomainList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GeographicalDomainTxnDAO[loadGeoDomainCode]", "", "", "", "Exception:" + e.getMessage());
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
}
