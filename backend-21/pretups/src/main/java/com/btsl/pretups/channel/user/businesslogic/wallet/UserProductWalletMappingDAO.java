package com.btsl.pretups.channel.user.businesslogic.wallet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

/**
 * @author birendra.mishra
 * 
 */
public class UserProductWalletMappingDAO {

    private static final Log LOG = LogFactory.getLog(UserProductWalletMappingDAO.class.getName());

    /**
     * This method loads all the records from table USER_WALLET_PRODUCT_MAPPING
     * and maintains a list of that.
     * 
     * @author birendra.mishra
     * @return List<UserProductWalletMappingVO>
     * @throws Exception
     */
    public List<UserProductWalletMappingVO> loadUserProductWalletMappingList(boolean sortByProductCode) throws BTSLBaseException {
        final String methodName = "loadUserProductWalletMappingList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

        ResultSet rs = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        final StringBuilder selectQuery = new StringBuilder();
        UserProductWalletMappingVO userProductWalletMappingVO = null;
        final List<UserProductWalletMappingVO> userProdcutWalletMappingList = new ArrayList<UserProductWalletMappingVO>();

        try {
            selectQuery.append("SELECT UPWM.NETWORK_CODE, UPWM.PRODUCT_CODE, UPWM.ACCOUNT_CODE, UPWM.ACCOUNT_NAME, UPWM.ACCOUNT_PRIORITY, ");
            selectQuery.append("UPWM.ADDNL_COMM_ALWD, UPWM.LMS_POINT, UPWM.PARTL_DED_ALWD,NET.NETWORK_NAME, PRD.PRODUCT_NAME, ");
            selectQuery.append("UPWM.CREATED_BY, UPWM.CREATED_ON, UPWM.MODIFIED_BY, UPWM.MODIFIED_ON, PRD.PRODUCT_TYPE, UPWM.PENALTY_ACCOUNT_PRIORITY ");
            selectQuery.append("FROM USER_WALLET_PRODUCT_MAPPING UPWM, NETWORKS NET, PRODUCTS PRD ");
            selectQuery.append("WHERE UPWM.NETWORK_CODE = NET.NETWORK_CODE ");
            selectQuery.append("AND UPWM.PRODUCT_CODE = PRD.PRODUCT_CODE ");
            if (sortByProductCode) {
                selectQuery.append("ORDER BY UPWM.PRODUCT_CODE ");
            }

            final String sqlSelect = selectQuery.toString();
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                /** These columns are from table USER_WALLET_PRODUCT_MAPPING */
                userProductWalletMappingVO = new UserProductWalletMappingVO();
                userProductWalletMappingVO.setNetworkCode(rs.getString("NETWORK_CODE"));
                userProductWalletMappingVO.setProductCode(rs.getString("PRODUCT_CODE"));
                userProductWalletMappingVO.setAccountCode(rs.getString("ACCOUNT_CODE"));
                userProductWalletMappingVO.setAccountName(rs.getString("ACCOUNT_NAME"));
                userProductWalletMappingVO.setAccountPriority(rs.getInt("ACCOUNT_PRIORITY"));
                userProductWalletMappingVO.setAddnlComAlwd(rs.getString("ADDNL_COMM_ALWD"));
                userProductWalletMappingVO.setLmsPoint(rs.getString("LMS_POINT"));
                userProductWalletMappingVO.setPartialDedAlwd(rs.getString("PARTL_DED_ALWD"));
                userProductWalletMappingVO.setCreatedBy(rs.getString("CREATED_BY"));
                userProductWalletMappingVO.setCreatedOn(rs.getDate("CREATED_ON"));
                userProductWalletMappingVO.setModifiedBy(rs.getString("MODIFIED_BY"));
                userProductWalletMappingVO.setModifiedOn(rs.getDate("MODIFIED_ON"));
                /** These columns are from tables PRODUCTS and NETWORKS */
                userProductWalletMappingVO.setNetworkName(rs.getString("NETWORK_NAME"));
                userProductWalletMappingVO.setProductName(rs.getString("PRODUCT_NAME"));
                userProductWalletMappingVO.setProductNameToShow(rs.getString("PRODUCT_NAME"));
                userProductWalletMappingVO.setProductType(rs.getString("PRODUCT_TYPE"));
                userProductWalletMappingVO.setPenaltyAccountPriority(rs.getInt("PENALTY_ACCOUNT_PRIORITY"));
                userProdcutWalletMappingList.add(userProductWalletMappingVO);
            }

        } catch (SQLException sqe) {
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserProductWalletMappingDAO[ " + methodName + " ]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserProductWalletMappingDAO[ " + methodName + " ]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:Map size = " + userProdcutWalletMappingList.size());
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect = " + selectQuery);
        }

        return userProdcutWalletMappingList;
    }

    /**
     * This method updates all the records in table
     * USER_WALLET_PRODUCT_MAPPING..
     * 
     * @param userVO
     * @param con
     * @param userProdcutWalletMappingList
     * @return updateCount
     * @throws Exception
     */
    public int updateUserProductWalletMappingList(Connection p_con, List<UserProductWalletMappingVO> p_userProdcutWalletMappingList, UserVO userVO) throws BTSLBaseException {
        final String methodName = "updateUserProductWalletMappingList()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:" + " p_con:" + ", p_userProdcutWalletMappingList Size:" + p_userProdcutWalletMappingList.size());
        }

        final ResultSet rs = null;
        PreparedStatement pstmt = null;
        StringBuilder updateQuery = null;
        int updateCount = 1;

        try {
            final Date date = new Date();
            updateQuery = new StringBuilder();
            updateQuery.append("UPDATE USER_WALLET_PRODUCT_MAPPING SET ACCOUNT_NAME = ?, ACCOUNT_PRIORITY = ?,");
            updateQuery.append(" PARTL_DED_ALWD = ?, ADDNL_COMM_ALWD = ?, LMS_POINT = ?, MODIFIED_BY = ?, MODIFIED_ON = ?");
            updateQuery.append(" WHERE NETWORK_CODE = ? AND PRODUCT_CODE = ? AND ACCOUNT_CODE = ? ");

            final String sqlUpdate = updateQuery.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Update Query  in UserProductWalletMappingDAO  ::" + sqlUpdate);
            }
            pstmt = p_con.prepareStatement(sqlUpdate);
            for (final UserProductWalletMappingVO userProductWalletMappingVO : p_userProdcutWalletMappingList) {
                pstmt.clearParameters();
                pstmt.setString(1, userProductWalletMappingVO.getAccountName());
                pstmt.setInt(2, userProductWalletMappingVO.getAccountPriority());
                pstmt.setString(3, userProductWalletMappingVO.getPartialDedAlwd());
                pstmt.setString(4, userProductWalletMappingVO.getAddnlComAlwd());
                pstmt.setString(5, userProductWalletMappingVO.getLmsPoint());
                pstmt.setString(6, userVO.getModifiedBy());
                pstmt.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(date));
                pstmt.setString(8, userProductWalletMappingVO.getNetworkCode());
                pstmt.setString(9, userProductWalletMappingVO.getProductCode());
                pstmt.setString(10, userProductWalletMappingVO.getAccountCode());

                final int rowUpdateCount = pstmt.executeUpdate();
                if (rowUpdateCount != 1) {
                    updateCount = 0;
                    break;
                }

            }
        } catch (SQLException sqe) {
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserProductWalletMappingDAO[ " + methodName + " ]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserProductWalletMappingDAO[ " + methodName + " ]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: updateCount:" + updateCount);
            }
        }

        return updateCount;
    }

}
