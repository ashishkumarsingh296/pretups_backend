package com.btsl.pretups.network.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
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
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

/**
 * @(#)NetworkDAO.java
 *                     Copyright(c) 2005, Bharti Telesoft Ltd. All Rights
 *                     Reserved
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Mohit Goel 26/05/2005 Initial Creation Avinash
 *                     10/03/2005 modify load This class is used for
 *                     Insertion,Deletion,Updation and Selection of the
 *                     Networks
 */
public class NetworkDAO {
    /**
     * Commons Logging instance.
     */
    private final Log log = LogFactory.getLog(this.getClass().getName());

    public static NetworkDAO getInstance(){
    	return new NetworkDAO();
    }
    /**
     * Method for loading Network List fo cache.
     * 
     * @param p_con
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap<String,NetworkVO> loadNetworksCache() throws BTSLBaseException {

        final String methodName = "loadNetworksCache";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        

        HashMap<String,NetworkVO> networkMap = new HashMap<String,NetworkVO>();

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT network_name,network_code, ");
        strBuff.append("network_short_name,company_name, ");
        strBuff.append("erp_network_code, ");
        strBuff.append("status, language_1_message, ");
        strBuff.append("language_2_message, ");
        strBuff.append("modified_on FROM networks WHERE status <> 'N' ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadNetworksCache", "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
           try( PreparedStatement pstmt = con.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();)
           {
            NetworkVO networkVO = null;
            while (rs.next()) {

                networkVO = new NetworkVO();
                networkVO.setNetworkName(rs.getString("network_name"));
                networkVO.setNetworkCode(rs.getString("network_code"));
                networkVO.setNetworkShortName(rs.getString("network_short_name"));
                networkVO.setCompanyName(rs.getString("company_Name"));
                networkVO.setErpNetworkCode(rs.getString("erp_network_code"));
                networkVO.setStatus(rs.getString("status"));
                networkVO.setLanguage1Message(rs.getString("language_1_message"));
                networkVO.setLanguage2Message(rs.getString("language_2_message"));
                networkVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));

                networkMap.put(networkVO.getNetworkCode(), networkVO);
            }

        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworksCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworksCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	OracleUtil.closeQuietly(con);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: networkMap size=" + networkMap.size());
            }
        }
        return networkMap;
    }

    /**
     * Method for loading Network List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_status
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadNetworkList(Connection p_con, String p_status) throws BTSLBaseException {

        final String methodName = "loadNetworkList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_status" + p_status);
        }

        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT network_name,network_code,");
        strBuff.append(" network_short_name,company_name,report_header_name, ");
        strBuff.append(" erp_network_code,address1,address2,city,state,zip_code, ");
        strBuff.append("country,network_type,status,remarks,language_1_message, ");
        strBuff.append(" language_2_message,text_1_value,text_2_value,country_prefix_code,service_set_id, ");
        strBuff.append("created_by, modified_by, created_on,");
        strBuff.append(" modified_on FROM networks WHERE status not in(?) ");
        strBuff.append(" ORDER BY network_name");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList list = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
           
        	pstmt = p_con.prepareStatement(sqlSelect);
        	pstmt.setString(1, p_status);
        	rs = pstmt.executeQuery();

            NetworkVO networkVO = null;

            while (rs.next()) {

                networkVO = new NetworkVO();
                networkVO.setNetworkName(rs.getString("network_name"));
                networkVO.setNetworkCode(rs.getString("network_code"));
                networkVO.setNetworkShortName(rs.getString("network_short_name"));
                networkVO.setCompanyName(rs.getString("company_Name"));
                networkVO.setReportHeaderName(rs.getString("report_header_name"));
                networkVO.setErpNetworkCode(rs.getString("erp_network_code"));
                networkVO.setAddress1(rs.getString("address1"));
                networkVO.setAddress2(rs.getString("address2"));
                networkVO.setCity(rs.getString("city"));
                networkVO.setState(rs.getString("state"));
                networkVO.setZipCode(rs.getString("zip_code"));
                networkVO.setCountry(rs.getString("country"));
                networkVO.setNetworkType(rs.getString("network_type"));
                networkVO.setStatus(rs.getString("status"));
                networkVO.setRemarks(rs.getString("remarks"));
                networkVO.setLanguage1Message(rs.getString("language_1_message"));
                networkVO.setLanguage2Message(rs.getString("language_2_message"));
                networkVO.setText1Value(rs.getString("text_1_value"));
                networkVO.setText2Value(rs.getString("text_2_value"));
                networkVO.setCountryPrefixCode(rs.getString("country_prefix_code"));
                networkVO.setServiceSetID(rs.getString("service_set_id"));
                networkVO.setCreatedBy(rs.getString("created_by"));
                networkVO.setModifiedBy(rs.getString("modified_by"));
                networkVO.setCreatedOn(rs.getDate("created_on"));
                networkVO.setModifiedOn(rs.getDate("modified_on"));
                networkVO.setLastModified(rs.getTimestamp("modified_on").getTime());

                list.add(networkVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
				try {
					if(rs!=null)
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	
				try {
					if(pstmt!=null)
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: networkMap size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading particular Network Detail.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @return networkVO NetworkVO
     * @exception BTSLBaseException
     */
    public NetworkVO loadNetwork(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadNetwork";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: networkCode=" + p_networkCode);
        }

        
         
        NetworkVO networkVO = null;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT network_name,network_code,");
        strBuff.append("network_short_name,company_name,report_header_name,");
        strBuff.append("erp_network_Code,address1,address2,city,state,zip_code, ");
        strBuff.append("country,network_type,status,remarks,language_1_message, ");
        strBuff.append("language_2_message,text_1_value,text_2_value,country_prefix_code,service_set_id, ");
        strBuff.append("created_by, modified_by, created_on,");
        strBuff.append("modified_on FROM networks WHERE network_code = ?");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_networkCode);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            if (rs.next()) {
                networkVO = new NetworkVO();
                networkVO.setNetworkName(rs.getString("network_name"));
                networkVO.setNetworkCode(rs.getString("network_code"));
                networkVO.setNetworkShortName(rs.getString("network_short_name"));
                networkVO.setCompanyName(rs.getString("company_name"));
                networkVO.setReportHeaderName(rs.getString("report_header_name"));
                networkVO.setErpNetworkCode(rs.getString("erp_network_code"));
                networkVO.setAddress1(rs.getString("address1"));
                networkVO.setAddress2(rs.getString("address2"));
                networkVO.setCity(rs.getString("city"));
                networkVO.setState(rs.getString("state"));
                networkVO.setZipCode(rs.getString("zip_code"));
                networkVO.setCountry(rs.getString("country"));
                networkVO.setNetworkType(rs.getString("network_type"));
                networkVO.setStatus(rs.getString("status"));
                networkVO.setRemarks(rs.getString("remarks"));
                networkVO.setLanguage1Message(rs.getString("language_1_message"));
                networkVO.setLanguage2Message(rs.getString("language_2_message"));
                networkVO.setText1Value(rs.getString("text_1_value"));
                networkVO.setText2Value(rs.getString("text_2_value"));
                networkVO.setCountryPrefixCode(rs.getString("country_prefix_code"));
                networkVO.setServiceSetID(rs.getString("service_set_id"));
                networkVO.setCreatedBy(rs.getString("created_by"));
                networkVO.setModifiedBy(rs.getString("modified_by"));
                networkVO.setCreatedOn(rs.getDate("created_on"));
                networkVO.setModifiedOn(rs.getDate("modified_on"));
                networkVO.setLastModified(rs.getTimestamp("modified_on").getTime());
            }

            return networkVO;
        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetwork]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetwork]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: networkVO =" + networkVO);
            }
        }
    }

    /**
     * ********************************** Network Prefixes
     * ******************************
     */

    /**
     * Method for loading Network Prefix List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadNetworkPrefixList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadNetworkPrefixList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

         
        
        ArrayList networkList = new ArrayList();
        StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT network_code,");
        strBuff.append("prefix_id,series,operator,series_type ");
        strBuff.append(" FROM network_prefixes ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect); ResultSet rs = pstmt.executeQuery();) {
            
            
            NetworkPrefixVO myVO = null;
            while (rs.next()) {
                myVO = new NetworkPrefixVO();
                myVO.setPrefixId(rs.getInt("prefix_id"));
                myVO.setNetworkCode(rs.getString("network_code"));
                myVO.setSeries(rs.getString("series"));
                myVO.setOperator(rs.getString("operator"));
                myVO.setSeriesType(rs.getString("series_type"));

                networkList.add(myVO);
            }
            return networkList;
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException: " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkPrefixList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception: " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkPrefixList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: networkList size=" + networkList.size());
            }
        }
    }

    /**
     * Method for inserting Networks Prefixes.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            ArrayList
     * @return insertCount int
     * @throws BTSLBaseException
     */
    /*
     * public int insertNetworkPrefix(Connection p_con, ArrayList p_voList)
     * throws BTSLBaseException
     * {
     * 
     * PreparedStatement psmtInsert = null;
     * int insertCount = 0;
     * 
     * if (log.isDebugEnabled())
     * {
     * log.debug("insertNetworkPrefix", "Entered: p_voList size= " +
     * p_voList.size());
     * }
     * 
     * try
     * {
     * int count = 0;
     * 
     * if ((p_voList != null))
     * {
     * StringBuffer strBuff = new StringBuffer();
     * 
     * strBuff.append("INSERT INTO network_prefixes (prefix_id,network_code,");
     * strBuff.append("series,operator,series_type) values ");
     * strBuff.append("(?,?,?,?,?)");
     * 
     * String insertQuery = strBuff.toString();
     * 
     * if (log.isDebugEnabled())
     * {
     * log.debug("insertNetworkPrefix", "Query sqlInsert:" + insertQuery);
     * }
     * 
     * psmtInsert = p_con.prepareStatement(insertQuery);
     * for (int i = 0, j = p_voList.size(); i < j; i++)
     * {
     * NetworkPrefixVO myVO = (NetworkPrefixVO) p_voList.get(i);
     * 
     * psmtInsert.setLong(1, myVO.getPrefixID());
     * psmtInsert.setString(2, myVO.getNetworkCode());
     * psmtInsert.setString(3, myVO.getSeries());
     * psmtInsert.setString(4, myVO.getOperator());
     * psmtInsert.setString(5, myVO.getSeriesType());
     * 
     * insertCount = psmtInsert.executeUpdate();
     * 
     * psmtInsert.clearParameters();
     * // check the status of the update
     * if (insertCount > 0)
     * {
     * count++;
     * }
     * }
     * if (count == p_voList.size())
     * insertCount = 1;
     * else
     * insertCount = 0;
     * }
     * } // end of try
     * catch (SQLException sqle)
     * {
     * log.error("insertNetworkPrefix", "SQLException: " + sqle.getMessage());
     * sqle.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .RAISED,EventLevelI.FATAL,"NetworkDAO[insertNetworkPrefix]","","","",
     * "SQL Exception:"+sqle.getMessage());
     * throw new BTSLBaseException(this, "insertNetworkPrefix",
     * "error.general.sql.processing");
     * } // end of catch
     * catch (Exception e)
     * {
     * log.error("insertNetworkPrefix", "Exception: " + e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .RAISED,EventLevelI.FATAL,"NetworkDAO[insertNetworkPrefix]","","","",
     * "Exception:"+e.getMessage());
     * throw new BTSLBaseException(this, "insertNetworkPrefix",
     * "error.general.processing");
     * } // end of catch
     * finally
     * {
     * try{if (psmtInsert != null){psmtInsert.close();}} catch (Exception e){}
     * 
     * if (log.isDebugEnabled())
     * {
     * log.debug("insertNetworkPrefix", "Exiting: insertCount=" + insertCount);
     * }
     * } // end of finally
     * 
     * return insertCount;
     * }
     */

    /**
     * Method for deleting Networks Prefixes.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteNetworkPrefix(Connection p_con, String p_networkCode) throws BTSLBaseException {
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;

        final String methodName = "deleteNetworkPrefix";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_networkCode= " + p_networkCode);
        }

        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("DELETE FROM network_prefixes where ");
            strBuff.append("network_code = ?");
            String deleteQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            psmtDelete = p_con.prepareStatement(deleteQuery);
            psmtDelete.setString(1, p_networkCode);
            deleteCount = psmtDelete.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[deleteNetworkPrefix]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[deleteNetworkPrefix]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	OracleUtil.closeQuietly(psmtDelete);
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap<String,NetworkPrefixVO> loadNetworkPrefixCache() throws BTSLBaseException {
        final String methodName = "loadNetworkPrefixCache";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

       

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT ");
        strBuff.append(" np.network_code, np.prefix_id, np.series, np.operator, np.series_type, ");
        strBuff.append(" n.network_name , n.network_short_name, n.company_name, ");
        strBuff.append(" n.erp_network_code, n.status, n.language_1_message, ");
        strBuff.append(" n.language_2_message, n.modified_on  ");
        strBuff.append(" FROM network_prefixes np , networks n  ");
        strBuff.append(" WHERE n.status <> 'N' AND np.network_code = n.network_code AND  np.status <> 'N' ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        HashMap<String,NetworkPrefixVO>  map = new HashMap<String,NetworkPrefixVO> ();
        
        try(Connection con = OracleUtil.getSingleConnection();PreparedStatement pstmt = con.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();) {

        		 
        		
            

            NetworkPrefixVO networkPrefixVO = null;

            while (rs.next()) {
                networkPrefixVO = new NetworkPrefixVO();
                networkPrefixVO.setPrefixId(rs.getLong("prefix_id"));
                networkPrefixVO.setNetworkCode(rs.getString("network_code"));
                networkPrefixVO.setSeries(rs.getString("series"));
                networkPrefixVO.setSeriesType(rs.getString("series_type"));
                networkPrefixVO.setNetworkName(rs.getString("network_name"));
                networkPrefixVO.setNetworkCode(rs.getString("network_code"));
                networkPrefixVO.setNetworkShortName(rs.getString("network_short_name"));
                networkPrefixVO.setCompanyName(rs.getString("company_Name"));
                networkPrefixVO.setErpNetworkCode(rs.getString("erp_network_code"));
                networkPrefixVO.setStatus(rs.getString("status"));
                networkPrefixVO.setLanguage1Message(rs.getString("language_1_message"));
                networkPrefixVO.setLanguage2Message(rs.getString("language_2_message"));
                networkPrefixVO.setModifiedOn(rs.getDate("modified_on"));
                networkPrefixVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));
                networkPrefixVO.setOperator(rs.getString("operator"));

               
                map.put(networkPrefixVO.getSeries() + "_" + networkPrefixVO.getSeriesType(), networkPrefixVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException: " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkPrefixCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception: " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkPrefixCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: networkList size=" + map.size());
            }
        }
        return map;
    }

    /**
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap<String,MSISDNPrefixInterfaceMappingVO> loadMSISDNInterfaceMappingCache() throws BTSLBaseException {

        final String methodName = "loadMSISDNInterfaceMappingCache";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

         
         
        NetworkQry networkQry = (NetworkQry)ObjectProducer.getObject(QueryConstants.NETWORK_QRY, QueryConstants.QUERY_PRODUCER);
        String sqlSelect = networkQry.loadMSISDNInterfaceMappingCacheQry();       

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        HashMap<String,MSISDNPrefixInterfaceMappingVO> map = new HashMap<String,MSISDNPrefixInterfaceMappingVO>();
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
           try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
           {
            pstmt.setString(1, PretupsI.ALL);
            try (ResultSet rs = pstmt.executeQuery();)
            {
            MSISDNPrefixInterfaceMappingVO myVO = null;
            while (rs.next()) {
                myVO = new MSISDNPrefixInterfaceMappingVO();
                myVO.setNetworkCode(rs.getString("network_code"));
                myVO.setPrefixId(rs.getLong("prefix_id"));
                myVO.setInterfaceType(rs.getString("method_type"));
                myVO.setInterfaceID(rs.getString("interface_id"));
                myVO.setHandlerClass(rs.getString("handler_class"));
                myVO.setUnderProcessMsgRequired(rs.getString("underprocess_msg_reqd"));
                myVO.setAllServiceClassID(rs.getString("service_class_id"));
                myVO.setExternalID(rs.getString("external_id"));
                myVO.setInterfaceStatus(rs.getString("status"));
                myVO.setStatusType(rs.getString("statustype"));
                myVO.setLanguage1Message(rs.getString("message_language1"));
                myVO.setLanguage2Message(rs.getString("message_language2"));
                myVO.setInterfaceTypeID(rs.getString("interface_type_id"));
                myVO.setSingleStep(rs.getString("single_state_transaction"));
                if (PretupsI.YES.equals(myVO.getUnderProcessMsgRequired())) {
                    myVO.setUnderProcessMsgRequiredBool(true);
                } else {
                    myVO.setUnderProcessMsgRequiredBool(false);
                }
                String actionArr[] = rs.getString("action").split(",");
                String key = myVO.getPrefixId() + "_" + myVO.getInterfaceType();
                for (int i = 0; i < actionArr.length; i++) {
                    myVO.setAction(actionArr[i]);
                    map.put(key + "_" + actionArr[i], myVO);
                }
            }
            }
           }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException: " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadMSISDNInterfaceMappingCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception: " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadMSISDNInterfaceMappingCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	OracleUtil.closeQuietly(con);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: networkList size=" + map.size());
            }
        }
        return map;
    }

    /**
     * Load the Network Interface Module Cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap loadNetworkInterfaceModuleCache() throws BTSLBaseException {

        final String methodName = "loadNetworkInterfaceModuleCache";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

       

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT ");
        strBuff.append(" module, network_code, method_type, comm_type, ip, port, class_name ");
        strBuff.append(" FROM ");
        strBuff.append(" network_interface_modules ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        HashMap map = new HashMap();
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

           try( PreparedStatement pstmt = con.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();)
            
           {
            while (rs.next()) {
                NetworkInterfaceModuleVO interfaceModuleVO = new NetworkInterfaceModuleVO();

                interfaceModuleVO.setModule(rs.getString("module"));
                interfaceModuleVO.setNetworkCode(rs.getString("network_code"));
                interfaceModuleVO.setMethodType(rs.getString("method_type"));
                interfaceModuleVO.setCommunicationType(rs.getString("comm_type"));
                interfaceModuleVO.setIP(rs.getString("ip"));
                interfaceModuleVO.setPort(rs.getInt("port"));
                interfaceModuleVO.setClassName(rs.getString("class_name"));

                map.put(interfaceModuleVO.getModule() + "_" + interfaceModuleVO.getNetworkCode() + "_" + interfaceModuleVO.getMethodType(), interfaceModuleVO);
            }

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException: " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkInterfaceModuleCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception: " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkInterfaceModuleCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	OracleUtil.closeQuietly(con);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: Map size=" + map.size());
            }
        }
        return map;
    }

    // added for O2C

    public HashMap loadNWPrefixServiceTypeMappingCache() throws BTSLBaseException {
        final String methodName = "loadNWPrefixServiceTypeMappingCache";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT ");
        strBuff.append(" network_code, prefix_id, service_type, service_handler_class ");
        strBuff.append(" FROM ");
        strBuff.append(" prefix_service_mapping ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        HashMap map = new HashMap();
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();)
            {
            

            while (rs.next()) {
                NetworkPrefixServiceTypeVO prefixServiceVO = new NetworkPrefixServiceTypeVO();
                prefixServiceVO.setNetworkCode(rs.getString("network_code"));
                prefixServiceVO.setPrefixID(rs.getString("prefix_id"));
                prefixServiceVO.setServiceType(rs.getString("service_type"));
                prefixServiceVO.setHandlerClass(rs.getString("service_handler_class"));
                map.put(prefixServiceVO.getPrefixID() + "_" + prefixServiceVO.getNetworkCode(), prefixServiceVO);
            }

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException: " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNWPrefixServiceTypeMappingCache]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception: " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkInterfaceModuleCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	OracleUtil.closeQuietly(con);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: Map size=" + map.size());
            }
        }
        return map;

    }

    /*******
     * 
     * @param p_con
     * @param p_prefix_id
     * @return prefixSeries
     * @throws BTSLBaseException
     * @author arvinder.singh
     */

    public String getSeries(Connection p_con, String prefixID) throws BTSLBaseException {
        final String methodName = "getSeries";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: prefixID " + prefixID);
        }

         
        ResultSet rs = null;
        String prefixSeries = null;
        try {
            String[] temp = prefixID.split(",");
            String[] arr = new String[temp.length];
            StringBuilder str = new StringBuilder();
            String sqlQuery = "";

            for (int i = 0; i < arr.length; i++) {
                String values = temp[i];
                sqlQuery = "SELECT prefix_id,series FROM network_prefixes where prefix_id = ? AND status != 'N'";
                try(PreparedStatement pstmt = p_con.prepareStatement(sqlQuery);)
                {
                pstmt.setString(1, values);
                for (rs = pstmt.executeQuery(); rs.next();) {

                    if (i != 0) {
                        str.append(",");
                    }
                    prefixSeries = str.append(rs.getString("SERIES")).toString();

                }

            }
            }
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[getSeries]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[getSeries]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        } // end of finally

        return prefixSeries;
    }// end

    /**
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap<String,MSISDNPrefixInterfaceMappingVO> loadMSISDNInterfaceMappingCacheWithInterfaceID() throws BTSLBaseException {

        if (log.isDebugEnabled()) {
            log.debug("loadMSISDNInterfaceMappingCacheWithInterfaceID", "Entered");
        }
        final String METHOD_NAME = "loadMSISDNInterfaceMappingCacheWithInterfaceID";
         
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT i.external_id, i.status,i.message_language1, i.message_language2,I.status_type statustype, I.single_state_transaction, ");
        strBuff.append(" i.interface_id,im.handler_class,im.underprocess_msg_reqd,im.interface_type_id FROM interfaces i,interface_types im WHERE ");
        strBuff.append(" i.interface_type_id = im.interface_type_id AND i.status<>'N' ");
        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadMSISDNInterfaceMappingCacheWithInterfaceID", "QUERY sqlSelect=" + sqlSelect);
        }

        HashMap<String,MSISDNPrefixInterfaceMappingVO> map = new HashMap<String,MSISDNPrefixInterfaceMappingVO>();
       
        try (Connection con = OracleUtil.getSingleConnection();
        		PreparedStatement pstmt = con.prepareStatement(sqlSelect);
        		ResultSet rs = pstmt.executeQuery();){
            
            MSISDNPrefixInterfaceMappingVO myVO = null;
            while (rs.next()) {
                myVO = new MSISDNPrefixInterfaceMappingVO();

                myVO.setInterfaceID(rs.getString("interface_id"));
                myVO.setHandlerClass(rs.getString("handler_class"));
                myVO.setUnderProcessMsgRequired(rs.getString("underprocess_msg_reqd"));
                myVO.setExternalID(rs.getString("external_id"));
                myVO.setInterfaceStatus(rs.getString("status"));
                myVO.setStatusType(rs.getString("statustype"));
                myVO.setLanguage1Message(rs.getString("message_language1"));
                myVO.setLanguage2Message(rs.getString("message_language2"));
                myVO.setInterfaceTypeID(rs.getString("interface_type_id"));
                myVO.setSingleStep(rs.getString("single_state_transaction"));
                if (PretupsI.YES.equals(myVO.getUnderProcessMsgRequired())) {
                    myVO.setUnderProcessMsgRequiredBool(true);
                } else {
                    myVO.setUnderProcessMsgRequiredBool(false);
                }

                String key = myVO.getInterfaceID();
                map.put(key, myVO);
            }

        } catch (SQLException sqe) {
            log.error("loadMSISDNInterfaceMappingCacheWithInterfaceID", "SQLException: " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadMSISDNInterfaceMappingCacheWithInterfaceID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadMSISDNInterfaceMappingCacheWithInterfaceID", "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error("loadMSISDNInterfaceMappingCacheWithInterfaceID", "Exception: " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadMSISDNInterfaceMappingCacheWithInterfaceID]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadMSISDNInterfaceMappingCacheWithInterfaceID", "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("loadMSISDNInterfaceMappingCacheWithInterfaceID", "Exiting: networkList size=" + map.size());
            }
        }
        return map;
    }
    
    /**
     * Method for loading Network List for super network admin and super customer care
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_status
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList<NetworkVO> loadNetworkListForSuperOperatorUsers(Connection p_con, String p_status, String p_userid) throws BTSLBaseException {

        final String methodName = "loadNetworkListForSuperOperatorUsers";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_status" + p_status);
        }

         

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT distinct(n.network_code),n.network_name,");
        strBuff.append(" n.network_short_name,n.company_name,n.report_header_name, ");
        strBuff.append(" n.erp_network_code,n.address1,address2,city,state,zip_code, ");
        strBuff.append("n.country,n.network_type,n.status,n.remarks,n.language_1_message, ");
        strBuff.append(" n.language_2_message,n.text_1_value,n.text_2_value,n.country_prefix_code,n.service_set_id, ");
        strBuff.append("n.created_by, n.modified_by, n.created_on,");
        strBuff.append(" n.modified_on FROM networks n ,user_geographies ug WHERE status not in(" + p_status + ") ");
        strBuff.append("and ug.grph_domain_code =n.network_code and ug.user_id=?");
        strBuff.append(" ORDER BY n.network_code");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList<NetworkVO> list = new ArrayList<>();

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_userid);
            try(ResultSet rs = pstmt.executeQuery();){

            NetworkVO networkVO = null;

            while (rs.next()) {

                networkVO = new NetworkVO();
                networkVO.setNetworkName(rs.getString("network_name"));
                networkVO.setNetworkCode(rs.getString("network_code"));
                networkVO.setNetworkShortName(rs.getString("network_short_name"));
                networkVO.setCompanyName(rs.getString("company_Name"));
                networkVO.setReportHeaderName(rs.getString("report_header_name"));
                networkVO.setErpNetworkCode(rs.getString("erp_network_code"));
                networkVO.setAddress1(rs.getString("address1"));
                networkVO.setAddress2(rs.getString("address2"));
                networkVO.setCity(rs.getString("city"));
                networkVO.setState(rs.getString("state"));
                networkVO.setZipCode(rs.getString("zip_code"));
                networkVO.setCountry(rs.getString("country"));
                networkVO.setNetworkType(rs.getString("network_type"));
                networkVO.setStatus(rs.getString("status"));
                networkVO.setRemarks(rs.getString("remarks"));
                networkVO.setLanguage1Message(rs.getString("language_1_message"));
                networkVO.setLanguage2Message(rs.getString("language_2_message"));
                networkVO.setText1Value(rs.getString("text_1_value"));
                networkVO.setText2Value(rs.getString("text_2_value"));
                networkVO.setCountryPrefixCode(rs.getString("country_prefix_code"));
                networkVO.setServiceSetID(rs.getString("service_set_id"));
                networkVO.setCreatedBy(rs.getString("created_by"));
                networkVO.setModifiedBy(rs.getString("modified_by"));
                networkVO.setCreatedOn(rs.getDate("created_on"));
                networkVO.setModifiedOn(rs.getDate("modified_on"));
                networkVO.setLastModified(rs.getTimestamp("modified_on").getTime());

                list.add(networkVO);
            }

        }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkListForSuperOperatorUsers]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkListForSuperOperatorUsers]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: networkMap size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Network List for super channel admin
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_status
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList<NetworkVO> loadNetworkListForSuperChannelAdm(Connection p_con, String p_status, String p_userid) throws BTSLBaseException {

        final String methodName = "loadNetworkListForSuperChannelAdm";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_status" + p_status);
        }

         
         

        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT distinct(n.network_code),n.network_name,");
        strBuff.append(" n.network_short_name,n.company_name,n.report_header_name, ");
        strBuff.append(" n.erp_network_code,n.address1,address2,city,state,zip_code, ");
        strBuff.append("n.country,n.network_type,n.status,n.remarks,n.language_1_message, ");
        strBuff.append(" n.language_2_message,n.text_1_value,n.text_2_value,n.country_prefix_code,n.service_set_id, ");
        strBuff.append("n.created_by, n.modified_by, n.created_on,");
        strBuff.append(" n.modified_on FROM networks n ,GEOGRAPHICAL_DOMAINS gd, user_geographies ug WHERE n.status not in(" + p_status + ") ");
        strBuff.append(" and gd.status <>'N' and ug.grph_domain_code=gd.GRPH_DOMAIN_CODE and gd.network_code=n.network_code and ug.user_id=?");
        strBuff.append(" ORDER BY n.network_code");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        ArrayList<NetworkVO> list = new ArrayList<>();

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_userid);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            NetworkVO networkVO = null;

            while (rs.next()) {

                networkVO = new NetworkVO();
                networkVO.setNetworkName(rs.getString("network_name"));
                networkVO.setNetworkCode(rs.getString("network_code"));
                networkVO.setNetworkShortName(rs.getString("network_short_name"));
                networkVO.setCompanyName(rs.getString("company_Name"));
                networkVO.setReportHeaderName(rs.getString("report_header_name"));
                networkVO.setErpNetworkCode(rs.getString("erp_network_code"));
                networkVO.setAddress1(rs.getString("address1"));
                networkVO.setAddress2(rs.getString("address2"));
                networkVO.setCity(rs.getString("city"));
                networkVO.setState(rs.getString("state"));
                networkVO.setZipCode(rs.getString("zip_code"));
                networkVO.setCountry(rs.getString("country"));
                networkVO.setNetworkType(rs.getString("network_type"));
                networkVO.setStatus(rs.getString("status"));
                networkVO.setRemarks(rs.getString("remarks"));
                networkVO.setLanguage1Message(rs.getString("language_1_message"));
                networkVO.setLanguage2Message(rs.getString("language_2_message"));
                networkVO.setText1Value(rs.getString("text_1_value"));
                networkVO.setText2Value(rs.getString("text_2_value"));
                networkVO.setCountryPrefixCode(rs.getString("country_prefix_code"));
                networkVO.setServiceSetID(rs.getString("service_set_id"));
                networkVO.setCreatedBy(rs.getString("created_by"));
                networkVO.setModifiedBy(rs.getString("modified_by"));
                networkVO.setCreatedOn(rs.getDate("created_on"));
                networkVO.setModifiedOn(rs.getDate("modified_on"));
                networkVO.setLastModified(rs.getTimestamp("modified_on").getTime());

                list.add(networkVO);
            }

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkListForSuperChannelAdm]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDAO[loadNetworkListForSuperChannelAdm]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: networkMap size=" + list.size());
            }
        }
        return list;
    }
 
    /**
     * @param imsi
     * @return
     * @throws BTSLBaseException
     * @author ashish.gupta
     * For VIL
     */
    public String getSeriesBasedOnIMSI(String imsi) throws BTSLBaseException {
        final String methodName = "getSeriesBasedOnIMSI";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: IMSI " + imsi);
        }

        String subStr5=null;
        String subStr6=null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String prefixSeries = null;
        String brand = null;
        String series = null;
        Connection con = null;
        try {
        	if(BTSLUtil.isNullString(imsi)){
	        	throw new BaseException(PretupsErrorCodesI.C2S_ERROR_MISSING_IMSI_INFORMATION);
	        }
	        else{
	        	try {
	        		subStr5=imsi.substring(0, 5);
	        		subStr6=imsi.substring(0, 6);
				} catch (Exception e) {
					throw new BaseException(PretupsErrorCodesI.C2S_ERROR_MISSING_IMSI_INFORMATION);
				}
	        }
	        if(con == null || con.isClosed()){
	        	con = OracleUtil.getConnection();
	        }
	        
	        StringBuilder str = new StringBuilder();
	        String sqlQuery = "";
	        sqlQuery = "SELECT series, brand, network_code FROM IMSI_NW_BRAND_PREFIX_MAPPING where (IMSI = ? or IMSI =?) AND status <> ?";
	        pstmt = con.prepareStatement(sqlQuery);
	        pstmt.setString(1, subStr5);
	        pstmt.setString(2, subStr6);
	        pstmt.setString(3, PretupsI.NO);
	        rs=pstmt.executeQuery();
	        if (rs.next()) {
	            series = rs.getString("SERIES").toString().trim();
	            brand = rs.getString("BRAND").toString().trim();
	            prefixSeries=series+"_"+brand;
	        }
        }
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[getSeries]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[getSeries]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	try {if (rs != null) {rs.close();}} catch (Exception e) {log.errorTrace(methodName, e);}
        	try {if (pstmt != null) {pstmt.close();}} catch (Exception e) {log.errorTrace(methodName, e);}
        	OracleUtil.closeQuietly(con);
        	if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited : Prefix defined = "+prefixSeries+" , against IMSI " + imsi);
            }
        } // end of finally
       	return prefixSeries;
    }// end

}