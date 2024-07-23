/*
 * @# RoutingControlDAO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Oct 30, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.selftopup.pretups.routing.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.selftopup.common.ListValueVO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

/**
 * 
 */
public class RoutingControlDAO {
    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadRoutingControlDetails.
     * 
     * @return HashMap
     * @throws SQLException
     * @throws Exception
     */
    public HashMap loadRoutingControlDetails() throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadRoutingControlDetails", "Entered ");
        PreparedStatement pstmtSelect = null;
        HashMap routingControlMap = new HashMap();
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT network_code, service_type, interface_category, database_check, series_check ");
            selectQueryBuff.append(" FROM routing_control ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadRoutingControlDetails", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                subscriberRoutingControlVO = new SubscriberRoutingControlVO();
                subscriberRoutingControlVO.setNetworkCode(rs.getString("network_code"));
                subscriberRoutingControlVO.setServiceType(rs.getString("service_type"));
                subscriberRoutingControlVO.setInterfaceCategory(rs.getString("interface_category"));
                subscriberRoutingControlVO.setDatabaseCheck(rs.getString("database_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getDatabaseCheck()))
                    subscriberRoutingControlVO.setDatabaseCheckBool(true);
                else
                    subscriberRoutingControlVO.setDatabaseCheckBool(false);

                subscriberRoutingControlVO.setSeriesCheck(rs.getString("series_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getSeriesCheck()))
                    subscriberRoutingControlVO.setSeriesCheckBool(true);
                else
                    subscriberRoutingControlVO.setSeriesCheckBool(false);
                routingControlMap.put(rs.getString("network_code") + "_" + rs.getString("service_type") + "_" + rs.getString("interface_category"), subscriberRoutingControlVO);
            }// end while
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadRoutingControlDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw sqle;
        }// end of catch
        catch (Exception e) {
            _log.error("loadRoutingControlDetails", "Exception " + e.getMessage());
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
                _log.debug("loadRoutingControlDetails", "Exiting routingControlMap.size:" + routingControlMap.size());
        }// end of finally\
        return routingControlMap;
    }

    /**
     * Method loadInterfaceRoutingControlDetails.
     * 
     * @return HashMap
     * @throws SQLException
     * @throws Exception
     */
    public HashMap loadInterfaceRoutingControlDetails() throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadInterfaceRoutingControlDetails", "Entered ");
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelectAlt = null;
        HashMap routingControlMap = new HashMap();

        Connection con = null;
        ResultSet rs = null;
        ResultSet rsAlt = null;
        ListValueVO listValueVOAlt1 = null;
        ListValueVO listValueVOAlt2 = null;
        String alt1 = null;
        String alt2 = null;
        ArrayList routingControlVOList = null;
        StringBuffer strBuff = new StringBuffer("SELECT interface_id ,alternate_interface_id1,alternate_interface_id2 ");
        strBuff.append(" FROM interface_routing_control");

        StringBuffer strBuffAlt = new StringBuffer("SELECT  I.interface_id,I.status,I.message_language1, I.message_language2, I.external_id,IT.handler_class,IT.underprocess_msg_reqd,SC.service_class_id,I.status_type statustype ");
        strBuffAlt.append(" FROM interfaces I,interface_types IT ,service_classes SC ");
        strBuffAlt.append(" WHERE I.interface_id=? AND I.interface_type_id=IT.interface_type_id AND I.status='Y'  "); // Changed
                                                                                                                      // the
                                                                                                                      // condition
                                                                                                                      // of
                                                                                                                      // I.status<>'N'
                                                                                                                      // to
                                                                                                                      // Y
                                                                                                                      // so
                                                                                                                      // that
                                                                                                                      // only
                                                                                                                      // active
                                                                                                                      // ones
                                                                                                                      // comes
        strBuffAlt.append(" AND I.interface_id=SC.interface_id(+) AND SC.service_class_code(+)=? AND SC.STATUS(+)<>'N' ");

        try {
            con = OracleUtil.getSingleConnection();
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceRoutingControlDetails", "Select Query= " + strBuff.toString());
            pstmtSelect = con.prepareStatement(strBuff.toString());
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                if (_log.isDebugEnabled())
                    _log.debug("loadInterfaceRoutingControlDetails", "Select Query for alternate= " + strBuffAlt.toString());
                routingControlVOList = new ArrayList();
                alt1 = rs.getString("alternate_interface_id1");
                alt2 = rs.getString("alternate_interface_id2");
                pstmtSelectAlt = con.prepareStatement(strBuffAlt.toString());
                pstmtSelectAlt.setString(1, alt1);
                pstmtSelectAlt.setString(2, PretupsI.ALL);
                rsAlt = pstmtSelectAlt.executeQuery();
                if (rsAlt.next()) {
                    listValueVOAlt1 = new ListValueVO(rsAlt.getString("handler_class"), rsAlt.getString("interface_id"));
                    listValueVOAlt1.setType(rsAlt.getString("underprocess_msg_reqd"));
                    listValueVOAlt1.setTypeName(rsAlt.getString("service_class_id"));
                    listValueVOAlt1.setIDValue(rsAlt.getString("external_id"));
                    listValueVOAlt1.setStatus(rsAlt.getString("status"));
                    listValueVOAlt1.setStatusType(rsAlt.getString("statustype"));
                    listValueVOAlt1.setOtherInfo(rsAlt.getString("message_language1"));
                    listValueVOAlt1.setOtherInfo2(rsAlt.getString("message_language2"));
                    routingControlVOList.add(listValueVOAlt1);
                }
                pstmtSelectAlt.clearParameters();
                pstmtSelectAlt.setString(1, alt2);
                pstmtSelectAlt.setString(2, PretupsI.ALL);
                rsAlt = pstmtSelectAlt.executeQuery();
                if (rsAlt.next()) {
                    listValueVOAlt2 = new ListValueVO(rsAlt.getString("handler_class"), rsAlt.getString("interface_id"));
                    listValueVOAlt2.setType(rsAlt.getString("underprocess_msg_reqd"));
                    listValueVOAlt2.setTypeName(rsAlt.getString("service_class_id"));
                    listValueVOAlt2.setIDValue(rsAlt.getString("external_id"));
                    listValueVOAlt2.setStatus(rsAlt.getString("status"));
                    listValueVOAlt2.setStatusType(rsAlt.getString("statustype"));
                    listValueVOAlt2.setOtherInfo(rsAlt.getString("message_language1"));
                    listValueVOAlt2.setOtherInfo2(rsAlt.getString("message_language2"));
                    routingControlVOList.add(listValueVOAlt2);
                }

                routingControlMap.put(rs.getString("interface_id"), routingControlVOList);
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadInterfaceRoutingControlDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw sqle;
        }// end of catch
        catch (Exception e) {
            _log.error("loadInterfaceRoutingControlDetails", "Exception " + e.getMessage());
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
                if (rsAlt != null)
                    rsAlt.close();
            } catch (Exception e) {
            }

            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelectAlt != null)
                    pstmtSelectAlt.close();
            } catch (Exception e) {
            }

            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceRoutingControlDetails", "Exiting routingControlMap.size:" + routingControlMap.size());
        }// end of finally\
        return routingControlMap;
    }

    /**
     * Method loadServiceInterfaceRoutingDetails.
     * 
     * @return HashMap
     * @throws SQLException
     * @throws Exception
     */
    public HashMap loadServiceInterfaceRoutingDetails() throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadServiceInterfaceRoutingDetails", "Entered ");
        PreparedStatement pstmtSelect = null;
        HashMap serviceRoutingMap = new HashMap();
        ServiceInterfaceRoutingVO serviceInterfaceRoutingVO = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT network_code, service_type, interface_type, alternate_interface_type,alternate_interface_check, ");
            selectQueryBuff.append(" sno,sender_subscriber_type, interface_def_selector, alternate_def_selector, ");
            selectQueryBuff.append(" created_on,created_by,modified_by,modified_on FROM service_interface_routing ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadServiceInterfaceRoutingDetails", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                serviceInterfaceRoutingVO = new ServiceInterfaceRoutingVO();
                serviceInterfaceRoutingVO.setSno(rs.getInt("sno"));
                serviceInterfaceRoutingVO.setNetworkCode(rs.getString("network_code"));
                serviceInterfaceRoutingVO.setServiceType(rs.getString("service_type"));
                serviceInterfaceRoutingVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                serviceInterfaceRoutingVO.setInterfaceType(rs.getString("interface_type"));
                serviceInterfaceRoutingVO.setInterfaceDefaultSelectortCode(rs.getString("interface_def_selector"));
                serviceInterfaceRoutingVO.setAlternateInterfaceType(rs.getString("alternate_interface_type"));
                serviceInterfaceRoutingVO.setAlternateDefaultSelectortCode(rs.getString("alternate_def_selector"));
                serviceInterfaceRoutingVO.setCreatedBy(rs.getString("created_by"));
                serviceInterfaceRoutingVO.setCreatedOn(rs.getDate("created_on"));
                serviceInterfaceRoutingVO.setModifiedBy(rs.getString("modified_by"));
                serviceInterfaceRoutingVO.setModifiedOn(rs.getDate("modified_on"));
                serviceInterfaceRoutingVO.setAlternateInterfaceCheck(rs.getString("alternate_interface_check"));
                if (PretupsI.YES.equals(rs.getString("alternate_interface_check"))) {
                    if (serviceInterfaceRoutingVO.getAlternateInterfaceCheck() != null && !BTSLUtil.isNullString(serviceInterfaceRoutingVO.getAlternateInterfaceType()))
                        serviceInterfaceRoutingVO.setAlternateInterfaceCheckBool(true);
                    else
                        serviceInterfaceRoutingVO.setAlternateInterfaceCheckBool(false);
                } else
                    serviceInterfaceRoutingVO.setAlternateInterfaceCheckBool(false);

                serviceRoutingMap.put(rs.getString("network_code") + "_" + rs.getString("service_type") + "_" + serviceInterfaceRoutingVO.getSenderSubscriberType(), serviceInterfaceRoutingVO);
            }// end while
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadServiceInterfaceRoutingDetails", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw sqle;
        }// end of catch
        catch (Exception e) {
            _log.error("loadServiceInterfaceRoutingDetails", "Exception " + e.getMessage());
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
                _log.debug("loadServiceInterfaceRoutingDetails", "Exiting serviceRoutingVO.size:" + serviceRoutingMap.size());
        }// end of finally\
        return serviceRoutingMap;
    }

    /**
     * Method loadRoutingControlDetailsList.
     * 
     * @return ArrayList
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadRoutingControlDetailsList() throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadRoutingControlDetailsList", "Entered ");
        PreparedStatement pstmtSelect = null;
        ArrayList routingControlList = new ArrayList();
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = OracleUtil.getSingleConnection();
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT network_code, service_type, interface_category, database_check, series_check ");
            selectQueryBuff.append(" FROM routing_control ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadRoutingControlDetailsList", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                subscriberRoutingControlVO = new SubscriberRoutingControlVO();
                subscriberRoutingControlVO.setNetworkCode(rs.getString("network_code"));
                subscriberRoutingControlVO.setServiceType(rs.getString("service_type"));
                subscriberRoutingControlVO.setInterfaceCategory(rs.getString("interface_category"));
                subscriberRoutingControlVO.setDatabaseCheck(rs.getString("database_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getDatabaseCheck()))
                    subscriberRoutingControlVO.setDatabaseCheckBool(true);
                else
                    subscriberRoutingControlVO.setDatabaseCheckBool(false);

                subscriberRoutingControlVO.setSeriesCheck(rs.getString("series_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getSeriesCheck()))
                    subscriberRoutingControlVO.setSeriesCheckBool(true);
                else
                    subscriberRoutingControlVO.setSeriesCheckBool(false);

                routingControlList.add(subscriberRoutingControlVO);

            }// end while
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadRoutingControlDetailsList", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw sqle;
        }// end of catch
        catch (Exception e) {
            _log.error("loadRoutingControlDetailsList", "Exception " + e.getMessage());
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
                _log.debug("loadRoutingControlDetailsList", "Exiting routingControlMap.size:" + routingControlList.size());
        }// end of finally\
        return routingControlList;
    }

    /**
     * Method loadRoutingControlDetailsList.
     * 
     * @return ArrayList
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadRoutingControlDetailsList(Connection con) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadRoutingControlDetailsList", "Entered ");
        PreparedStatement pstmtSelect = null;
        ArrayList routingControlList = new ArrayList();
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT network_code, service_type, interface_category, database_check, series_check ");
            selectQueryBuff.append(" FROM routing_control ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadRoutingControlDetailsList", "select query:" + selectQuery);
            pstmtSelect = con.prepareStatement(selectQuery);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                subscriberRoutingControlVO = new SubscriberRoutingControlVO();
                subscriberRoutingControlVO.setNetworkCode(rs.getString("network_code"));
                subscriberRoutingControlVO.setServiceType(rs.getString("service_type"));
                subscriberRoutingControlVO.setInterfaceCategory(rs.getString("interface_category"));
                subscriberRoutingControlVO.setDatabaseCheck(rs.getString("database_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getDatabaseCheck()))
                    subscriberRoutingControlVO.setDatabaseCheckBool(true);
                else
                    subscriberRoutingControlVO.setDatabaseCheckBool(false);

                subscriberRoutingControlVO.setSeriesCheck(rs.getString("series_check"));
                if (PretupsI.YES.equals(subscriberRoutingControlVO.getSeriesCheck()))
                    subscriberRoutingControlVO.setSeriesCheckBool(true);
                else
                    subscriberRoutingControlVO.setSeriesCheckBool(false);

                routingControlList.add(subscriberRoutingControlVO);

            }// end while
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadRoutingControlDetailsList", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw sqle;
        }// end of catch
        catch (Exception e) {
            _log.error("loadRoutingControlDetailsList", "Exception " + e.getMessage());
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
            if (_log.isDebugEnabled())
                _log.debug("loadRoutingControlDetailsList", "Exiting routingControlMap.size:" + routingControlList.size());
        }// end of finally\
        return routingControlList;
    }

}
