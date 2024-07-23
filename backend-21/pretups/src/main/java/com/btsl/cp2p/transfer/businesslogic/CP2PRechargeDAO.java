package com.btsl.cp2p.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class CP2PRechargeDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method will search whether the services are available and their
     * status is Y or not
     * 
     * @param p_con
     * @param p_module
     * @param p_services
     * @return boolean
     * @throws BTSLBaseException
     * @throws SQLException
     */
    public boolean isServicesAvailable(Connection p_con, String p_module, String p_service) throws BTSLBaseException, SQLException {
        if (_log.isDebugEnabled())
            _log.debug("isServicesAvailable", "Entered :" + p_module + " : " + p_service);
        final String METHOD_NAME = "isServicesAvailable";
        boolean isServicesExist = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT st.service_type,st.status,st.type FROM service_type st ");
            strBuff.append("WHERE st.status='Y' AND st.service_type=? AND st.module=?");
            String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("isServicesAvailable", "selectQuery :" + selectQuery);
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_service);
            pstmt.setString(2, p_module);
            rs = pstmt.executeQuery();
            if (rs != null) {
                isServicesExist = true;
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        finally{
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  _log.error("An error occurred closing statement.", e);
              }
        	if (_log.isDebugEnabled())
                _log.debug("isServicesAvailable", "Exiting :" + isServicesExist);
        }

        return isServicesExist;
    }
}
