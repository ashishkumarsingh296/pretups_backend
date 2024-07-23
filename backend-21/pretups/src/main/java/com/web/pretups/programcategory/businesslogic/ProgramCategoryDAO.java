package com.web.pretups.programcategory.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.stereotype.Repository;

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

@Repository
public class ProgramCategoryDAO {

    private static Log _log = LogFactory.getLog(ProgramCategoryDAO.class.getName());

    public ArrayList<ListValueVO> loadProductList(Connection p_con, String p_networkCode, String p_moduleCode) throws BTSLBaseException {
        final String methodName = "loadProductList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: networkCode = ");
        	msg.append(p_networkCode);
        	msg.append(", p_moduleCode = ");
        	msg.append(p_moduleCode);

        	String message=msg.toString();
            _log.debug(methodName, message);
        }

        final ArrayList<ListValueVO> list = new ArrayList<ListValueVO>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT l.lookup_code,l.lookup_name,l.lookup_type,l.status ");
        strBuff.append(" FROM LOOKUPS l, LOOKUP_TYPES lt ");
        strBuff.append(" WHERE l.status = 'Y' AND l.lookup_type = lt.lookup_type  AND ");
        strBuff.append(" lt.lookup_type =?");
        strBuff.append(" ORDER BY l.lookup_type");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.PROG_MGMT_TYPE);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("product_name"), rs.getString("product_code")));
            }
            return list;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProgramCategoryDAO[loadProductList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadProductList]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: Product List Size =" + list.size());
            }
        }
    }

    public ArrayList<String> loadWalletList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadWalletList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        final ArrayList<String> list = new ArrayList<String>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" select distinct wallet_type from network_stocks ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("wallet_type"));
            }
            return list;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkProductDAO[loadWalletList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProgramCategoryDAO[loadWalletList]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: Product List Size =" + list.size());
            }
        }
    }

    public int saveProgramRules(Connection p_con, ProgramCategoryVO programCategoryVO) throws BTSLBaseException {
        final String methodName = "saveProgramRules";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuffer strBuff = null;
        PreparedStatement pstmt = null;
        int insertCount = 0;
        try {
            strBuff = new StringBuffer();
            strBuff.append("insert into PROGRAM_CATEGORY (  ");
            strBuff.append("  REWARD_TYPES, MAX_PERIOD,MIN_PERIOD,POINTS_EARN_ALLOWED,PROFILE_TYPE,");
            strBuff.append(" REDEEMPTION_ALLOWED,REDEEMTION_FREQ,WALLET_ALLOWED,EARNING_TYPE ) ");
            strBuff.append(" values (?,?,?,?,?,?,?,?,?) ");
            final String insertQuery = strBuff.toString();
            pstmt = p_con.prepareStatement(insertQuery);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + insertQuery);
            }
            pstmt.setString(1, programCategoryVO.getRewardType());
            pstmt.setString(2, programCategoryVO.getMaxPeriod());
            pstmt.setString(3, programCategoryVO.getMinPeriod());
            pstmt.setString(4, Boolean.toString(programCategoryVO.isPointsForParent()));
            pstmt.setString(5, programCategoryVO.getProgramType());
            pstmt.setString(6, Boolean.toString(programCategoryVO.isAutoRedempAll()));
            pstmt.setString(7, programCategoryVO.getRedempFrequency());
            pstmt.setString(8, programCategoryVO.getRedempWalletType());
            pstmt.setString(9, programCategoryVO.getProgramEarningType());

            insertCount = pstmt.executeUpdate();
            if (insertCount <= 0) {
                throw new SQLException(methodName);
            } else {
                p_con.commit();
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProgramCategoryDAO[saveProgramRules]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProgramCategoryDAO[saveProgramRules]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: Insert Count =" + insertCount);
            }
        }
        return insertCount;
    }

    public int updateProgramRules(Connection p_con, ProgramCategoryVO programCategoryVO) throws BTSLBaseException {
        final String methodName = "updateProgramRules";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuffer strBuff = null;
        PreparedStatement pstmt = null;
        int updateCount = 0;
        try {
            strBuff = new StringBuffer();
            strBuff.append("update PROGRAM_CATEGORY set  ");
            strBuff.append("  REWARD_TYPES=?, MAX_PERIOD=?,MIN_PERIOD=?,POINTS_EARN_ALLOWED=?,PROFILE_TYPE=?,");
            strBuff.append(" REDEEMPTION_ALLOWED=?,REDEEMTION_FREQ=?,WALLET_ALLOWED=?,EARNING_TYPE=?  ");
            strBuff.append(" where PROFILE_TYPE=? ");
            final String insertQuery = strBuff.toString();
            pstmt = p_con.prepareStatement(insertQuery);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + insertQuery);
            }

            pstmt.setString(1, programCategoryVO.getRewardType());
            pstmt.setString(2, programCategoryVO.getMaxPeriod());
            pstmt.setString(3, programCategoryVO.getMinPeriod());
            pstmt.setString(4, Boolean.toString(programCategoryVO.isPointsForParent()));
            pstmt.setString(5, programCategoryVO.getProgramType());
            pstmt.setString(6, Boolean.toString(programCategoryVO.isAutoRedempAll()));
            pstmt.setString(7, programCategoryVO.getRedempFrequency());
            pstmt.setString(8, programCategoryVO.getRedempWalletType());
            pstmt.setString(9, programCategoryVO.getProgramEarningType());
            pstmt.setString(10, programCategoryVO.getProgramType());

            updateCount = pstmt.executeUpdate();
            if (updateCount <= 0) {
                throw new SQLException(methodName);
            } else {
                p_con.commit();
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProgramCategoryDAO[updateProgramRules]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProgramCategoryDAO[updateProgramRules]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: Insert Count =" + updateCount);
            }
        }
        return updateCount;
    }

    public ArrayList<ProgramCategoryVO> checkIfRuleSetExists(Connection p_con, ProgramCategoryVO programCategoryVO) throws BTSLBaseException {
        final String methodName = "checkIfRuleSetExists";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        final ArrayList<ProgramCategoryVO> list = new ArrayList<ProgramCategoryVO>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" select REWARD_TYPES, MAX_PERIOD,MIN_PERIOD,POINTS_EARN_ALLOWED,PROFILE_TYPE,REDEEMPTION_ALLOWED,REDEEMTION_FREQ,WALLET_ALLOWED,EARNING_TYPE  ");
        strBuff.append(" from PROGRAM_CATEGORY where profile_type=? ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, programCategoryVO.getProgramType());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                programCategoryVO.setRewardType(rs.getString("REWARD_TYPES"));
                programCategoryVO.setMaxPeriod(rs.getString("MAX_PERIOD"));
                programCategoryVO.setMinPeriod(rs.getString("MIN_PERIOD"));
                programCategoryVO.setPointsForParent(Boolean.valueOf(rs.getString("POINTS_EARN_ALLOWED")));
                programCategoryVO.setProgramType(rs.getString("PROFILE_TYPE"));
                programCategoryVO.setAutoRedempAll(Boolean.valueOf(rs.getString("REDEEMPTION_ALLOWED")));
                programCategoryVO.setRedempFrequency(rs.getString("REDEEMTION_FREQ"));
                programCategoryVO.setRedempWalletType(rs.getString("WALLET_ALLOWED"));
                programCategoryVO.setProgramEarningType(rs.getString("EARNING_TYPE"));

                programCategoryVO.setProgramEarningSelList((Arrays.asList(rs.getString("EARNING_TYPE").split(","))));
                programCategoryVO.setRewardTypeSelList((Arrays.asList(rs.getString("REWARD_TYPES").split(","))));
                programCategoryVO.setRedempFreqSelList((Arrays.asList(rs.getString("REDEEMTION_FREQ").split(","))));
                programCategoryVO.setRedempWalletSelList((Arrays.asList(rs.getString("WALLET_ALLOWED").split(","))));
                list.add(programCategoryVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProgramCategoryDAO[checkIfRuleSetExists]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProgramCategoryDAO[checkIfRuleSetExists]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: rule set Size =" + rs);
            }
        }
        return list;
    }

}
