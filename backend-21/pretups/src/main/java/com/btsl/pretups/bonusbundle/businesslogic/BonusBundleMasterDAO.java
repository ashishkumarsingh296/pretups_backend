/*
 * Created on Dec 29, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.bonusbundle.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;

/**
 * @author rajdeep.deb
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class BonusBundleMasterDAO {

    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * 
     * @param pCon
     * @param pLookupType
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadBundleList(Connection pCon, String pLookupType) throws BTSLBaseException {
        
    	   final String methodName = "loadBundleList";
    	if (log.isDebugEnabled()) {
            log.debug( methodName , " Entered ");
        }
     
        ArrayList bundleVOList = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BonusBundleMasterVO bonusVO = null;
        int size=0;
        try {
            bundleVOList = new ArrayList();
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("SELECT distinct bbm.bundle_id,bbm.bundle_name,bbm.bundle_code,bbm.bundle_type,bbm.status,bbm.res_in_status,l.lookup_name ");
            strBuff.append("from BONUS_BUNDLE_MASTER bbm,LOOKUPS l,LOOKUP_TYPES lt where bbm.status in ('Y','S') and l.lookup_code=bbm.bundle_type ");
            strBuff.append("and lt.LOOKUP_TYPE=? and lt.LOOKUP_TYPE=l.LOOKUP_TYPE order by bbm.bundle_id");
            if (log.isDebugEnabled()) {
                log.debug(methodName , " Query " + strBuff.toString());
            }
            pstmt = pCon.prepareStatement(strBuff.toString());
            pstmt.setString(1, pLookupType);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                bonusVO = new BonusBundleMasterVO();
                bonusVO.setBundleId(rs.getInt("bundle_id"));
                bonusVO.setBundleName(rs.getString("bundle_name"));
                bonusVO.setBundleCode(rs.getString("bundle_code"));
                bonusVO.setBundleType(rs.getString("bundle_type"));
                bonusVO.setBundleTypeDes(rs.getString("lookup_name"));
                bonusVO.setBundleStatus(rs.getString("status"));
                bonusVO.setResponseFrmIN(rs.getString("res_in_status"));
                if ("Y".equals(rs.getString("status"))) {
                    bonusVO.setBundleStatusDes("Active");
                } else if ("S".equals(rs.getString("status"))) {
                    bonusVO.setBundleStatusDes("Suspended");
                }
                if ("Y".equals(rs.getString("res_in_status"))) {
                    bonusVO.setResponseINDes("Active");
                } else if ("N".equals(rs.getString("res_in_status"))) {
                    bonusVO.setResponseFrmIN(PretupsI.SUSPEND);
                    bonusVO.setResponseINDes("Suspended");
                }
                bundleVOList.add(bonusVO);
            }
            if(!bundleVOList.isEmpty()){
            	size=bundleVOList.size();
            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Exiting " + size);
        }
        return bundleVOList;
    }

    /**
     * 
     * @param pCon
     * @param pBonusVO
     * @return int
     * @throws BTSLBaseException
     */
    public int saveBundleDetails(Connection pCon, BonusBundleMasterVO pBonusVO) throws BTSLBaseException {
        final String methodName = "saveBundleDetails";
        if (log.isDebugEnabled()) {
            log.debug("saveBundleDetails ", " Entered ");
        }
        int update = 0;
        PreparedStatement pstmt = null;
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO BONUS_BUNDLE_MASTER ( BUNDLE_ID, BUNDLE_NAME, BUNDLE_CODE, BUNDLE_TYPE, STATUS,RES_IN_STATUS ) VALUES ( ?, ?, ?, ?, ?, ?)");
            if (log.isDebugEnabled()) {
                log.debug("saveBundleDetails ", " Query " + strBuff.toString());
            }
            pstmt = pCon.prepareStatement(strBuff.toString());
            int i = 0;
            pstmt.setInt(++i, pBonusVO.getBundleId());
            pstmt.setString(++i, pBonusVO.getBundleName());
            pstmt.setString(++i, pBonusVO.getBundleCode());
            pstmt.setString(++i, pBonusVO.getBundleType());
            pstmt.setString(++i, pBonusVO.getBundleStatus());
            // modified by nilesh
            if (PretupsI.SUSPEND.equals(pBonusVO.getResponseFrmIN())) {
                pstmt.setString(++i, PretupsI.NO);
            } else {
                pstmt.setString(++i, pBonusVO.getResponseFrmIN());
            }
            update = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
        } finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        }
        if (log.isDebugEnabled()) {
            log.debug("saveBundleDetails ", " Exiting " + update);
        }
        return update;
    }

    /**
     * 
     * @param pCon
     * @return int
     */
    public int getMaxBundleID(Connection pCon) {
        final String methodName = "getMaxBundleID";
        int maxId = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("select MAX(bundle_id) maxid from BONUS_BUNDLE_MASTER");
            pstmt = pCon.prepareStatement(strBuff.toString());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                maxId = rs.getInt("maxid");
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        }
        return maxId;
    }

    /**
     * 
     * @param pCon
     * @param pBundleId
     * @return
     */
    public int deleteBundles(Connection pCon, int pBundleId) throws BTSLBaseException {

        final String methodName = "deleteBundles";
		String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered ");
        }
        int deleteBundle = 0;
        PreparedStatement pstmt = null;
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append(" UPDATE BONUS_BUNDLE_MASTER SET STATUS='N' WHERE BUNDLE_ID=?");
			
            if (QueryConstants.DB_POSTGRESQL.equals(dbConnected))
			strBuff.append(" AND to_char(BUNDLE_ID,'99999D') NOT IN ( SELECT SELECTOR_CODE");
            else
			strBuff.append(" AND to_char(BUNDLE_ID) NOT IN ( SELECT SELECTOR_CODE");
			strBuff.append(" FROM SERVICE_TYPE_SELECTOR_MAPPING");
            strBuff.append(" WHERE SERVICE_TYPE in ('" + PretupsI.SERVICE_TYPE_P2PRECHARGE + "','" + PretupsI.SERVICE_TYPE_CHNL_RECHARGE + "'))");
            pstmt = pCon.prepareStatement(strBuff.toString());
			if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered 111" +strBuff );
			}
            pstmt.setInt(1, pBundleId);
            deleteBundle = pstmt.executeUpdate();
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        } finally {
            try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Exiting ");
        }
        return deleteBundle;
    }

    /**
     * 
     * @param pCon
     * @return
     * @throws BTSLBaseException
     */
    /*
     * Added By Babu Kunwar For C2SBonus Report
     */
    public ArrayList loadBundleNameList(Connection pCon) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadBundleNameList ", " Entered ");
        }
        ArrayList<ListValueVO> bonusNameList = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        ListValueVO bonusVO = null;
        final String methodName = "loadBundleNameList";
        int size=0;
        try {
            StringBuilder queryString = new StringBuilder();
            bonusNameList = new ArrayList<ListValueVO>();
            queryString.append("select distinct bundle_name,bundle_id from bonus_bundle_master");
            if (log.isDebugEnabled()) {
                log.debug("loadBundleNameList", " Query :" + queryString.toString());
            }
            pstmt = pCon.prepareStatement(queryString.toString());
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                bonusVO = new ListValueVO(resultSet.getString("bundle_name"), resultSet.getString("bundle_id"));
                bonusNameList.add(bonusVO);
            }
            if(!bonusNameList.isEmpty()){
            	size=bonusNameList.size();
            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        finally {
        	try{
                if (resultSet!= null){
                	resultSet.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        }
        if (log.isDebugEnabled()) {
            log.debug("loadBundleList ", " Exiting " +size);
        }
        return bonusNameList;
    }

    /*
     * Addition by Babu Kunwar Ends
     */

    /**
     * 
     * @param pCon
     * @param pBundleId
     * @return BonusBundleMasterVO
     */
    /**
     * @param pCon
     * @param pBonusVO
     * @return
     * @throws BTSLBaseException
     */
    public int modifyBundleDetails(Connection pCon, BonusBundleMasterVO pBonusVO) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("modifyBundleDetails ", " Entered ");
        }
        final String methodName = "modifyBundleDetails";
        PreparedStatement pstmt = null;
        int updateRes = 0;
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE BONUS_BUNDLE_MASTER SET BUNDLE_NAME=?,BUNDLE_CODE=?,");
            strBuff.append("BUNDLE_TYPE=?,STATUS=?,RES_IN_STATUS=? WHERE BUNDLE_ID=?");
            if (log.isDebugEnabled()) {
                log.debug("modifyBundleDetails", " Query :" + strBuff.toString());
            }
            pstmt = pCon.prepareStatement(strBuff.toString());
            pstmt.setString(1, pBonusVO.getBundleName());
            pstmt.setString(2, pBonusVO.getBundleCode());
            pstmt.setString(3, pBonusVO.getBundleType());
            pstmt.setString(4, pBonusVO.getBundleStatus());
            // modified by nilesh
            if (PretupsI.SUSPEND.equals(pBonusVO.getResponseFrmIN())) {
                pstmt.setString(5, PretupsI.NO);
            } else {
                pstmt.setString(5, pBonusVO.getResponseFrmIN());
            }
            pstmt.setInt(6, pBonusVO.getBundleId());
            updateRes = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
        } finally {
            try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        }
        if (log.isDebugEnabled()) {
            log.debug("modifyBundleDetails ", " Exiting ");
        }
        return updateRes;
    }

}