/**
 * @(#)LookupsDAO.java
 *                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 * 
 *                     <description>
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     avinash.kamthan Mar 13, 2005 Initital Creation
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 * 
 */

package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.OracleUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class LookupsDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * load the lookups with lookup types.
     * 
     * lookupTypes is key and lookups is List associated
     * 
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap<String,ArrayList<LookupsVO>> loadLookups() throws BTSLBaseException {
        final String METHOD_NAME = "loadLookups";
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookups()", " Entered ");
        }
        Connection con = null;
        HashMap<String,ArrayList<LookupsVO>> map = null;
        try {
            con = OracleUtil.getSingleConnection();
            map = this.loadLookupsTypeList(con);
            ArrayList lookupList = this.loadLookupsList(con);
            /**
             * Associate the looktype in map with lookup arraylist
             */
            if (lookupList != null) {
                LookupsVO lookupsVO = null;
                ArrayList tempList = null;
                for (int i = 0, k = lookupList.size(); i < k; i++) {
                    lookupsVO = (LookupsVO) lookupList.get(i);
                    if (map.containsKey(lookupsVO.getLookupType())) {
                        tempList = (ArrayList) map.get(lookupsVO.getLookupType());
                        tempList.add(lookupsVO);
                    }
                }
            }
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error("loadLookups()", "Exception: " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "loadLookups()", "error.general.processing");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadLookups()", "Exited: Map size=" + map.size());
            }
        }
        return map;
    }

    /**
     * load the lookuptypesList
     * 
     * @param p_con
     * @return HashMap
     * @throws BTSLBaseException
     */
    private HashMap loadLookupsTypeList(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadLookupsTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookupsTypeList()", "Entered ");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap lookupTypeMap = new HashMap();
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT lookup_type FROM lookup_types");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookupsTypeList()", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                lookupTypeMap.put(rs.getString("lookup_type"), new ArrayList());
            }
        } catch (SQLException sqe) {
            _log.error("loadLookupsTypeList()", " SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "loadLookupsTypeList()", "error.general.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error("loadLookupsTypeList()", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "loadLookupsTypeList()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadLookupsTypeList()", "Exiting: networkMap size=" + lookupTypeMap.size());
            }
        }
        return lookupTypeMap;
    }

    /**
     * 
     * @param p_con
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadLookupsList(Connection p_con) throws BTSLBaseException {

        final String METHOD_NAME = "loadLookupsList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookupsType()", "Entered ");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList lookupList = new ArrayList();
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT lookup_code,lookup_name, lookup_type, modified_on, status");
        strBuff.append(" FROM lookups WHERE status <> 'N'   ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookupsType()", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            LookupsVO lookupsVO = null;
            while (rs.next()) {
                lookupsVO = new LookupsVO();
                lookupsVO.setLookupCode(rs.getString("lookup_code"));
                lookupsVO.setLookupName(rs.getString("lookup_name"));
                lookupsVO.setLookupType(rs.getString("lookup_type"));
                lookupsVO.setModifiedOn(rs.getTimestamp("modified_on"));
                lookupsVO.setStatus(rs.getString("status"));
                lookupList.add(lookupsVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadLookupsType()", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException(this, "loadLookupsType()", "error.general.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error("loadLookupsType()", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(this, "loadLookupsType()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadLookupsType()", "Exiting: networkMap size=" + lookupList.size());
            }
        }
        return lookupList;
    }


/**
 * 
 * @param p_con
 * @lookup_code
 * @lookup_type (can be null)
 * @return ArrayList
 * @throws BTSLBaseException
 */
