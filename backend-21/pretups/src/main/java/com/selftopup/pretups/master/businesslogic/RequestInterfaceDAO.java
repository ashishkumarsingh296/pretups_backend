package com.selftopup.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;

import com.selftopup.loadcontroller.NetworkLoadVO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

/*
 * RequestInterfaceDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 30/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Request interface data access object class for interaction with the database
 */

public class RequestInterfaceDAO {

    private static Log _log = LogFactory.getLog(RequestInterfaceDAO.class.getName());

    public HashMap loadRequestInterfaceDetails() throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadRequestInterfaceDetails", "Entered ");
        PreparedStatement pstmtSelect = null;
        HashMap requestInterfaceMap = new HashMap();
        RequestInterfaceDetailVO requestInterfaceDetailVO = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT req_interface_code,req_interface_name,protocol,host,service_port,login_id,password,auth_type ");
            selectQueryBuff.append(" request_handler,encryption_level,encryption_key,content_type,created_on,created_by,modified_by,modified_on ");
            selectQueryBuff.append(" FROM req_interface_detail ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadRequestInterfaceDetails", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                requestInterfaceDetailVO = new RequestInterfaceDetailVO();
                requestInterfaceDetailVO.setReqInterfaceCode(rs.getString("req_interface_code"));
                requestInterfaceDetailVO.setReqInterfaceName(rs.getString("req_interface_name"));
                requestInterfaceDetailVO.setProtocol(rs.getString("protocol"));
                requestInterfaceDetailVO.setHost(rs.getString("host"));
                requestInterfaceDetailVO.setServicePort(rs.getString("service_port"));
                requestInterfaceDetailVO.setLoginID(rs.getString("login_id"));
                requestInterfaceDetailVO.setPassword(rs.getString("password"));
                requestInterfaceDetailVO.setAuthType(rs.getString("auth_type"));
                requestInterfaceDetailVO.setRequestHandler(rs.getString("request_handler"));
                requestInterfaceDetailVO.setEncryptionLevel(rs.getString("encryption_level"));
                requestInterfaceDetailVO.setEncryptionKey(rs.getString("encryption_key"));
                requestInterfaceDetailVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                requestInterfaceDetailVO.setCreatedBy(rs.getString("created_by"));
                requestInterfaceDetailVO.setModifiedBy(rs.getString("modified_by"));
                requestInterfaceDetailVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                requestInterfaceMap.put(rs.getString("req_interface_code"), requestInterfaceDetailVO);
            }// end while
            return requestInterfaceMap;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadRequestInterfaceDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw sqle;
        }// end of catch
        catch (Exception e) {
            _log.error("loadRequestInterfaceDetails", "Exception " + e.getMessage());
            e.printStackTrace();
            throw e;
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadRequestInterfaceDetails", "Exiting requestInterfaceMap.size:" + requestInterfaceMap.size());
        }// end of finally
    }
}