public ArrayList loadLookupsFromLookupCode(Connection p_con, String lookup_code,String lookup_type) throws BTSLBaseException {

    final String METHOD_NAME = "loadLookupsFromLookupCode";
    if (_log.isDebugEnabled()) {
        _log.debug("loadLookupsFromLookupCode()", "Entered ");
    }
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ArrayList lookupList = new ArrayList();
    StringBuffer strBuff = new StringBuffer();
    strBuff.append(" SELECT lookup_code,lookup_name, lookup_type, modified_on, status");
    strBuff.append(" FROM lookups WHERE ");
    strBuff.append(" lookup_code = ? ");
    if(lookup_type != null);{
    	strBuff.append(" and lookup_type = ? ");
    }
    
    String sqlSelect = strBuff.toString();
    if (_log.isDebugEnabled()) {
        _log.debug("loadLookupsFromLookupCode()", "QUERY sqlSelect=" + sqlSelect);
    }
    try {
        pstmt = p_con.prepareStatement(sqlSelect);
        pstmt.setString(1, lookup_code);
        if(lookup_type != null) {
        	pstmt.setString(2, lookup_type);
        }
        rs = pstmt.executeQuery();
        LookupsVO lookupsVO = null;
        while (rs.next()) {
            lookupsVO = new LookupsVO();
            lookupsVO.setLookupCode(rs.getString("lookup_code"));
            lookupsVO.setLookupName(rs.getString("lookup_name"));
            lookupsVO.setLookupType(rs.getString("lookup_type"));
            lookupsVO.setModifiedOn(rs.getTimestamp("modified_on"));
            lookupsVO.setStatus(rs.getString("status"));
            lookupList.add(lookupsVO);
        }
    } catch (SQLException sqe) {
        _log.error("loadLookupsFromLookupCode()", "SQLException : " + sqe);
        _log.errorTrace(METHOD_NAME, sqe);
        throw new BTSLBaseException(this, "loadLookupsFromLookupCode()", "error.general.processing");
    } catch (Exception ex) {
        if (ex instanceof BTSLBaseException) {
            throw (BTSLBaseException) ex;
        }
        _log.error("loadLookupsFromLookupCode()", "Exception : " + ex);
        _log.errorTrace(METHOD_NAME, ex);
        throw new BTSLBaseException(this, "loadLookupsFromLookupCode()", "error.general.processing");
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookupsFromLookupCode()", "Exiting: networkMap size=" + lookupList.size());
        }
    }
    return lookupList;
}

/**
 * 
 * @param p_con
 * @lookup_type (can be null)
 * @return ArrayList
 * @throws BTSLBaseException
 */
public ArrayList loadLookupsFromLookupType(Connection p_con,String lookup_type) throws BTSLBaseException {

    final String METHOD_NAME = "loadLookupsFromLookupType";
    if (_log.isDebugEnabled()) {
        _log.debug("loadLookupsFromLookupCode()", "Entered ");
    }
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ArrayList lookupList = new ArrayList();
    StringBuffer strBuff = new StringBuffer();
    strBuff.append(" SELECT lookup_code,lookup_name, lookup_type, modified_on, status");
    strBuff.append(" FROM lookups WHERE ");
    strBuff.append(" lookup_type = ? ");
    
    String sqlSelect = strBuff.toString();
    if (_log.isDebugEnabled()) {
        _log.debug("loadLookupsFromLookupCode()", "QUERY sqlSelect=" + sqlSelect);
    }
    try {
        pstmt = p_con.prepareStatement(sqlSelect);
        pstmt.setString(1, lookup_type);
        rs = pstmt.executeQuery();
        LookupsVO lookupsVO = null;
        while (rs.next()) {
            lookupsVO = new LookupsVO();
            lookupsVO.setLookupCode(rs.getString("lookup_code"));
            lookupsVO.setLookupName(rs.getString("lookup_name"));
            lookupsVO.setLookupType(rs.getString("lookup_type"));
            lookupsVO.setModifiedOn(rs.getTimestamp("modified_on"));
            lookupsVO.setStatus(rs.getString("status"));
            lookupList.add(lookupsVO);
        }
    } catch (SQLException sqe) {
        _log.error("loadLookupsFromLookupType()", "SQLException : " + sqe);
        _log.errorTrace(METHOD_NAME, sqe);
        throw new BTSLBaseException(this, "loadLookupsFromLookupType()", "error.general.processing");
    } catch (Exception ex) {
        if (ex instanceof BTSLBaseException) {
            throw (BTSLBaseException) ex;
        }
        _log.error("loadLookupsFromLookupType()", "Exception : " + ex);
        _log.errorTrace(METHOD_NAME, ex);
        throw new BTSLBaseException(this, "loadLookupsFromLookupType()", "error.general.processing");
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadLookupsFromLookupType()", "Exiting: networkMap size=" + lookupList.size());
        }
    }
    return lookupList;
}
}
