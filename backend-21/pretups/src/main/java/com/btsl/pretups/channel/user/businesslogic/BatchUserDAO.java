package com.btsl.pretups.channel.user.businesslogic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/*
 * @# BatchUserDAO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Ved prakash July 14, 2006 Initial creation
 * Amit Ruwali July 20,2006 Modified
 * Samna Soin November 15,2011 Modified
 * Shashank Gaur April 22,2013
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Bharti Telesoft Ltd.
 * This class use for batch user creation.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.batch.businesslogic.BatchesVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddRptReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddStsRespDTO;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchesLog;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.BooleanOperator;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;
import com.btsl.util.SqlParameterEncoder;
import com.opencsv.CSVWriter;
import com.restapi.channelAdmin.BulkModifyListVO;
import com.txn.pretups.channel.transfer.businesslogic.BulkUserAddStsReportWriter;
import com.web.pretups.channel.user.web.BatchBarForDelForm;

/**
 * @author ved.sharma
 * 
 */
public class BatchUserDAO {
    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private BatchUserQry batchUserQry =(BatchUserQry) ObjectProducer.getObject(QueryConstants.BATCH_USER_QRY, QueryConstants.QUERY_PRODUCER);

    /**
     * Method :loadMasterCategoryList
     * This method load list of Categories on the basis of domain code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadMasterCategoryList(Connection p_con, String p_domainCode, String p_categoryCode, String p_userType) throws BTSLBaseException {
        final String methodName = "loadMasterCategoryList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_domainCode=" + p_domainCode + "p_userType=" + p_userType + "p_categoryCode=" + p_categoryCode);
        }
         
       
        final StringBuffer strBuff = new StringBuffer("SELECT category_code,category_name,web_interface_allowed,");

        // for Zebra and Tango by Sanjeew date 09/07/07
        strBuff.append(" low_bal_alert_allow, ");
        // End Zebra and Tango

        strBuff.append("sms_interface_allowed,authentication_type,GRPH_DOMAIN_TYPE FROM categories WHERE domain_code=? AND status='Y' ");

        // Changes made for Batch user initiate for channel user
        if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
            strBuff.append(" AND sequence_no>(select sequence_no from  categories where category_code=?)");
        }
        // End of Changes made for Batch user initiate for channel user

        strBuff.append("ORDER BY sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList categoryList = new ArrayList();
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            int i = 1;
            pstmtSelect.setString(i++, p_domainCode);
            // Changes made for Batch user initiate for channel user
            if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
                pstmtSelect.setString(i++, p_categoryCode);
            }
            // End of Changes made for Batch user initiate for channel user
            try( ResultSet rs = pstmtSelect.executeQuery();)
            {
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                categoryVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                categoryVO.setWebInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("web_interface_allowed")));
                categoryVO.setSmsInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("sms_interface_allowed")));

                // for Zebra and Tango by Sanjeew date 09/07/07
                categoryVO.setLowBalAlertAllow(SqlParameterEncoder.encodeParams(rs.getString("low_bal_alert_allow")));
                // End Zebra and Tango
                // Added for Authentication type
                categoryVO.setAuthenticationType(SqlParameterEncoder.encodeParams(rs.getString("authentication_type")));
                categoryVO.setGrphDomainType(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_TYPE")));
                categoryList.add(categoryVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: categoryList size =" + categoryList.size());
            }
        }
        return categoryList;
    }

    /**
     * Method :loadMasterCategoryGradeList
     * This method load list of Grade on the basis of domain code .
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadMasterCategoryGradeList(Connection p_con, String p_domainCode, String p_categoryCode, String p_userType) throws BTSLBaseException {
        final String methodName = "loadMasterCategoryGradeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_domainCode=" + p_domainCode + "p_userType=" + p_userType + "p_categoryCode=" + p_categoryCode);
        }
        
        final StringBuffer strBuff = new StringBuffer(
            " SELECT CAT.sequence_no,CAT.category_code,CAT.category_name,CG.grade_code, CG.grade_name FROM categories CAT, channel_grades CG ");
        strBuff.append(" WHERE CAT.domain_code=? AND CAT.status='Y' AND CG.status='Y' AND CAT.category_code=CG.category_code ");
        // Changes made for Batch user initiate for channel user
        if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
            strBuff.append(" AND CAT.sequence_no>(select sequence_no from  categories where category_code=?)");
        }
        // End of Changes made for Batch user initiate for channel user
        strBuff.append(" ORDER BY CAT.sequence_no ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList gradeList = new ArrayList();
        ;
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            int i = 1;
            pstmtSelect.setString(i++, p_domainCode);
            // Changes made for Batch user initiate for channel user
            if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
                pstmtSelect.setString(i++, p_categoryCode);
            }
            // End of Changes made for Batch user initiate for channel user
            try(ResultSet rs = pstmtSelect.executeQuery();){
            GradeVO gradeVO = null;
            String categoryCode = null;
            while (rs.next()) {
                gradeVO = new GradeVO();
                categoryCode = SqlParameterEncoder.encodeParams(rs.getString("category_code"));
                gradeVO.setCategoryCode(categoryCode);
                gradeVO.setGradeCode(SqlParameterEncoder.encodeParams(rs.getString("grade_code")));
                gradeVO.setGradeName(SqlParameterEncoder.encodeParams(rs.getString("grade_name")));
                gradeVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                gradeList.add(gradeVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterCategoryGradeList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterCategoryGradeList]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: gradeList size =" + gradeList.size());
            }
        }
        return gradeList;
    }
    
    
    /*public void resetCounterDaily(Connection p_con, UserVO userVO) throws SQLException{
    	final String methodName = "resetCounterDaily";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered: user s=" + userVO.getLoginID());
		}
		PreparedStatement pstmtSelect = null;
		PreparedStatement pstmtUpdate = null;
		
		ResultSet rs = null;
		
		Date currDate = new Date();
		Date counterDate = null;
		
		int updateCount = 0;
		final String sqlSelect = "select last_update_Date,user_id from daily_file_counter where module='DVD'";
		try {
			pstmtSelect = p_con.prepareStatement(sqlSelect);
			rs = pstmtSelect.executeQuery();
			while (rs.next()) {
				counterDate = rs.getDate("last_update_date");
				if(BTSLUtil.getDifferenceInUtilDates(currDate, counterDate) != 0){
					StringBuffer strBuff = new StringBuffer("UPDATE DAILY_FILE_COUNTER SET counters=?,last_update_date=?");
					strBuff.append("WHERE USER_ID = ?");
					final String sqlUpdate = strBuff.toString();
					pstmtUpdate = p_con.prepareStatement(sqlUpdate);
					
					pstmtUpdate.setInt(1,0);
					pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currDate));
					pstmtUpdate.setString(3, rs.getString("user_id"));
					updateCount = pstmtUpdate.executeUpdate();
					if(updateCount >0 ){
						p_con.commit();
					}
						
				}
				
			}
		}
		finally{
			try{
			if(rs!=null)
				rs.close();
			}catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
			try{
				if(pstmtSelect!=null)
					pstmtSelect.close();
				}catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
			try{
				if(pstmtUpdate!=null)
					pstmtUpdate.close();
				}catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
			
		}
    }*/
    /**
     * MethodName : getDailyDVDCountForUser
     * This is used to get the count of the files uploaded by a user for DVD in a day.
     * @param p_con
     * @param userVO
     * @return
     * @throws BTSLBaseException
     */

	/*public int getDailyDVDCountForUser(Connection p_con, UserVO userVO) throws BTSLBaseException {
		final String methodName = "getDVDCountForUser";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered: user s=" + userVO.getLoginID());
		}
		PreparedStatement pstmtSelect = null;
		PreparedStatement pstmtInsert = null;
		ResultSet rs = null;
		int userCount = 0;
		int insertCount = 0;

		final String sqlSelect = "select counters from daily_file_counter where user_id = ?";
		try {
			pstmtSelect = p_con.prepareStatement(sqlSelect);
			pstmtSelect.setString(1, userVO.getUserID());

			rs = pstmtSelect.executeQuery();
			if (rs.next()) {
				userCount = rs.getInt("counters");
			}
			else{
				StringBuffer sb = new StringBuffer();
				sb.append("insert into daily_file_counter");
				sb.append("(user_id, msisdn,network_id,counters,module,last_update_date)");
				sb.append("values(?, ?, ?, ?, ?, ?)");
			
				Date date = new Date();
				final String sqlUpdate = sb.toString();
				pstmtInsert = p_con.prepareStatement(sqlUpdate);

				pstmtInsert.setString(1, userVO.getUserID());
				pstmtInsert.setString(2, userVO.getMsisdn());
				pstmtInsert.setString(3, userVO.getNetworkID());
				pstmtInsert.setInt(4, 0);
				pstmtInsert.setString(5, "DVD");
				pstmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(date));
				
				insertCount = pstmtInsert.executeUpdate();
				 
			}
			
		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"BatchUserDAO[loadMasterTransferProfileList]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		finally {

			if(insertCount > 0)
			{
				try {
					p_con.commit();
				} catch (SQLException e) {
	                _log.errorTrace(methodName, e);
				}
			}
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
                if (pstmtInsert != null) {
                	pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: Count for user is =" + String.valueOf(userCount));
            }
        }
		return userCount;
	}
	*/

	/**
	 * MethodName: updateDailyCountForUser
	 * This is used to update the daily count of the files by a user.
	 * @param p_con
	 * @param userVO
	 * @throws BTSLBaseException
	 */
	/*public void updateDailyCountForUser(Connection p_con, UserVO userVO) throws BTSLBaseException
	{
		final String methodName = "updateDailyCountForUser";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered: p_userID=" + userVO.getUserID());
		}
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		PreparedStatement pstmtUpdate = null;
		int userCount = 0;
		int updateCount = 0;
		
		StringBuffer strBuff = new StringBuffer("UPDATE DAILY_FILE_COUNTER SET counters=?,last_update_date=?");
		strBuff.append("WHERE USER_ID = ?");
		Date date = new Date();
		final String sqlUpdate = strBuff.toString();
			
		try {
			
			userCount = getDailyDVDCountForUser(p_con, userVO);
					
			pstmtUpdate = p_con.prepareStatement(sqlUpdate);
			pstmtUpdate.setInt(1, userCount+1);
			pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
			pstmtUpdate.setString(3, userVO.getUserID());
			
			updateCount = pstmtUpdate.executeUpdate();
		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"BatchUserDAO[loadMasterTransferProfileList]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		finally {
            try {
            	if(updateCount > 0)
            	{
            		p_con.commit();
            	}
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
                if (pstmtUpdate != null) {
                	pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: Count for user is =" + userCount );
            }
        }
		
	}*/
	/**
	 * MethodName:getDailyDVDCountForNetwork
	 * This is used to get the count of the files uploaded on a network in a day for DVD.
	 * @param p_con
	 * @param p_networkID
	 * @return
	 * @throws BTSLBaseException
	 */
	/*public int getDailyDVDCountForNetwork(Connection p_con, String p_networkID) throws BTSLBaseException {
		final String methodName = "getDailyDVDCountForNetwork";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered: p_networkID=" + p_networkID);
		}
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		int networkCount = -1;
		String sqlSelect = "";
        StringBuffer strBuff = new StringBuffer("select sum(counters) as total from daily_file_counter ");
        strBuff.append("where network_id = ? ");
        strBuff.append("group by network_id");
        
        sqlSelect = strBuff.toString();
		try {
			pstmtSelect = p_con.prepareStatement(sqlSelect);
			pstmtSelect.setString(1, p_networkID);

			rs = pstmtSelect.executeQuery();
			while (rs.next()) {
				networkCount = rs.getInt("total");
			}
		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"BatchUserDAO[loadMasterTransferProfileList]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		finally {
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
                _log.debug(methodName, "Exiting: Count for network is =" + networkCount );
            }
        }
		return networkCount;
	}
	
	*//**
	 * MethodName:getDailyTotalDVDCount
	 * This is used to get the count of files uploaded by a user for DVD in a day by a user.
	 * @param p_con
	 * @return
	 * @throws BTSLBaseException
	 *//*
	
	public int getDailyTotalDVDCount(Connection p_con) throws BTSLBaseException {
		final String methodName = "getDVDCountForUser";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		int totalCount = -1;
		final String sqlSelect = "select sum(counters) as total from daily_file_counter";
		
		try {
			pstmtSelect = p_con.prepareStatement(sqlSelect);

			rs = pstmtSelect.executeQuery();
			while (rs.next()) {
				totalCount = rs.getInt("total");
			}
		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"BatchUserDAO[loadMasterTransferProfileList]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		finally {
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
                _log.debug(methodName, "Exiting: Count for DVD is =" + totalCount );
            }
        }
		return totalCount;
	}*/
    /**
     * Method :loadMasterTransferProfileList
     * This method load list of Transfer Profile on the basis of domain code .
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @param p_networkCode
     *            TODO
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadMasterTransferProfileList(Connection p_con, String p_domainCode, String p_networkCode, String p_categoryCode, String p_userType) throws BTSLBaseException {
        final String methodName = "loadMasterTransferProfileList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                "Entered: p_domainCode=" + p_domainCode + " p_networkCode=" + p_networkCode + "p_userType=" + p_userType + "p_categoryCode=" + p_categoryCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        
         StringBuffer strBuff = null;
        
         String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
         boolean tcpOn = false;
         Set<String> uniqueTransProfileId = new HashSet();
         
         if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
         	tcpOn = true;
         }
         String sqlSelect = null;
         HashMap<String, HashMap<String, String>> tcpMap = null;
         java.util.List<HashMap<String, String>> resultSet = null;
         
//         if(tcpOn) { as per Akhilesh commented.
//
//
//             strBuff = new StringBuffer(" SELECT CAT.category_code,CAT.category_name FROM categories CAT ");
//             strBuff.append(" WHERE CAT.domain_code=? AND CAT.status='Y'  ");
//            // strBuff.append("AND CAT.category_code=TP.category_code AND TP.status IN('Y','S')  AND TP.network_code=? ");
//             // Changes made for Batch user initiate for channel user
//             if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
//                 strBuff.append(" AND CAT.sequence_no>(select sequence_no from  categories where category_code=?)");
//             }
//             // End of Changes made for Batch user initiate for channel user
//             strBuff.append(" ORDER BY CAT.sequence_no  ");
//             
//             //profile_id, profile_name
//             //Search - parent_profile_id='USER'
//                 //AND CAT.category_code=TP.category_code
//             	//AND CAT.category_code=TP.category_code 
//               // TP.status IN('Y','S')
//             //TP.network_code=? 
//             
//         	SearchCriteria searchCriteria = new SearchCriteria("parent_profile_id", Operator.EQUALS, "USER",
// 					ValueType.STRING, null);
//         
//         	resultSet = BTSLUtil.invokeService("TRANSFER_PROFILE", new HashSet<String>(Arrays.asList("profile_id","status","profile_name", "network_code")),
//         			
//         			searchCriteria);
//         	
//             
//         }else {


        strBuff = new StringBuffer(" SELECT CAT.category_code,CAT.category_name,TP.profile_id, TP.profile_name FROM categories CAT,transfer_profile TP ");
        strBuff.append(" WHERE CAT.domain_code=? AND CAT.status='Y' AND TP.parent_profile_id='USER' ");
        strBuff.append("AND CAT.category_code=TP.category_code AND TP.status IN('Y','S')  AND TP.network_code=? ");
        // Changes made for Batch user initiate for channel user
        if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
            strBuff.append(" AND CAT.sequence_no>(select sequence_no from  categories where category_code=?)");
        }
        // End of Changes made for Batch user initiate for channel user
        strBuff.append(" ORDER BY CAT.sequence_no  ");
        
//         }
        
        
        sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList trfProfileList = new ArrayList();
        final ArrayList trfProfileList2 = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            
            int i=1;            
            pstmtSelect.setString(i, p_domainCode);
            
            if(!tcpOn) {
            i++;
            pstmtSelect.setString(i, p_networkCode);
            // Changes made for Batch user initiate for channel user
            }
            
            i++;
            if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
                pstmtSelect.setString(i, p_categoryCode);
            }
            // End of Changes made for Batch user initiate for channel user
            rs = pstmtSelect.executeQuery();
            TransferProfileVO profileVO = null;
            while (rs.next()) {
           
            	
                profileVO = new TransferProfileVO();
                
                profileVO.setCategory(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                
                
                profileVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                
                if(!tcpOn) {
                profileVO.setProfileId(SqlParameterEncoder.encodeParams(rs.getString("profile_id")));
                profileVO.setProfileName(SqlParameterEncoder.encodeParams(rs.getString("profile_name")));
                }
                
                trfProfileList.add(profileVO);
            }
            
            
            if(tcpOn) {
            Iterator<TransferProfileVO> itr = trfProfileList.iterator();
            
        	while(itr.hasNext()) {
        		
        		TransferProfileVO transferProfileVOObj = itr.next() ;

        		String catCode = transferProfileVOObj.getCategory();
        	
        		Iterator<HashMap<String, String>> itrTcp = resultSet.iterator();
        		
        		while(itrTcp.hasNext()) {
        			
        			HashMap<String, String> tcpMapObj = itrTcp.next();
        			if(tcpMapObj.get("categoryCode") != null && tcpMapObj.get("categoryCode").equalsIgnoreCase(transferProfileVOObj.getCategory())  && ( tcpMapObj.get("status").equalsIgnoreCase("Y") || tcpMapObj.get("status").equalsIgnoreCase("Y") )) {
        				
        				transferProfileVOObj.setProfileId(tcpMapObj.get("profileId"));
        				transferProfileVOObj.setProfileName(tcpMapObj.get("categoryName"));
        				trfProfileList2.add(transferProfileVOObj);
        				break;
        			}
        			
        		}
    			
    		}
            }
        	
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterTransferProfileList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterTransferProfileList]", "", "",
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: trfProfileList size =" + trfProfileList.size());
            }
        }
        return trfProfileList;
    }

    /**
     * Method :loadMasterGroupRoleList
     * This method load list of Group roles on the basis of domain code .
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadMasterGroupRoleList(Connection p_con, String p_domainCode, String p_categoryCode, String p_userType) throws BTSLBaseException {
        final String methodName = "loadMasterGroupRoleList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_domainCode=" + p_domainCode + "p_userType=" + p_userType + "p_categoryCode=" + p_categoryCode);
        }
        
        final StringBuffer strBuff = new StringBuffer(" SELECT CAT.category_code,CR.role_code,R.role_name,R.group_name,R.is_default FROM categories CAT,category_roles CR, roles R ");
        strBuff.append(" WHERE CAT.domain_code=? AND CAT.status='Y' AND R.status='Y' AND CAT.category_code=CR.category_code AND CR.role_code=R.role_code AND R.group_role=? ");
        // Changes made for Batch user initiate for channel user
        if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
            strBuff.append(" AND CAT.sequence_no>=(select sequence_no from  categories where category_code=?)");
        }
        // End of Changes made for Batch user initiate for channel user
        strBuff.append(" ORDER BY CAT.sequence_no  ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList roleList = new ArrayList();
        try (PreparedStatement  pstmtSelect = p_con.prepareStatement(sqlSelect);){
            
            int i = 1;
            pstmtSelect.setString(i++, p_domainCode);
            pstmtSelect.setString(i++, TypesI.YES);
            // Changes made for Batch user initiate for channel user
            if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
                pstmtSelect.setString(i++, p_categoryCode);
            }
            // End of Changes made for Batch user initiate for channel user
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            UserRolesVO rolesVO = null;
            while (rs.next()) {
                rolesVO = new UserRolesVO();
                rolesVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                rolesVO.setRoleCode(SqlParameterEncoder.encodeParams(rs.getString("role_code")));
                rolesVO.setRoleName(SqlParameterEncoder.encodeParams(rs.getString("role_name")));
                rolesVO.setGroupName(SqlParameterEncoder.encodeParams(rs.getString("group_name")));
                rolesVO.setDefaultType(SqlParameterEncoder.encodeParams(rs.getString("is_default")));
                roleList.add(rolesVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterGroupRoleList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterGroupRoleList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: roleList size =" + roleList.size());
            }
        }
        return roleList;
    }

    /**
     * Method :loadBatchListForApproval
     * This method load list of batches.
     * 
     * @param p_con
     *            Connection
     * @param p_geographyCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_domainCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadBatchListForApproval(Connection p_con, String p_geographyCode, String p_networkCode, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadBatchListForApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_geographyCode=" + p_geographyCode + " p_networkCode=" + p_networkCode + " p_domainCode=" + p_domainCode);
        }
        

        final String geographyCode = p_geographyCode.replaceAll("'", "");
        final String gg = geographyCode.replaceAll("\" ", "");
        final String m_geographyCode[] = gg.split(",");

        final String domainCode = p_domainCode.replaceAll("'", "");
        final String dd = domainCode.replaceAll("\" ", "");
        final String m_domainCode[] = dd.split(",");
       
        final String sqlSelect = batchUserQry.loadBatchListForApprovalQry(m_geographyCode, m_domainCode);
     
        final ArrayList batchList = new ArrayList();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);){
            
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_NEW);
            if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
                pstmtSelect.setString(i++, PretupsI.USER_STATUS_PREACTIVE);
            } else {
                pstmtSelect.setString(i++, PretupsI.USER_STATUS_ACTIVE);
            }
            pstmtSelect.setString(i++, PretupsI.BATCH_USR_CREATION_TYPE);
            pstmtSelect.setString(i++, PretupsI.USR_BATCH_STATUS_OPEN);

            for (int x = 0; x < m_geographyCode.length; x++) {
                pstmtSelect.setString(i++, m_geographyCode[x]);
            }
            pstmtSelect.setString(i++, p_networkCode);
            for (int x = 0; x < m_domainCode.length; x++) {
                pstmtSelect.setString(i++, m_domainCode[x]);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            BatchesVO batchesVO = null;
            while (rs.next()) {
                batchesVO = new BatchesVO();
                batchesVO.setBatchID(rs.getString("batch_id"));
                batchesVO.setStatus(rs.getString("status"));
                batchesVO.setBatchName(rs.getString("batch_name"));
                batchesVO.setBatchSize(rs.getLong("batch_size"));
                batchesVO.setBatchType(rs.getString("batch_type"));
                batchesVO.setCreatedBy(rs.getString("created_by"));
                batchesVO.setCreatedOn(rs.getTimestamp("created_on"));
                if (batchesVO.getCreatedOn() != null) {
                    batchesVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchesVO.getCreatedOn())));
                }
                batchesVO.setModifiedBy(rs.getString("modified_by"));
                batchesVO.setModifiedOn(rs.getTimestamp("modified_on"));
                if (batchesVO.getModifiedOn() != null) {
                    batchesVO.setModifiedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchesVO.getModifiedOn())));
                }
                batchesVO.setActiveRecords(rs.getInt("active"));
                batchesVO.setNewRecords(rs.getInt("new"));
                // added by shashank
                batchesVO.setIntiatorUserType(rs.getString("intiator_type"));
                batchesVO.setInitiatorCategory(rs.getString("initiator_category"));
                // end
                batchList.add(batchesVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchListForApproval]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchListForApproval]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: batchList size =" + batchList.size());
            }
        }
        return batchList;
    }

    /**
     * Method :loadBatchDetailsList
     * This method load list of batches details.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_batchID
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadBatchDetailsList(Connection p_con, String p_batchID, String p_intiatorType) throws BTSLBaseException {
        final String methodName = "loadBatchDetailsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_batchID=" + p_batchID + ",p_intiatorType=" + p_intiatorType);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PreparedStatement pstmtSelectForSrevice = null;
        ResultSet rsForService = null;
                      
        PreparedStatement pstmtSelectForRoles = null;
        ResultSet rsForRoles = null;
        PreparedStatement pstmtSelectForMsisdn = null;
        ResultSet rsForMsisdn = null;
        UserPhoneVO userPhoneVO = null;
        ArrayList msisdnList = null;
        final String sqlSelect = batchUserQry.loadBatchDetailsListQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect: " + sqlSelect);
        }

        // Modification for Service Management [by Vipul]
        final StringBuffer selectServices = new StringBuffer("SELECT US.service_type, US.user_id, US.status");
        selectServices.append(" FROM user_services US,users U,category_service_type CST");
        selectServices
            .append(" WHERE US.user_id=? AND US.status='Y' AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type and CST.network_code=U.network_code");

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectServices: " + selectServices);
        }
      //Voucher Type
        PreparedStatement pstmtVouchers = null;
        ResultSet rsVouchers = null;
        String vouchers = null;
        StringBuffer strBuffVouchers= new StringBuffer(" SELECT uv.voucher_type, vt.name ");
        strBuffVouchers.append(" FROM user_vouchertypes uv, voms_types vt, users u ");
        strBuffVouchers.append(" WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id ");
        final String sqlSelectVouchers = strBuffVouchers.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectVouchers=" + sqlSelectVouchers);
        }
        
        
        PreparedStatement pstmtSegments = null;
        ResultSet rsSegments = null;
        String segments = null;
        StringBuffer strBuffSegments= new StringBuffer(" SELECT us.VOUCHER_SEGMENT, lu.LOOKUP_NAME ");
        strBuffSegments.append(" FROM USER_VOUCHER_SEGMENTS us, LOOKUPS lu, users u ");
        strBuffSegments.append(" WHERE u.user_id = ? AND us.VOUCHER_SEGMENT = lu.LOOKUP_CODE  AND u.user_id = us.user_id ");
        final String sqlSelectSegments = strBuffSegments.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectSegments=" + sqlSelectSegments);
        }
        
               
        final StringBuffer selectRoles = new StringBuffer("SELECT UR.role_code FROM user_roles UR WHERE UR.user_id=? ORDER BY UR.role_code");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectRoles: " + selectRoles);
        }
        // Multiple msisdn change start
        final StringBuffer selectMsisdn = new StringBuffer(
            "select msisdn, sms_pin,phone_language,country,PRIMARY_NUMBER from user_phones where user_id=? order by primary_number desc");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectMsisdn: " + selectMsisdn);
            // Multiple msisdn change end
        }

        final ArrayList batchList = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelectForSrevice = p_con.prepareStatement(selectServices.toString());
            pstmtVouchers = p_con.prepareStatement(sqlSelectVouchers);
            pstmtSegments = p_con.prepareStatement(sqlSelectSegments);
            pstmtSelectForRoles = p_con.prepareStatement(selectRoles.toString());
            // Multiple msisdn change start
            pstmtSelectForMsisdn = p_con.prepareStatement(selectMsisdn.toString());
            // Multiple msisdn change end
            int i = 1;
            pstmtSelect.setString(i++, p_batchID);
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_NEW);
            rs = pstmtSelect.executeQuery();
            ChannelUserVO channelUserVO = null;
            CategoryVO categoryVO = null;
            int j = 0;
            String services = null;
            
            String roles = null;
            StringBuffer sb = null;
            while (rs.next()) {
                channelUserVO = new ChannelUserVO();
                channelUserVO.setRecordNumber("" + (j++));
                channelUserVO.setBatchID(rs.getString("batch_id"));
                channelUserVO.setParentStatus(rs.getString("parent_status"));
                channelUserVO.setParentLoginID(rs.getString("parent_login_id"));
                channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setLastLoginOn(rs.getTimestamp("last_login_on"));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("status"));
                // added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                // end
                channelUserVO.setEmail(rs.getString("email"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setUserType(rs.getString("user_type"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setAppointmentDate(rs.getDate("appointment_date"));
                channelUserVO.setPreviousStatus(rs.getString("previous_status"));
                channelUserVO.setCreationType(rs.getString("creation_type"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setSubOutletCode(rs.getString("suboutlet_code"));
                channelUserVO.setOutletCode(rs.getString("outlet_code"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                if (SystemPreferences.USERWISE_LOAN_ENABLE) 
                    channelUserVO.setLoanProfileId(rs.getString("profile_id"));
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
                channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                channelUserVO.setRsaFlag(rs.getString("rsaflag"));
                channelUserVO.setAuthTypeAllowed(rs.getString("authentication_allowed"));
                channelUserVO.setLongitude(rs.getString("longitude"));
                channelUserVO.setLatitude(rs.getString("latitude"));
                channelUserVO.setDocumentType(rs.getString("document_type"));
                channelUserVO.setDocumentNo(rs.getString("document_no"));
                channelUserVO.setPaymentType(rs.getString("payment_type"));
                categoryVO = new CategoryVO();
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                channelUserVO.setCategoryVO(categoryVO);
                if (PretupsI.NO.equals(categoryVO.getFixedRoles())) {
                    pstmtSelectForRoles.clearParameters();
                    pstmtSelectForRoles.setString(1, channelUserVO.getUserID());
                    rsForRoles = pstmtSelectForRoles.executeQuery();
                    roles = "";
                    while (rsForRoles.next()) {
                        roles = roles + rsForRoles.getString("role_code") + ",";
                    }
                    if (roles.length() > 1) {
                        roles = roles.substring(0, roles.length() - 1);
                    }
                    if (rsForRoles != null) {
                        rsForRoles.close();
                    }
                    channelUserVO.setGroupRoleCode(roles);
                }
                if (PretupsI.YES.equals(categoryVO.getServiceAllowed())) {
                    // load services from user services
                    pstmtSelectForSrevice.clearParameters();
                    pstmtSelectForSrevice.setString(1, channelUserVO.getUserID());
                    rsForService = pstmtSelectForSrevice.executeQuery();
                    services = "";
                    while (rsForService.next()) {
                        services = services + rsForService.getString("service_type") + ",";
                    }
                    if (services.length() > 1) {
                        services = services.substring(0, services.length() - 1);
                    }
                    if (rsForService != null) {
                        rsForService.close();
                    }
                    channelUserVO.setServiceTypes(services);
                }
                boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
                //Voucher Type  
                if(userVoucherTypeAllowed) {	               
	                pstmtVouchers.clearParameters();
	                pstmtVouchers.setString(1, channelUserVO.getUserID());
	                rsVouchers = pstmtVouchers.executeQuery();
	                vouchers = "";
	                while(rsVouchers.next()) {
	                	vouchers = vouchers + ", " + rsVouchers.getString("voucher_type");
	                }
	                if (!BTSLUtil.isNullString(vouchers)) {
	                	vouchers = vouchers.substring(1);
	                }
	                channelUserVO.setVoucherTypes(vouchers);
                }
                boolean userVoucherSegmentAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED);
                if(userVoucherSegmentAllowed) {
	                pstmtSegments.clearParameters();
	                pstmtSegments.setString(1, channelUserVO.getUserID());
	                rsSegments = pstmtSegments.executeQuery();
	                vouchers = "";
	                while(rsSegments.next()) {
	                	segments = segments + ", " + rsSegments.getString("voucher_segment");
	                }
	                if (!BTSLUtil.isNullString(segments)) {
	                	segments = segments.substring(1);
	                }
	                channelUserVO.setSegments(segments);
                }
                
                // Multiple msisdn change start
                // load msisdns from user phones
                pstmtSelectForMsisdn.clearParameters();
                pstmtSelectForMsisdn.setString(1, channelUserVO.getUserID());
                rsForMsisdn = pstmtSelectForMsisdn.executeQuery();
                msisdnList = new ArrayList();
                sb = new StringBuffer();
                while (rsForMsisdn.next()) {
                    userPhoneVO = new UserPhoneVO();
                    userPhoneVO.setMsisdn(rsForMsisdn.getString("msisdn"));
                    // added by deepika aggarwal
                    userPhoneVO.setPhoneLanguage(rsForMsisdn.getString("phone_language"));
                    channelUserVO.setLanguage(rsForMsisdn.getString("phone_language") + "_" + rsForMsisdn.getString("country"));
                    // end added by deepika aggarwal
                    sb.append(userPhoneVO.getMsisdn());
                    sb.append(",");
                    if (!BTSLUtil.isNullString(rsForMsisdn.getString("PRIMARY_NUMBER")) && rsForMsisdn.getString("PRIMARY_NUMBER").equalsIgnoreCase(PretupsI.YES)) {
                        channelUserVO.setPrimaryMsisdnPin(rsForMsisdn.getString("sms_pin"));
                    }
                    userPhoneVO.setSmsPin(rsForMsisdn.getString("sms_pin"));
                    msisdnList.add(userPhoneVO);
                }
                if (rsForMsisdn != null) {
                    rsForMsisdn.close();
                }
                channelUserVO.setMsisdnList(msisdnList);
                channelUserVO.setMultipleMsisdnlist(sb.toString().substring(0, sb.toString().length() - 1));

                // Multiple msisdn change end

                batchList.add(channelUserVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchDetailsList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchDetailsList]", "", "", "",
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
                if (rsSegments != null) {
                	rsSegments.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsVouchers != null) {
                	rsVouchers.close();
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
            try {
                if (rsForService != null) {
                    rsForService.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectForSrevice != null) {
                    pstmtSelectForSrevice.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtVouchers != null) {
                	pstmtVouchers.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSegments != null) {
                	pstmtSegments.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsForRoles != null) {
                    rsForRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectForRoles != null) {
                    pstmtSelectForRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsForMsisdn != null) {
                    rsForMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectForMsisdn != null) {
                    pstmtSelectForMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: batchList size =" + batchList.size());
            }
        }
        return batchList;
    }

    /**
     * Method :updateUserForApproval
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            TODO
     * @param p_initiatorUserType
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList updateUserForApproval(Connection p_con, ArrayList p_userDetails, MessageResources p_messages, Locale p_locale, UserVO p_userVO, String p_initiatorUserType) throws BTSLBaseException {
        final String methodName = "updateUserForApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userDetails.size()=" + p_userDetails.size() + " p_messages=" + p_messages + " p_locale=" + p_locale + " p_userVO=" + p_userVO
                .toString() + " p_initiatorUserType " + p_initiatorUserType);
        }
        
        ResultSet rsSelectParentUsrStatus = null;
        final StringBuffer selectParentStatus = new StringBuffer("SELECT UP.status FROM users U, users UP	WHERE U.user_id=? AND U.parent_id=UP.user_id");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectParentStatus=" + selectParentStatus);
        }
        final StringBuffer strBuffUsr = new StringBuffer(" UPDATE users SET level1_approved_by=?, level1_approved_on=?,");
        strBuffUsr.append(" modified_by=?, modified_on=?, status=?, previous_status=?,remarks=?, login_id=?, rsaflag=?,ssn=? WHERE user_id=?");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY strBuffUsr=" + strBuffUsr);
        }
        // modified by shashank
        final StringBuffer strBuffChnlUsr = new StringBuffer(" UPDATE channel_users SET activated_on=? ");
        // if(p_initiatorUserType.equals(PretupsI.CHANNEL_USER_TYPE) &&
        // !SystemPreferences.BATCH_USER_PROFILE_ASSIGN)
        // {
        strBuffChnlUsr.append(" ,user_grade=?, TRANSFER_PROFILE_ID=?, COMM_PROFILE_SET_ID=?, ");
        strBuffChnlUsr.append(" mcommerce_service_allow=?, mpay_profile_id=?, trf_rule_type=?");
        // }
        strBuffChnlUsr.append(" WHERE user_id=? ");
        //
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY strBuffChnlUsr=" + strBuffChnlUsr);
        }
        final ArrayList errorList = new ArrayList();
        int updateCount = 0;
        try(PreparedStatement  pstmtUpdateUsr = p_con.prepareStatement(strBuffUsr.toString());
        		PreparedStatement pstmtUpdateChnlUsr = p_con.prepareStatement(strBuffChnlUsr.toString());
        		PreparedStatement  pstmtSelectParentUsrStatus = p_con.prepareStatement(selectParentStatus.toString());) {
            
            ChannelUserVO channelUserVO = null;
            int updateCountUsr = 0;
            int updateCountChnlUsr = 0;
            ListValueVO errorVO = null;
            int warnings = 0;
            String msg = null;
            String parentStatus = null;
            CategoryVO categoryVO = null;
            PushMessage pushMessage = null;
            BTSLMessages btslPushMessage = null;
            String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
            String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
            final Locale defaultLocale = new Locale(defaultLanguage, defaultCountry);
            final Map userMap = null;
            String paymentNumber = null;
            final CommonUtil commonUtil = null;
            // Email for pin & Password
            EmailSendToUser emailSendToUser = null;
            String subject = null;
            for (int i = 0, j = p_userDetails.size(); i < j; i++) {
                try {
                    updateCountUsr = 0;
                    updateCountChnlUsr = 0;
                    channelUserVO = (ChannelUserVO) p_userDetails.get(i);
                    categoryVO = channelUserVO.getCategoryVO();
                    pstmtSelectParentUsrStatus.clearParameters();
                    pstmtSelectParentUsrStatus.setString(1, channelUserVO.getUserID());
                    rsSelectParentUsrStatus = pstmtSelectParentUsrStatus.executeQuery();
                    if (rsSelectParentUsrStatus.next() && categoryVO.getSequenceNumber() != 1) {
                        parentStatus = rsSelectParentUsrStatus.getString("status");
                        if (parentStatus.equals(PretupsI.USER_STATUS_DELETED)) {
                            channelUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
                            msg = p_messages.getMessage(p_locale, "bulkuser.processuploadedfile.error.parentnotactivedelete", channelUserVO.getUserName());
                            channelUserVO.setRemarks(msg);
                            errorVO = new ListValueVO("WARNING", channelUserVO.getRecordNumber(), msg);
                            errorList.add(errorVO);
                            warnings++;
                            BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Success with warning :=" + p_messages.getMessage(
                                "bulkuser.processuploadedfile.error.parentnotactivedelete", channelUserVO.getUserName()));
                        }

                        if (channelUserVO.getStatus().equals(PretupsI.USER_STATUS_DELETED) || parentStatus.equals(PretupsI.USER_STATUS_ACTIVE)) {
                            pstmtUpdateUsr.setString(5, channelUserVO.getStatus());
                            pstmtUpdateUsr.setString(7, channelUserVO.getRemarks());
                        } else {
                            msg = p_messages.getMessage(p_locale, "bulkuser.processuploadedfile.error.parentnotactivediscard", channelUserVO.getUserName());
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), msg);
                            errorList.add(errorVO);
                            BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.processuploadedfile.error.parentnotactivediscard",
                                channelUserVO.getUserName()));
                            continue;
                        }
                    } else if (categoryVO.getSequenceNumber() == 1)// parent
                    // (owner
                    // user)
                    {
                        pstmtUpdateUsr.setString(5, channelUserVO.getStatus());
                        pstmtUpdateUsr.setString(7, channelUserVO.getRemarks());
                    } else {
                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                            "bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                        continue;
                    }
                    if (channelUserVO.getStatus().trim().equalsIgnoreCase(PretupsI.USER_STATUS_DELETED)) {
                        channelUserVO.setLoginID(channelUserVO.getUserID());
                    }
                    pstmtUpdateUsr.setString(1, channelUserVO.getLevel1ApprovedBy());
                    pstmtUpdateUsr.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLevel1ApprovedOn()));
                    pstmtUpdateUsr.setString(3, channelUserVO.getModifiedBy());
                    pstmtUpdateUsr.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                    pstmtUpdateUsr.setString(6, channelUserVO.getPreviousStatus());
                    pstmtUpdateUsr.setString(8, channelUserVO.getLoginID());
                    // added by shashank for rsa
                    pstmtUpdateUsr.setString(9, channelUserVO.getRsaFlag());
                    pstmtUpdateUsr.setString(10, channelUserVO.getSsn());
                    // end
                    pstmtUpdateUsr.setString(11, channelUserVO.getUserID());
                    updateCountUsr = pstmtUpdateUsr.executeUpdate();
                    pstmtUpdateUsr.clearParameters();
                    if (updateCountUsr <= 0) {
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages
                            .getMessage(p_locale, "bulkuser.processuploadedfile.error.updateusererr"));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.processuploadedfile.error.updateusererr"));
                        continue;
                    }
                    pstmtUpdateChnlUsr.clearParameters();
                    int k = 1;
                    pstmtUpdateChnlUsr.setTimestamp(k, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getActivatedOn()));
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getUserGrade());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getTransferProfileID());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getCommissionProfileSetID());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getMcommerceServiceAllow());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getMpayProfileID());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getTrannferRuleTypeId());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getUserID());

                    // pstmtUpdateChnlUsr.setTimestamp(1,BTSLUtil.getTimestampFromUtilDate(channelUserVO.getActivatedOn()));
                    // pstmtUpdateChnlUsr.setString(2,channelUserVO.getUserID());
                    updateCountChnlUsr = pstmtUpdateChnlUsr.executeUpdate();
                    if (updateCountChnlUsr <= 0) {
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                            "bulkuser.processuploadedfile.error.updatechannelusererr"));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.processuploadedfile.error.updatechannelusererr"));
                        continue;
                    }

                    // for Zebra and Tango by Sanjeew date 10/07/07
                    paymentNumber = null;
                    /*
                     * if(ptupsMobqutyMergd)
                     * {
                     * if(autoPaymentMethod &&
                     * PretupsI.SELECT_CHECKBOX
                     * .equals(channelUserVO.getMcommerceServiceAllow()))
                     * {
                     * commonUtil=new CommonUtil();
                     * userMap=commonUtil.getMapfromUserVO(channelUserVO);
                     * //this map retun payment_method_number as key this
                     * payment_method_number show in message.
                     * paymentNumber=(String)commonUtil.assignPaymentMethod(p_con
                     * ,userMap).get("payment_method_number");
                     * }
                     * }
                     */
                    // End Zebra and Tango

                    updateCount++;
                    p_con.commit();
                    boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
                    boolean loginPasswordAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
                    boolean autoPaymentMethod = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD);
                    // push message in try catch(not for deleted/rejected users)
                    if (channelUserVO.getStatus().equals(PretupsI.USER_STATUS_ACTIVE) || channelUserVO.getStatus().equals(PretupsI.USER_STATUS_PREACTIVE)) {
                        if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                            if (loginPasswordAllowed) {// send
                                // message
                                // for
                                // both
                                // login
                                // id
                                // and
                                // sms
                                // pin
                                final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
                                    .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                                // for Zebra and Tango by Sanjeew date 11/07/07
                                if (ptupsMobqutyMergd) {
                                    if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                        arrArray[2] = paymentNumber;
                                    }
                                }
                                // End Zebra and Tango

                                // Email for pin & password
                                subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });

                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                            } else {
                                if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                                    // send message for both login id and sms
                                    // pin
                                    final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
                                        .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                                    // for Zebra and Tango by Sanjeew date
                                    // 11/07/07
                                    if (ptupsMobqutyMergd) {
                                        if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                            arrArray[2] = paymentNumber;
                                        }
                                    }
                                    // End Zebra and Tango
                                    subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });
                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                                } else {
                                    // send message only for sms pin
                                    final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
                                        .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                                    // for Zebra and Tango by Sanjeew date
                                    // 11/07/07
                                    if (ptupsMobqutyMergd) {
                                        if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                            arrArray[2] = paymentNumber;
                                        }
                                    }
                                    // End Zebra and Tango
                                    subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });
                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                                }
                            }
                        }

                        else if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && loginPasswordAllowed) {
                            // send message for login id
                            final String[] arrArray = { channelUserVO.getLoginID(), "" };

                            // for Zebra and Tango by Sanjeew date 11/07/07
                            if (ptupsMobqutyMergd) {
                                if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                    arrArray[1] = paymentNumber;
                                }
                            }
                            // End Zebra and Tango
                            // Email for pin & password
                            subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });

                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
                        } else if (TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                            // send message for sms pin
                            final String[] arrArray = { channelUserVO.getMsisdn(), "" };

                            // for Zebra and Tango by Sanjeew date 11/07/07
                            if (ptupsMobqutyMergd) {
                                if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                    arrArray[1] = paymentNumber;
                                }
                            }
                            // End Zebra and Tango
                            // Email for pin & Password
                            subject = p_messages.getMessage(p_locale, "subject.user.regmsidn.massage", new String[] { channelUserVO.getMsisdn() });

                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                        }

                        pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", defaultLocale, p_userVO.getNetworkID());
                        pushMessage.push();
                        // Email for pin & password- email change
                        boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
                        if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                            emailSendToUser = new EmailSendToUser(subject, btslPushMessage, p_locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
                                channelUserVO, p_userVO);
                            emailSendToUser.sendMail();
                        }
                    }

                    BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Success");
                    ChannelUserLog.log("BULKUSRAPP", channelUserVO, p_userVO, true, "Success Batch user approval");
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
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "",
                        "", "SQL Exception:" + sqe.getMessage());
                    errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "error.general.sql.processing"));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + p_messages.getMessage("error.general.sql.processing"));
                    continue;
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
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "",
                        "", "Exception:" + ex.getMessage());
                    errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "error.general.processing"));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + p_messages.getMessage("error.general.processing"));
                    continue;
                }

            }
            errorVO = new ListValueVO("UPDATECOUNT", String.valueOf(warnings), String.valueOf(updateCount));
            errorList.add(errorVO);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            // close result set
            try {
                if (rsSelectParentUsrStatus != null) {
                    rsSelectParentUsrStatus.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList size =" + errorList.size() + " updateCount" + updateCount);
            }
        }
        return errorList;
    }

    /**
     * Method :updateBatchesForApproval
     * 
     * @param p_con
     *            java.sql.Connection
     * @throws BTSLBaseException
     * @return int updateCount
     * @author Ved Prakash
     */
    public int updateBatchesForApproval(Connection p_con, BatchesVO p_batchesVO) throws BTSLBaseException {
        final String methodName = "updateBatchesForApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_batchesVO=" + p_batchesVO.toString());
        }
        int updateCount = 0;
        
        final StringBuffer strBuff = new StringBuffer("UPDATE batches SET status=?, modified_by=?, modified_on=?,approved_records = ?, rejected_records =? WHERE batch_id=?");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY strBuff=" + strBuff);
        }
        try (PreparedStatement pstmtUpdate = p_con.prepareStatement(strBuff.toString());){
            
            pstmtUpdate.setString(1, p_batchesVO.getStatus());
            pstmtUpdate.setString(2, p_batchesVO.getModifiedBy());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_batchesVO.getModifiedOn()));
            pstmtUpdate.setInt(4, p_batchesVO.getActiveRecords());
            pstmtUpdate.setInt(5, p_batchesVO.getRejectRecords());
            pstmtUpdate.setString(6, p_batchesVO.getBatchID());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateBatchesForApproval]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateBatchesForApproval]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount =" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method generateBatchId
     * This method is used to generate batch id
     * 
     * @param p_prefix
     *            String
     * @param counter
     *            long
     * @return id String
     * @throws Exception
     */

    /*
     * public String generateBatchId(String p_networkCode,String p_prefix,long
     * counter) throws Exception
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("generateBatchId" ,
     * "Entered  p_networkCode="+p_networkCode+" p_prefix="
     * +p_prefix+" counter="+counter);
     * String id=null;
     * try
     * {
     * int length =
     * Integer.parseInt(Constants.getProperty("BATCH_PADDING_LENGTH"));
     * id = generateIdFormat(p_networkCode,p_prefix,counter,length);
     * }
     * catch (Exception e)
     * {
     * _log.debug("generateBatchId" , "Exception id="+e);
     * e.printStackTrace();
     * }
     * finally
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("generateBatchId" , "Exiting id="+id);
     * }
     * return id;
     * }
     */
    /**
     * @param p_prefix
     * @param counter
     * @param length
     * @return
     * @throws Exception
     */
    /*
     * public String generateIdFormat(String p_networkCode,String p_prefix,long
     * counter,int length) throws Exception
     * {
     * if (_log.isDebugEnabled()) {
     * _log.debug("generateIdFormat" ,
     * "Entered  p_networkCode="+p_networkCode+" p_prefix="
     * +p_prefix+" counter="+counter+" length="+length);
     * }
     * 
     * String id = BTSLUtil.padZeroesToLeft(counter+"",length);
     * java.util.Calendar currentDate=GregorianCalendar.getInstance();
     * String month="";
     * String dateStr="";
     * if((currentDate.get(Calendar.MONTH)+1)<=9)
     * month="0"+(currentDate.get(Calendar.MONTH)+1);
     * else
     * month=""+(currentDate.get(Calendar.MONTH)+1);
     * int date=currentDate.get(Calendar.DATE);
     * if(date<=9)
     * dateStr="0"+date;
     * else
     * dateStr=""+date;
     * id =
     * p_networkCode+p_prefix+BTSLUtil.getFinancialYearLastDigits(2)+month+dateStr
     * +"."+id;
     * if (_log.isDebugEnabled()) {
     * _log.debug("generateIdFormat" , "Exiting id="+id);
     * }
     * return id;
     * }
     */

    /**
     * Method :loadBatchListForEnquiry
     * This method load list of batches on the basis of batchID.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            java.lang.String
     * @return BatchesVO
     * @throws BTSLBaseException
     * @author Amit singh
     */
    public BatchesVO loadBatchListForEnquiry(Connection p_con, String p_networkCode, String p_batchID) throws BTSLBaseException {
        final String methodName = "loadBatchListForEnquiry";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkCode=" + p_networkCode + " p_batchID=" + p_batchID);
        }
        
       
        final String sqlSelect = batchUserQry.loadBatchListForEnquiryQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        BatchesVO batchesVO = null;
        try(PreparedStatement  pstmtSelect = p_con.prepareStatement(sqlSelect);) {
          
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_NEW);
            if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
                pstmtSelect.setString(i++, PretupsI.USER_STATUS_PREACTIVE);
            } else {
                pstmtSelect.setString(i++, PretupsI.USER_STATUS_ACTIVE);
            }
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(i++, p_batchID);
            pstmtSelect.setString(i++, PretupsI.BATCH_USR_CREATION_TYPE);
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, PretupsI.BATCH_STATUS_LOOKUP);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (rs.next()) {
                batchesVO = new BatchesVO();
                batchesVO.setBatchID(rs.getString("batch_id"));
                batchesVO.setStatus(rs.getString("status"));
                batchesVO.setStatusDesc(rs.getString("lookup_name"));
                batchesVO.setBatchName(rs.getString("batch_name"));
                batchesVO.setFileName(rs.getString("file_name"));
                batchesVO.setBatchSize(rs.getLong("batch_size"));
                batchesVO.setBatchType(rs.getString("batch_type"));
                batchesVO.setCreatedBy(rs.getString("created_by"));
                batchesVO.setCreatedOn(rs.getTimestamp("created_on"));
                if (batchesVO.getCreatedOn() != null) {
                    batchesVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchesVO.getCreatedOn())));
                }
                batchesVO.setModifiedOn(rs.getTimestamp("modified_on"));
                if (batchesVO.getModifiedOn() != null) {
                    batchesVO.setModifiedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchesVO.getModifiedOn())));
                }
                batchesVO.setActiveRecords(rs.getInt("active"));
                batchesVO.setNewRecords(rs.getInt("new"));
                batchesVO.setRejectRecords(rs.getInt("reject"));
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchListForEnquiry]", "", "",
                "NetworkCode=" + p_networkCode, "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchListForEnquiry]", "", "",
                "NetworkCode=" + p_networkCode, "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting batchesVO: " + batchesVO);
            }
        }
        return batchesVO;
    }

    /**
     * Method :loadBatchListForEnquiry
     * This method load list of batches on the basis of geographyCode,
     * domainCode, fromDate and toDate
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            java.lang.String
     * @param p_geographyCode
     *            java.lang.String
     * @param p_domainCode
     *            java.lang.String
     * @param p_fromDate
     *            java.lang.String
     * @param p_toDate
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Amit singh
     */
    public ArrayList<BatchesVO> loadBatchListForEnquiry(Connection p_con, String p_networkCode, String p_geographyCode, String p_domainCode, String p_fromDate, String p_toDate, String p_categoryType, String p_userType) throws BTSLBaseException {
        final String methodName = "loadBatchListForApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered: p_geographyCode=" + p_geographyCode + " p_networkCode=" + p_networkCode + " p_domainCode=" + p_domainCode + "p_fromDate=" + p_fromDate + "p_toDate=" + p_toDate + "p_categoryType=" + p_categoryType + "p_userType=" + p_userType);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        final String geographyCode = p_geographyCode.replaceAll("'", "");
        final String gg = geographyCode.replaceAll("\" ", "");
        final String m_geographyCode[] = gg.split(",");
       
        
        final String sqlSelect = batchUserQry.loadBatchListForEnquiryQry(m_geographyCode, p_userType);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList batchList = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_NEW);
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_ACTIVE);
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(i++, PretupsI.BATCH_USR_CREATION_TYPE);
            for (int x = 0; x < m_geographyCode.length; x++) {
                pstmtSelect.setString(i++, m_geographyCode[x]);
            }
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getDateFromDateString(p_fromDate)));
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getDateFromDateString(p_toDate)));
            pstmtSelect.setString(i++, p_domainCode);
            pstmtSelect.setString(i++, PretupsI.BATCH_STATUS_LOOKUP);
            if (p_userType.equals(PretupsI.CHANNEL_USER_TYPE)) {
                pstmtSelect.setString(i++, p_categoryType);
            }
            rs = pstmtSelect.executeQuery();
            BatchesVO bacthesVO = null;
            while (rs.next()) {
                bacthesVO = new BatchesVO();
                bacthesVO.setBatchID(rs.getString("batch_id"));
                bacthesVO.setStatus(rs.getString("status"));
                bacthesVO.setStatusDesc(rs.getString("lookup_name"));
                bacthesVO.setBatchName(rs.getString("batch_name"));
                bacthesVO.setFileName(rs.getString("file_name"));
                bacthesVO.setBatchSize(rs.getLong("batch_size"));
                bacthesVO.setBatchType(rs.getString("batch_type"));
                bacthesVO.setCreatedBy(rs.getString("created_by"));
                bacthesVO.setCreatedOn(rs.getTimestamp("created_on"));
                if (bacthesVO.getCreatedOn() != null) {
                    bacthesVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(bacthesVO.getCreatedOn())));
                }
                bacthesVO.setModifiedBy(rs.getString("modified_by"));
                bacthesVO.setModifiedOn(rs.getTimestamp("modified_on"));
                if (bacthesVO.getModifiedOn() != null) {
                    bacthesVO.setModifiedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(bacthesVO.getModifiedOn())));
                }
                bacthesVO.setActiveRecords(rs.getInt("active"));
                bacthesVO.setNewRecords(rs.getInt("new"));
                bacthesVO.setRejectRecords(rs.getInt("reject"));

                batchList.add(bacthesVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchListForApproval]", "", "",
                "NetworkCode=" + p_networkCode, "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchListForApproval]", "", "",
                "NetworkCode=" + p_networkCode, "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: batchList size =" + batchList.size());
            }
        }
        return batchList;
    }

    /**
     * Method :loadBatchDetailsListForEnq
     * This method load user details of the batch for enquiry.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_batchID
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadBatchDetailsListForEnq(Connection p_con, String p_batchID) throws BTSLBaseException {
        final String methodName = "loadBatchDetailsListForEnq";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_batchID=" + p_batchID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PreparedStatement pstmtSelectForSrevice = null;
        PreparedStatement pstmtSelectForRoles = null;
        ResultSet rsForService = null;
        ResultSet rsForRoles = null;

        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        Set<String> uniqueTransProfileId = new HashSet();
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        String sqlSelect = null;
        
        if(tcpOn) {

        	sqlSelect = batchUserQry.loadBatchDetailsListForEnqQry();
        }else {
        sqlSelect = batchUserQry.loadBatchDetailsListForEnqQry();
        }
        // Modification for Service Management [by Vipul]
        final String selectServices = batchUserQry.loadBatchDetailsListForEnqSelectServicesQry();
    
        final String selectRoles = batchUserQry.loadBatchDetailsListForEnqSelectRolesQry();

        final ArrayList batchList = new ArrayList();
        
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelectForSrevice = p_con.prepareStatement(selectServices.toString());
            pstmtSelectForRoles = p_con.prepareStatement(selectRoles.toString());

            int i = 1;
            pstmtSelect.setString(i++, p_batchID);
            pstmtSelect.setString(i++, PretupsI.OUTLET_TYPE);
            pstmtSelect.setString(i++, PretupsI.OUTLET_TYPE);
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_TYPE);
            rs = pstmtSelect.executeQuery();
            ChannelUserVO channelUserVO = null;
            CategoryVO categoryVO = null;
            int j = 0;
            String service = null;
            String roles = null;
            while (rs.next()) {
                channelUserVO = new ChannelUserVO();
                channelUserVO.setRecordNumber("" + (j++));
                channelUserVO.setBatchID(rs.getString("batch_id"));
                channelUserVO.setParentStatus(rs.getString("parent_status"));
                channelUserVO.setParentLoginID(rs.getString("parent_login_id"));
                channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setLastLoginOn(rs.getTimestamp("last_login_on"));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("status"));
                channelUserVO.setStatusDesc(rs.getString("StatusDesc"));
                channelUserVO.setEmail(rs.getString("email"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setUserType(rs.getString("user_type"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setAppointmentDate(rs.getDate("appointment_date"));
                channelUserVO.setPreviousStatus(rs.getString("previous_status"));
                channelUserVO.setCreationType(rs.getString("creation_type"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                
                if(tcpOn) {
                	uniqueTransProfileId.add(rs.getString("transfer_profile_id"));
                }
                
                if(!tcpOn) {
                channelUserVO.setTransferProfileName(rs.getString("profile_name"));
                }
                
                channelUserVO.setSubOutletCode(rs.getString("sub_lookup_name"));
                channelUserVO.setOutletCode(rs.getString("OutletName"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
                channelUserVO.setCategoryName(rs.getString("category_name"));
                channelUserVO.setRemarks(rs.getString("user_remarks"));
                // added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                // end
                categoryVO = new CategoryVO();
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                channelUserVO.setCategoryVO(categoryVO);

                // for Zebra and Tango by Sanjeew date 09/07/07
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
                channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                // End Zebra and Tango

                // added for rsa authentication by shashank
                channelUserVO.setRsaFlag(rs.getString("rsaflag"));
                channelUserVO.setAuthTypeAllowed(rs.getString("authentication_allowed"));
                // end
                if (PretupsI.NO.equals(categoryVO.getFixedRoles())) {
                    // load roles from user roles
                    pstmtSelectForRoles.clearParameters();
                    pstmtSelectForRoles.setString(1, channelUserVO.getUserID());
                    rsForRoles = pstmtSelectForRoles.executeQuery();
                    roles = "";
                    while (rsForRoles.next()) {
                        roles = roles + rsForRoles.getString("role_name") + ",";
                    }
                    if (roles.length() > 1) {
                        roles = roles.substring(0, roles.length() - 1);
                    }
                    if (rsForRoles != null) {
                        rsForRoles.close();
                    }
                    channelUserVO.setGroupRoleCode(roles);
                }
                if (PretupsI.YES.equals(categoryVO.getServiceAllowed())) {
                    // load services from user services
                    pstmtSelectForSrevice.clearParameters();
                    pstmtSelectForSrevice.setString(1, channelUserVO.getUserID());
                    rsForService = pstmtSelectForSrevice.executeQuery();
                    service = "";
                    while (rsForService.next()) {
                        service = service + rsForService.getString("name") + ",";
                    }
                    if (service.length() > 1) {
                        service = service.substring(0, service.length() - 1);
                    }
                    if (rsForService != null) {
                        rsForService.close();
                    }
                    channelUserVO.setServiceTypes(service);
                }
                batchList.add(channelUserVO);
            }
            
            
            if (tcpOn) {
				SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, uniqueTransProfileId,
						ValueType.STRING);
				return BTSLUtil.updateMapViaMicroServiceResultSet(batchList, BTSLUtil
						.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","profile_Name","status")), searchCriteria));
			}
            
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchDetailsListForEnq]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchDetailsListForEnq]", "", "", "",
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsForService != null) {
                    rsForService.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectForSrevice != null) {
                    pstmtSelectForSrevice.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsForRoles != null) {
                    rsForRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectForRoles != null) {
                    pstmtSelectForRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: batchList size =" + batchList.size());
            }
        }
        return batchList;
    }
    /**
     * Method :loadBatchUserListForModify
     * This method load user details of the batch for Modification.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_geographyCode
     *            java.lang.String
     * @param p_category_code
     *            java.lang.String
     * @param p_user_id
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author sanjeew.kumar
     */
    public ArrayList loadBatchUserListForModify(Connection p_con, String p_geographyCode, String p_category_code, String p_user_id) throws BTSLBaseException {
        final String methodName = "loadBatchUserListForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_geographyCode=" + p_geographyCode + " p_user_id=" + p_user_id);
        }
        
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
         

        PreparedStatement pstmtGeoDomain = null;
        ResultSet rsGeoDomain = null;
        String geoDomain = null;
        final StringBuffer strBuffGeodomain = new StringBuffer("SELECT grph_domain_code FROM user_geographies WHERE user_id=?");
        final String sqlSelectGeodomain = strBuffGeodomain.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectGeodomain=" + sqlSelectGeodomain);
        }

        PreparedStatement pstmtRoles = null;
        ResultSet rsRoles = null;
        ResultSet rsRoles1 = null;
        String roles = null;
        final StringBuffer strBuffRoles = new StringBuffer("SELECT UR.role_code FROM user_roles UR, roles R ");
        strBuffRoles.append("WHERE UR.role_code=R.role_code AND R.status=? AND UR.user_id=? AND R.domain_type=? AND R.group_role=? ");
        final String sqlSelectRoles = strBuffRoles.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectRoles=" + sqlSelectRoles);
        }

        PreparedStatement pstmtServices = null;
        ResultSet rsServices = null;
        String services = null;
        // Modification for Service Management [by Vipul]
        final StringBuffer strBuffServices = new StringBuffer("SELECT US.service_type");
        strBuffServices.append(" FROM user_services US,users U,category_service_type CST");
        strBuffServices
            .append(" WHERE US.user_id=? AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type and CST.network_code=U.network_code");

        final String sqlSelectServices = strBuffServices.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectServices=" + sqlSelectServices);
        }
        
        //Voucher Type
        PreparedStatement pstmtVouchers = null;
        ResultSet rsVouchers = null;
        String vouchers = null;
        StringBuffer strBuffVouchers= new StringBuffer(" SELECT uv.voucher_type, vt.name ");
        strBuffVouchers.append(" FROM user_vouchertypes uv, voms_types vt, users u ");
        strBuffVouchers.append(" WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id ");
        final String sqlSelectVouchers = strBuffVouchers.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectVouchers=" + sqlSelectVouchers);
        }
        
        PreparedStatement pstmtSegments = null;
        ResultSet rsSegments = null;
        String segments = null;
        StringBuffer strBuffSegments= new StringBuffer(" SELECT us.VOUCHER_SEGMENT, lu.LOOKUP_NAME ");
        strBuffSegments.append(" FROM USER_VOUCHER_SEGMENTS us, LOOKUPS lu, users u ");
        strBuffSegments.append(" WHERE us.user_id = ? AND us.VOUCHER_SEGMENT = lu.LOOKUP_CODE  AND u.user_id = us.user_id ");
        final String sqlSelectSegments = strBuffSegments.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectSegments=" + sqlSelectSegments);
        }
        
        // Mutiple msisdn change start
        ArrayList msisdnList = null;
        PreparedStatement pstmtMsisdn = null;
        ResultSet rsMsisdn = null;
        final StringBuffer strBuffMsisdn = new StringBuffer("SELECT user_phones_id,msisdn,primary_number,sms_pin,phone_language,country ");// phone_language,country
        // added
        // by
        // deepika
        // aggarwal
        strBuffMsisdn.append("FROM user_phones WHERE user_id=? ORDER BY primary_number DESC ");
        final String sqlSelectMsisdn = strBuffMsisdn.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectMsisdn=" + sqlSelectMsisdn);
            // Mutiple msisdn change end
        }

        final ArrayList batchList = new ArrayList();
        try {
           
            pstmtGeoDomain = p_con.prepareStatement(sqlSelectGeodomain);
            pstmtRoles = p_con.prepareStatement(sqlSelectRoles);
            pstmtServices = p_con.prepareStatement(sqlSelectServices);
            pstmtVouchers = p_con.prepareStatement(sqlSelectVouchers);
            pstmtSegments = p_con.prepareStatement(sqlSelectSegments);
            pstmtMsisdn = p_con.prepareStatement(sqlSelectMsisdn);
            int i = 1;
            pstmtSelect = batchUserQry.loadBatchUserListForModifyQry(p_con, p_category_code, p_geographyCode, p_user_id);
            rs = pstmtSelect.executeQuery();

            ChannelUserVO channelUserVO = null;
            UserPhoneVO userPhoneVO = null;
            String password = null;
            while (rs.next()) {
                channelUserVO = new ChannelUserVO();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setCategoryName(rs.getString("category_name"));
                password = rs.getString("password");
                if (!BTSLUtil.isNullString(password)) {
                    channelUserVO.setPassword(BTSLUtil.decryptText(password));
                } else {
                    channelUserVO.setPassword("");
                }

                userPhoneVO = new UserPhoneVO();
                userPhoneVO.setMsisdn(rs.getString("msisdn"));
                userPhoneVO.setSmsPin(BTSLUtil.decryptText(rs.getString("sms_pin")));
                channelUserVO.setUserPhoneVO(userPhoneVO);

                channelUserVO.setDomainTypeCode(rs.getString("domain_type_code"));

                i = 1;
                pstmtGeoDomain.clearParameters();
                pstmtGeoDomain.setString(i++, rs.getString("user_id"));
                rsGeoDomain = pstmtGeoDomain.executeQuery();
                geoDomain = "";
                while (rsGeoDomain.next()) {
                    geoDomain = geoDomain + ", " + rsGeoDomain.getString("grph_domain_code");
                }
                if (!BTSLUtil.isNullString(geoDomain)) {
                    geoDomain = geoDomain.substring(1);
                }
                channelUserVO.setGeographicalCode(geoDomain);

                i = 1;
                pstmtRoles.clearParameters();
                pstmtRoles.setString(i++, TypesI.YES);
                pstmtRoles.setString(i++, rs.getString("user_id"));
                pstmtRoles.setString(i++, rs.getString("domain_type_code"));
                pstmtRoles.setString(i++, PretupsI.YES);
                rsRoles = pstmtRoles.executeQuery();
                if (rsRoles.next()) {
                    channelUserVO.setGroupRoleCode(rsRoles.getString("role_code"));
                    channelUserVO.setGroupRoleFlag(PretupsI.YES);
                } else {
                    i = 1;
                    pstmtRoles.clearParameters();
                    pstmtRoles.setString(i++, TypesI.YES);
                    pstmtRoles.setString(i++, rs.getString("user_id"));
                    pstmtRoles.setString(i++, rs.getString("domain_type_code"));
                    pstmtRoles.setString(i++, PretupsI.NO);
                    rsRoles1 = pstmtRoles.executeQuery();
                    roles = "";
                    while (rsRoles1.next()) {
                        roles = roles + ", " + rsRoles1.getString("role_code");
                    }
                    if (!BTSLUtil.isNullString(roles)) {
                        roles = roles.substring(1);
                    }
                    channelUserVO.setGroupRoleCode(roles);
                    channelUserVO.setGroupRoleFlag(PretupsI.NO);
                }

                i = 1;
                pstmtServices.clearParameters();
                pstmtServices.setString(i++, rs.getString("user_id"));
                rsServices = pstmtServices.executeQuery();
                services = "";
                while (rsServices.next()) {
                    services = services + ", " + rsServices.getString("service_type");
                }
                if (!BTSLUtil.isNullString(services)) {
                    services = services.substring(1);
                }
                channelUserVO.setServiceTypes(services);

                //Payment Type
                i = 1;
                pstmtVouchers.clearParameters();
                pstmtVouchers.setString(i++, rs.getString("user_id"));
                rsVouchers = pstmtVouchers.executeQuery();
                vouchers = "";
                while (rsVouchers.next()) {
                    vouchers = vouchers + ", " + rsVouchers.getString("voucher_type");
                }
                if (!BTSLUtil.isNullString(vouchers)) {
                    vouchers = vouchers.substring(1);
                }
                channelUserVO.setVoucherTypes(vouchers);
                
                i = 1;
                pstmtSegments.clearParameters();
                pstmtSegments.setString(i++, rs.getString("user_id"));
                rsSegments = pstmtSegments.executeQuery();
                segments = "";
                while (rsSegments.next()) {
                	segments = segments + ", " + rsSegments.getString("voucher_segment");
                }
                if (!BTSLUtil.isNullString(segments)) {
                	segments = segments.substring(1);
                }
                channelUserVO.setSegments(segments);
                //Payment Type end
                
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setEmail(rs.getString("email"));
                // added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                
                
                // end
                channelUserVO.setOutletCode(rs.getString("outlet_code"));
                channelUserVO.setSubOutletCode(rs.getString("suboutlet_code"));
                channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                
                // added for trf and rsa by shashank
                channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                channelUserVO.setRsaFlag(rs.getString("rsaflag"));
                // ends
                // Added for OTP
                channelUserVO.setAuthTypeAllowed(rs.getString("authentication_allowed"));
				if (SystemPreferences.USERWISE_LOAN_ENABLE) 
                    channelUserVO.setLoanProfileId(rs.getString("profile_id"));
                // Added by Amit Raheja
                channelUserVO.setAlertEmail(rs.getString("alert_email"));
                channelUserVO.setAlertType(rs.getString("alert_type"));
                channelUserVO.setAlertMsisdn(rs.getString("alert_msisdn"));
                // Addition ends

                // Multiple msisdn change start
                pstmtMsisdn.clearParameters();
                pstmtMsisdn.setString(1, rs.getString("user_id"));
                rsMsisdn = pstmtMsisdn.executeQuery();
                msisdnList = new ArrayList();
                String langcountry = null;// added by deepika aggarwal
                while (rsMsisdn.next()) {
                    userPhoneVO = new UserPhoneVO();
                    userPhoneVO.setMsisdn(rsMsisdn.getString("msisdn"));
                    userPhoneVO.setSmsPin(BTSLUtil.decryptText(rsMsisdn.getString("sms_pin")));
                    userPhoneVO.setUserPhonesId(rsMsisdn.getString("user_phones_id"));
                    userPhoneVO.setCountry(rsMsisdn.getString("country"));// added
                    langcountry = rsMsisdn.getString("phone_language") + "_" + rsMsisdn.getString("country");// added
                    userPhoneVO.setPhoneLanguage(rsMsisdn.getString("phone_language")); // added
                    msisdnList.add(userPhoneVO);
                    userPhoneVO = null;
                }
                channelUserVO.setLanguage(langcountry); 
                channelUserVO.setMsisdnList(msisdnList);
                channelUserVO.setLongitude(rs.getString("longitude"));
                channelUserVO.setLatitude(rs.getString("latitude"));
                channelUserVO.setDocumentType(rs.getString("document_type"));
                channelUserVO.setDocumentNo(rs.getString("document_no"));
                channelUserVO.setPaymentType(rs.getString("payment_type"));
                channelUserVO.setCommissionProfileSetID(rs.getString("COMM_PROFILE_SET_ID"));
                channelUserVO.setTransferProfileID(rs.getString("TRANSFER_PROFILE_ID"));
                channelUserVO.setUserGrade(rs.getString("USER_GRADE"));
                
                
                boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
                if (lmsAppl) {
                    channelUserVO.setLmsProfile(rs.getString("lms_profile"));
                }
                batchList.add(channelUserVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchUserListForModify]", "", "", "",
                "GeographyCode=" + p_geographyCode + "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchUserListForModify]", "", "", "",
                "GeographyCode=" + p_geographyCode + "SQL Exception:" + ex.getMessage());
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
                if (rsRoles != null) {
                	rsRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsRoles1 != null) {
                	rsRoles1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsVouchers != null) {
                	rsVouchers.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSegments != null) {
                	rsSegments.close();
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
            try {
                if (pstmtVouchers != null) {
                	pstmtVouchers.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSegments != null) {
                	pstmtSegments.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsGeoDomain != null) {
                    rsGeoDomain.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtGeoDomain != null) {
                    pstmtGeoDomain.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsRoles != null) {
                    rsRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtRoles != null) {
                    pstmtRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsServices != null) {
                    rsServices.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtServices != null) {
                    pstmtServices.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsMsisdn != null) {
                    rsMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtMsisdn != null) {
                    pstmtMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: batchList size =" + batchList.size());
            }
        }
        return batchList;
    }
    /**
     * Method :loadMasterGroupRoleCodeList
     * This method load Role code of the batch user for Modification.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_geographyCode
     *            java.lang.String
     * @param p_category_code
     *            java.lang.String
     * @param p_domainType
     *            java.lang.String
     * @param p_groupRoleAllowed
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author sanjeew.kumar
     */

    public ArrayList loadMasterGroupRoleCodeList(Connection p_con, String p_domainType, String p_category_code, String p_groupRoleAllowed) throws BTSLBaseException {
        final String methodName = "loadMasterGroupRoleCodeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_domainType=" + p_domainType + ", p_category_code=" + p_category_code + ", p_groupRoleAllowed=" + p_groupRoleAllowed);
        }
         
        ResultSet rs = null;
        final StringBuffer strBuffRoles = new StringBuffer("SELECT CR.role_code, R.role_name FROM roles R, category_roles CR ");
        strBuffRoles.append(" WHERE CR.role_code= R.role_code AND R.status=? AND cr.category_code=? AND R.domain_type=? AND R.group_role=? ");
        final String sqlSelectRoles = strBuffRoles.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectRoles=" + sqlSelectRoles);
        }
        final ArrayList roleList = new ArrayList();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelectRoles);){
           
            int i = 1;
            pstmtSelect.setString(i++, TypesI.YES);
            pstmtSelect.setString(i++, p_category_code);
            pstmtSelect.setString(i++, p_domainType);
            pstmtSelect.setString(i++, p_groupRoleAllowed);
            rs = pstmtSelect.executeQuery();

            UserRolesVO rolesVO = null;
            while (rs.next()) {
                rolesVO = new UserRolesVO();
                rolesVO.setRoleCode(rs.getString("role_code"));
                rolesVO.setRoleName(rs.getString("role_name"));
		rolesVO.setCategoryCode(p_category_code);
                roleList.add(rolesVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterGroupRoleList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadMasterGroupRoleList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterGroupRoleList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadMasterGroupRoleList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: roleList size =" + roleList.size());
            }
        }
        return roleList;
    }

    /**
     * Method :loadMasterGeographyForCategoryList
     * this method load list of Geographics on the basis of geographical code,
     * category code and login user id.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_geographicsCode
     *            java.lang.String
     * @param p_loginUserID
     *            java.lang.String
     * @param p_category
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author sanjeew.kumar
     */
    public ArrayList loadMasterGeographyForCategoryList(Connection p_con, String p_geographicsCode, String p_loginUserID, String p_category) throws BTSLBaseException {
        final String methodName = "loadMasterGeographyForCategoryList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_geographicsCode=" + p_geographicsCode + " p_loginUserID=" + p_loginUserID + " p_category=" + p_category);
        }
        PreparedStatement pstmtSelect = null;
        
    
        final ArrayList geographicesList = new ArrayList();
        try {
        	pstmtSelect = batchUserQry.loadMasterGeographyForCategoryListQry(p_con, p_category, p_geographicsCode, p_loginUserID);
          try(ResultSet  rs = pstmtSelect.executeQuery();)
          {
            UserGeographiesVO userGeographiesVO = null;
            while (rs.next()) {
                userGeographiesVO = new UserGeographiesVO();
                userGeographiesVO.setGraphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("geography_code")));
                userGeographiesVO.setGraphDomainName(SqlParameterEncoder.encodeParams(rs.getString("geography_name")));
                userGeographiesVO.setGraphDomainType(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type")));
                userGeographiesVO.setGraphDomainTypeName(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type_name")));
                userGeographiesVO.setGraphDomainSequenceNumber(rs.getInt("sequence_no"));
                geographicesList.add(userGeographiesVO);
            }
          }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterGeographyList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterGeographyList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: geographicesList size =" + geographicesList.size());
            }
        }
        return geographicesList;
    }

    /**
     * Method :loadLanguageList
     * this method load list of all available language options.
     * 
     * @param p_con
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author deepika.aggarwal
     */
    public HashMap loadLanguageList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadLanguageList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
       
        final StringBuffer strBuff = new StringBuffer(" SELECT LM.language,LM.country,LM.NAME FROM locale_master LM ");
        strBuff.append(" WHERE STATUS='Y' ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final HashMap map = new HashMap();
        try ( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);ResultSet rs = pstmtSelect.executeQuery();){
            
            
            while (rs.next()) {
                map.put(rs.getString("language") + "_" + rs.getString("country"), rs.getString("name"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadLanguageList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadLanguageList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: roleList size =" + map.size());
            }
        }
        return map;
    }

    /**
     * @param p_batchId
     * @param p_categoryCode
     * @param p_con
     * @return
     */

    public ArrayList<String> loadGeographyAndDomainDetails(Connection p_con, String p_categoryCode, String p_batchId) throws BTSLBaseException {
        final String methodName = "loadGeographyAndDomainDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered", "p_categoryCode = " + p_categoryCode + "p_batchId = " + p_batchId);
        }

        final ArrayList<String> batchInfo = new ArrayList<String>();
       
        ResultSet rsGeography = null;
         
        PreparedStatement pstmtSelectGeography = null;

        StringBuffer strbuff = new StringBuffer(" select c.DOMAIN_CODE, d.DOMAIN_NAME ");
        strbuff.append(" from categories c, domains d ");
        strbuff.append(" where c.CATEGORY_CODE=? and d.DOMAIN_CODE = c.DOMAIN_CODE ");
        final String sqlSelectDomain = strbuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadGeographyAndDomainDetails ", " SQL Query " + sqlSelectDomain.toString());
        }

        final String sqlSelectGeography = batchUserQry.loadGeographyAndDomainDetailsQry();

        try (PreparedStatement pstmtSelectDomain = p_con.prepareStatement(sqlSelectDomain);){
            
            pstmtSelectDomain.setString(1, p_categoryCode);
            try( ResultSet  rsDomain = pstmtSelectDomain.executeQuery();)
            {
            if (rsDomain.next()) {
                batchInfo.add(rsDomain.getString("domain_code"));
                batchInfo.add(rsDomain.getString("domain_name"));
            }

            pstmtSelectGeography = p_con.prepareStatement(sqlSelectGeography);
            pstmtSelectGeography.setString(1, p_batchId);
            rsGeography = pstmtSelectGeography.executeQuery();
            if (rsGeography.next()) {
                batchInfo.add(rsGeography.getString("GRPH_DOMAIN_CODE"));
            }
        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadGeographyAndDomainDetails]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception : " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadGeographyAndDomainDetails]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rsGeography != null) {
                    rsGeography.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectGeography != null) {
                    pstmtSelectGeography.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadMasterGeographyForCategoryList", "Exiting: batchInfo size =" + batchInfo.size());
            }
        }
        return batchInfo;
    }

    // added by akanksha for batch grade management
    /**
     * Method :loadCatUsersDeatilsVOList
     * This method check the data base validation
     * and after validation insert into channel user related tables.
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author lalit
     */

    public ArrayList loadCatUsersDeatilsVOList(Connection p_con, String p_networkCode, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadCatUsersDeatilsVOList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered p_networkCode : " + p_networkCode + " p_categoryCode : " + p_categoryCode);
        }
        ArrayList detailsList = null;
         
        detailsList = new ArrayList();
        GradeVO gradeVO = null;
        try {
            final StringBuffer strBuff = new StringBuffer("SELECT U.user_id , U.user_name, U.owner_id , U.parent_id , U.status  FROM  USERS U, CHANNEL_USERS CU");
            strBuff.append(" WHERE category_code = ? AND network_code=? AND status=? AND U.USER_ID = CU.USER_ID  AND CU.USER_GRADE IS NOT NULL ");

            if (_log.isDebugEnabled()) {
                _log.debug("loadCatUsersDeatilsVOList ", " SQL Query " + strBuff.toString());
            }
           try(PreparedStatement pstmt = p_con.prepareStatement(strBuff.toString());)
           {
            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, PretupsI.CATEGORY_STATUS_ACTIVE);

           try(ResultSet rs = pstmt.executeQuery();)
           {
            while (rs.next()) {
                gradeVO = new GradeVO();
                gradeVO.setCategoryUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                gradeVO.setCategoryUserId(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
                gradeVO.setParentCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("parent_id")));
                gradeVO.setOwnerCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("owner_id")));
                gradeVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                detailsList.add(gradeVO);
            }

        } 
           }
        }catch (SQLException sqe) {
            _log.error("loadCatUsersDeatilsVOList ", "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadCatUsersDeatilsVOList ", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadCatUsersDeatilsVOList ", " Exiting " + detailsList.size());
        }
        return detailsList;
    }

    // added by akanksha for batch grade management
    /**
     * Method :loadOwnerGradeInfo
     * This method check the data base validation
     * and after validation insert into channel user related tables.
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author lalit
     */

    public ArrayList loadOwnerGradeInfo(Connection p_con, String p_networkCode, String p_categoryCode, ArrayList p_userVOList) throws BTSLBaseException {
        final String methodName = "loadOwnerGradeInfo";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered p_networkCode : " + p_networkCode + " p_categoryCode : " + p_categoryCode);
        }
        ArrayList newDetailsList = null;
         
        
        newDetailsList = new ArrayList();
        GradeVO gradeVO = null;
        try {
            // the query is used to fetch channel user grade & his Owner name.
            // in case we do join then query will return his own garde & name
            // not Owner name
            final StringBuffer strBuff = new StringBuffer("SELECT user_name , user_grade   FROM users UR, channel_users CU  WHERE CU.user_id = ?  ");
            strBuff.append(" AND UR.user_id = ? AND network_code=?  AND status=? ");

            if (_log.isDebugEnabled()) {
                _log.debug("loadOwnerGradeInfo ", " SQL Query " + strBuff.toString());
            }
           try(PreparedStatement pstmt = p_con.prepareStatement(strBuff.toString());)
           {
            int userVOLists=p_userVOList.size();
            for (int i = 0; i < userVOLists; i++) {
                gradeVO = (GradeVO) p_userVOList.get(i);
                pstmt.clearParameters();
                pstmt.setString(1, gradeVO.getCategoryUserId());
                pstmt.setString(2, gradeVO.getOwnerCategoryCode());
                pstmt.setString(3, p_networkCode);
                pstmt.setString(4, PretupsI.CATEGORY_STATUS_ACTIVE);
                try(ResultSet rs = pstmt.executeQuery();)
                {
                if (rs.next()) {
                    gradeVO.setOwnerCategoryName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                    gradeVO.setGradeCode(SqlParameterEncoder.encodeParams(rs.getString("user_grade")));
                    newDetailsList.add(gradeVO);
                }
            }
            }
        } 
        }catch (SQLException sqe) {
            _log.error("loadOwnerGradeInfo ", "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadOwnerGradeInfo ", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadOwnerGradeInfo ", " Exiting " + newDetailsList.size());
        }
        return newDetailsList;
    }

    /**
     * @author shashank.gaur
     *         Method for create a new batch for barred users
     *         (insert into batches)
     * 
     * @param p_con
     *            java.sql.Connection
     * @param sessionUserVO
     *            (consist of UserVO)
     * @param form
     *            BatchBarForDelForm
     * 
     * @throws BTSLBaseException
     * 
     * 
     *             public void addbatchbaruserdetails(Connection
     *             p_con,ActionForm form,UserVO userVO,UserVO sessionUserVO)
     *             throws BTSLBaseException
     *             {
     *             int i=0;
     *             String batchDetailID = null;
     *             BatchBarForDelForm theForm = null;
     *             OperatorUtilI operatorUtil=null;
     *             PreparedStatement pstmtBar = null;
     *             Date currentDate = new Date();
     *             if (_log.isDebugEnabled())
     *             _log.debug("addbatchbaruserdetails", "Entered " );
     * 
     *             String utilClass = (String)
     *             PreferenceCache.getSystemPreferenceValue
     *             (PreferenceI.OPERATOR_UTIL_CLASS);
     * 
     *             try
     *             {
     *             operatorUtil = (OperatorUtilI)
     *             Class.forName(utilClass).newInstance();
     *             }
     *             catch(Exception e)
     *             {
     *             e.printStackTrace();
     *             EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.
     *             SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,
     *             "BatchUserDAO[addbatchbaruserdetails]"
     *             ,"","","","Exception while loading the class at the call:"
     *             +e.getMessage());
     *             }
     * 
     *             try
     *             {
     *             theForm=(BatchBarForDelForm) form;
     *             batchDetailID=operatorUtil.formatBatchesID(sessionUserVO.
     *             getNetworkID
     *             (),PretupsI.BULK_USR_BAR_DETAIL_ID_PREFIX,currentDate
     *             ,IDGenerator
     *             .getNextID(PretupsI.BULK_USR_BAR_DETAIL_ID_PREFIX,
     *             BTSLUtil.getFinancialYear(),sessionUserVO.getNetworkID()));
     *             StringBuffer strBuff = new StringBuffer();
     *             strBuff.append(
     *             "INSERT into batch_items (user_id,batch_id,batch_details_id,status,category_code,"
     *             );
     *             strBuff.append("initiator_remarks) values (?,?,?,?,?,?)");
     * 
     *             pstmtBar = p_con.prepareStatement(strBuff.toString());
     *             pstmtBar.setString(++i,userVO.getUserID());
     *             pstmtBar.setString(++i,theForm.getBatchID());
     *             pstmtBar.setString(++i,batchDetailID);
     *             if(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST.equals(userVO.
     *             getStatus()))
     *             pstmtBar.setString(++i,PretupsI.
     *             USER_STATUS_BCH_BAR_FOR_DEL_REQUEST);
     *             else
     *             pstmtBar.setString(++i,PretupsI.
     *             USER_STATUS_BCH_BAR_FOR_DEL_BARRED);
     *             pstmtBar.setString(++i,userVO.getCategoryCode());
     *             pstmtBar.setString(++i,userVO.getRemarks());
     * 
     * 
     *             pstmtBar.executeQuery();
     * 
     *             if (_log.isDebugEnabled())
     *             _log.debug("addbatchbaruserdetails",
     *             " OtherQuery sqlSelect QUERY :" + strBuff.toString());
     * 
     *             } // end of try
     * 
     *             catch (SQLException sqle)
     *             {
     *             _log.error("addbatchbaruserdetails", "SQLException: " +
     *             sqle.getMessage());
     *             sqle.printStackTrace();
     *             EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.
     *             SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,
     *             "BatchUserDAO[addbatchbaruserdetails]"
     *             ,"","","","SQL Exception:"+sqle.getMessage());
     *             throw new BTSLBaseException(this, "addbatchbaruserdetails",
     *             "error.general.sql.processing");
     *             } // end of catch
     *             catch (Exception e)
     *             {
     *             _log.error("addbatchbaruserdetails", "Exception: " +
     *             e.getMessage());
     *             e.printStackTrace();
     *             EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.
     *             SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,
     *             "BatchUserDAO[addbatchbaruserdetails]"
     *             ,"","","","Exception:"+e.getMessage());
     *             throw new BTSLBaseException(this, "addbatchbaruserdetails",
     *             "error.general.processing");
     *             } // end of catch
     *             finally
     *             {
     *             try{if (pstmtBar != null){pstmtBar.close();}} catch
     *             (Exception e){}
     *             if (_log.isDebugEnabled())
     *             _log.debug("addbatchbaruserdetails", "Exiting");
     *             } // end of finally
     *             }
     */

    /**
     * Method :rejectBatchBarForDel
     * 
     * @author shashank.gaur
     * 
     *         This method reject the request of batch to be barred.
     * @param p_con
     *            Connection
     * @param p_batchid
     *            String
     * 
     * @throws BTSLBaseException
     * 
     */

    public void rejectBatchBarForDel(Connection p_con, HashMap p_userList, BatchBarForDelForm p_form, UserVO sessionUserVO) throws BTSLBaseException {
        final String methodName = "rejectBatchBarForDel";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userList=" + p_userList, "Entered: p_form=" + p_form);
        }
        String p_status = null;
        String bi_status = null;
        String user_status = null;
  
        int rs = 0;
        final StringBuffer strBuff = new StringBuffer();
        PreparedStatement pstmtUser = null;
        final StringBuffer strUser = new StringBuffer();
        final Date currentDate = new Date();
        int j = 0;

        String roles = null;
        PreparedStatement pstmtBarDel = null;
        PreparedStatement pstmtBarIns = null;
        roles = Constants.getProperty("ROLES_FOR_BAR_USERS");
        final String[] role = roles.split(",");
        final StringBuffer strBuffDel = new StringBuffer();
        final StringBuffer strBuffIns = new StringBuffer();

        PreparedStatement pstmtEvent = null;
        final StringBuffer strEvent = new StringBuffer();

        BatchesVO batchesVO = null;

        strBuff.append("UPDATE batches SET status = ?, modified_by = ?,");
        if(p_form.getActionType().equalsIgnoreCase("approve")){
        	strBuff.append(" modified_on = ?, approved_records = ? WHERE batch_id = ?");
        }else{
        	strBuff.append(" modified_on = ?, rejected_records = ?  WHERE batch_id = ?");
        }
        

        final String sqlbatch = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlbatch);
        }

        strUser.append("UPDATE users SET status = ?, previous_status = ?, modified_by = ?,");
        strUser.append(" modified_on = ?, barred_deletion_batchid = ? WHERE user_id = ?");

        final String sqlUser = strUser.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlUser);
        }
        int reqCuserBarApproval = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_BAR_APPROVAL))).intValue();
        if ((reqCuserBarApproval == 1 || "batchBarapproval2".equals(p_form.getRequestType())) && "approve".equals(p_form.getActionType())) {
            strBuffDel.append("DELETE FROM user_roles where user_id= ? ");

            strBuffIns.append("INSERT INTO user_roles (user_id,role_code,gateway_types) VALUES (?,?,?)");

            strEvent.append("INSERT INTO USER_EVENT_REMARKS (USER_ID,EVENT_TYPE,REMARKS,CREATED_BY,CREATED_ON,MODULE,MSISDN,USER_TYPE )");
            strEvent.append(" VALUES (?,?,?,?,?,?,?,?)");

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " OtherQuery sqlSelect QUERY :" + strBuffDel.toString());
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " OtherQuery sqlSelect QUERY :" + strBuffIns.toString());
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " OtherQuery sqlSelect QUERY :" + strEvent.toString());
            }

        }
        try {
            batchesVO = p_form.getBatchesVO();
            p_status = PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST;
            if ((reqCuserBarApproval == 1 || "batchBarapproval2".equals(p_form.getRequestType())) && "approve".equals(p_form.getActionType())) {
                bi_status = PretupsI.USER_STATUS_BCH_BAR_FOR_DEL_BARRED;
                user_status = PretupsI.USER_STATUS_BARRED;
            } else if ("approve".equals(p_form.getActionType())) {
                bi_status = PretupsI.USER_STATUS_BCH_BAR_FOR_DEL_APPROVE1;
                user_status = PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE;
            } else {
                bi_status = PretupsI.USER_STATUS_BCH_BAR_FOR_DEL_REJECT;
                user_status = PretupsI.USER_STATUS_ACTIVE;
            }

            HashMap user_info = new HashMap();
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlbatch);)
            {
            // pstmtSelectUser=p_con.prepareStatement(sqlbatchUser);
            pstmtUser = p_con.prepareStatement(sqlUser);

            pstmtSelect.setString(++j, p_form.getStatus());
            pstmtSelect.setString(++j, sessionUserVO.getUserID());
            pstmtSelect.setTimestamp(++j, BTSLUtil.getTimestampFromUtilDate(currentDate));
            pstmtSelect.setInt(++j, p_userList.size());
            pstmtSelect.setString(++j, batchesVO.getBatchID());
            

            rs = pstmtSelect.executeUpdate();
              int usersList=p_userList.size();
            for (int i = 1; i <=usersList ; i++) {
                j = 0;
                user_info = (HashMap) p_userList.get(i);
                j = 0;
                pstmtUser.setString(++j, user_status);
                pstmtUser.setString(++j, p_status);
                pstmtUser.setString(++j, sessionUserVO.getUserID());
                pstmtUser.setTimestamp(++j, BTSLUtil.getTimestampFromUtilDate(currentDate));
                if (((reqCuserBarApproval == 1 || "batchBarapproval2".equals(p_form.getRequestType())) || "reject".equals(p_form.getActionType())) || PretupsI.USR_BATCH_BAR_STATUS_REJECT
                    .equals(p_form.getStatus())) {
                    pstmtUser.setString(++j, "");
                } else {
                    pstmtUser.setString(++j, batchesVO.getBatchID());
                }
                pstmtUser.setString(++j, (String) user_info.get("userId"));
                pstmtUser.addBatch();
                if ((reqCuserBarApproval == 1 || "batchBarapproval2".equals(p_form.getRequestType())) && "approve".equals(p_form.getActionType())) {
                    j = 0;
                    pstmtBarDel = p_con.prepareStatement(strBuffDel.toString());
                    pstmtBarDel.setString(1, (String) user_info.get("userId"));

                    pstmtBarDel.executeUpdate();

                    pstmtBarIns = p_con.prepareStatement(strBuffIns.toString());

                    for (final String count : role) {
                        pstmtBarIns.setString(1, (String) user_info.get("userId"));
                        pstmtBarIns.setString(2, count);
                        pstmtBarIns.setString(3, Constants.getProperty("DEFAULT_GATEWAY_FOR_BAR"));
                        pstmtBarIns.addBatch();
                    }
                    pstmtBarIns.executeBatch();
                    pstmtEvent = p_con.prepareStatement(strEvent.toString());
                    pstmtEvent.setString(++j, (String) user_info.get("userId"));
                    pstmtEvent.setString(++j, PretupsI.BARRED_REQUEST_EVENT);
                    pstmtEvent.setString(++j, (String) user_info.get("remarks"));
                    pstmtEvent.setString(++j, sessionUserVO.getUserID());
                    pstmtEvent.setTimestamp(++j, BTSLUtil.getTimestampFromUtilDate(currentDate));
                    pstmtEvent.setString(++j, PretupsI.C2S_MODULE);
                    pstmtEvent.setString(++j, (String) user_info.get("msisdn"));
                    pstmtEvent.setString(++j, PretupsI.USER_TYPE_CHANNEL);
                    pstmtEvent.executeUpdate();
                }

            }

            // pstmtSelectUser.executeBatch();
            pstmtUser.executeBatch();

        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[rejectBatchBarForDel]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadBatchBarDetailList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[rejectBatchBarForDel]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUser != null) {
                    pstmtUser.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtBarDel != null) {
                	pstmtBarDel.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtBarIns != null) {
                	pstmtBarIns.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtEvent != null) {
                	pstmtEvent.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }
    /**
     * Method :loadBatchUserListForProfileAssociate
     * 	This method load user details of the batch for Profile Association.
     *   
     * @param p_con java.sql.Connection
     * @param p_geographyCode java.lang.String
     * @param p_category_code java.lang.String
     * @param p_user_id java.lang.String
     * @return java.util.ArrayList
     * @throws  BTSLBaseException
     * @author sanjeew.kumar
     */
    public ArrayList loadBatchUserListForProfileAssociate(Connection p_con,String p_geographyCode,String p_category_code, String p_user_id) throws BTSLBaseException
    {
    	final String methodName = "loadBatchUserListForProfileAssociate";
    	if (_log.isDebugEnabled())
    	    _log.debug(methodName, "Entered: p_geographyCode="+p_geographyCode+" p_user_id="+p_user_id);
    	PreparedStatement pstmtSelect = null;
    	ResultSet rs = null;
    	
    	PreparedStatement pstmtGeoDomain = null;
    	ResultSet rsGeoDomain = null;
    	String geoDomain=null;
    	StringBuffer strBuffGeodomain = new StringBuffer("SELECT grph_domain_code FROM user_geographies WHERE user_id=?");
    	String sqlSelectGeodomain = strBuffGeodomain.toString();
    	if (_log.isDebugEnabled())
    	    _log.debug(methodName, "QUERY sqlSelectGeodomain=" + sqlSelectGeodomain);
    	
    	//added for service list
    	PreparedStatement pstmtServices = null;
    	ResultSet rsServices = null;
    	String services=null;
        //Modification for Service Management [by Vipul]
    	StringBuffer strBuffServices = new StringBuffer("SELECT DISTINCT US.service_type");
    	strBuffServices.append(" FROM user_services US,users U,category_service_type CST");
    	strBuffServices.append(" WHERE US.user_id=? AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type");
    	
    	String sqlSelectServices = strBuffServices.toString();
    	if (_log.isDebugEnabled())
    	    _log.debug(methodName, "QUERY sqlSelectServices=" + sqlSelectServices);
    		
//    	Mutiple msisdn change start
    	ArrayList msisdnList=null;
    	PreparedStatement pstmtMsisdn = null;
    	ResultSet rsMsisdn = null;
    	StringBuffer strBuffMsisdn = new StringBuffer("SELECT user_phones_id,msisdn ");
    	strBuffMsisdn.append("FROM user_phones WHERE user_id=? ORDER BY primary_number DESC ");
    	String sqlSelectMsisdn = strBuffMsisdn.toString();
    	if (_log.isDebugEnabled())
    	    _log.debug(methodName, "QUERY sqlSelectMsisdn=" + sqlSelectMsisdn);
    	//Mutiple msisdn change end
    	int i;
    	ArrayList batchList =new ArrayList();
    	try
    	{
    		
			String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
			boolean tcpOn = false;
			Set<String> uniqueTransProfileId = new HashSet();

			if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
				tcpOn = true;
			}
			String sqlSelect = null;

			if (tcpOn) {
				pstmtSelect = batchUserQry.loadBatchUserListForProfileAssociateTcpQry(p_con, p_category_code,
						p_geographyCode, p_user_id);
			} else {

				pstmtSelect = batchUserQry.loadBatchUserListForProfileAssociateQry(p_con, p_category_code,
						p_geographyCode, p_user_id);
			}
    	    pstmtGeoDomain = p_con.prepareStatement(sqlSelectGeodomain);
    	    //added for service list
    	    pstmtServices = p_con.prepareStatement(sqlSelectServices);
    	    pstmtMsisdn = p_con.prepareStatement(sqlSelectMsisdn);
    	    
    		rs = pstmtSelect.executeQuery();
    		
    		ChannelUserVO channelUserVO = null;
    		UserPhoneVO userPhoneVO=null;
    		
    	    while(rs.next())
    	    {
    	        channelUserVO = new ChannelUserVO();
    	        channelUserVO.setUserID(rs.getString("user_id"));
    	        channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
    	        channelUserVO.setUserName(rs.getString("user_name"));
    	       
    	        channelUserVO.setCategoryName(rs.getString("category_name"));
    	        
    	        
    	        userPhoneVO=new UserPhoneVO();
    	        userPhoneVO.setMsisdn(rs.getString("msisdn"));
    	        
    	        channelUserVO.setUserPhoneVO(userPhoneVO);
    	        
    	        channelUserVO.setDomainTypeCode(rs.getString("domain_type_code"));
    	        
    	       //added for service list
    	        i=1;
    	        pstmtServices.clearParameters();
    	        pstmtServices.setString(i++,rs.getString("user_id"));
    	        rsServices = pstmtServices.executeQuery();
    	        services="";
    	        while(rsServices.next())
    	            services=services+", "+rsServices.getString("service_type");
    	        if(! BTSLUtil.isNullString(services))
    	            services=services.substring(1);
    	        channelUserVO.setServiceTypes(services); 
    	        
    	        i=1;
    	        pstmtGeoDomain.clearParameters();
    	        pstmtGeoDomain.setString(i++,rs.getString("user_id"));
    	        rsGeoDomain = pstmtGeoDomain.executeQuery();
    	        geoDomain="";
    	        while(rsGeoDomain.next())
    	            geoDomain=geoDomain+", "+rsGeoDomain.getString("grph_domain_code");
    	        if(! BTSLUtil.isNullString(geoDomain))
    	            geoDomain=geoDomain.substring(1);
    	        channelUserVO.setGeographicalCode(geoDomain);
    	        i=1;
    	        
    	       
    	       
    	        channelUserVO.setExternalCode(rs.getString("external_code"));
    	       
    	        channelUserVO.setFirstName(rs.getString("firstname"));
    	        channelUserVO.setLastName(rs.getString("lastname"));
    	       
    	        
    	        channelUserVO.setUserGrade(rs.getString("user_grade"));
    	        channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
    	        channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
    	        channelUserVO.setInfo1(rs.getString("info1"));
    	        channelUserVO.setInfo2(rs.getString("info2"));
    	        channelUserVO.setInfo3(rs.getString("info3"));
    	        channelUserVO.setInfo4(rs.getString("info4"));
    	        channelUserVO.setInfo5(rs.getString("info5"));
    	        channelUserVO.setInfo6(rs.getString("info6"));
    	        channelUserVO.setInfo7(rs.getString("info7"));
    	        channelUserVO.setInfo8(rs.getString("info8"));
    	        channelUserVO.setInfo9(rs.getString("info9"));
    	        channelUserVO.setInfo10(rs.getString("info10"));
    	        
    	        

    	       
//    	      Multiple msisdn change start
    	        pstmtMsisdn.clearParameters();
    	        pstmtMsisdn.setString(1,rs.getString("user_id"));
    	        rsMsisdn = pstmtMsisdn.executeQuery();
    	        msisdnList=new ArrayList();
    	        while(rsMsisdn.next())
    	        {
    		        userPhoneVO=new UserPhoneVO();
    		        userPhoneVO.setMsisdn(SqlParameterEncoder.encodeParams(rsMsisdn.getString("msisdn")));
    		        
    		        msisdnList.add(userPhoneVO);
    		        userPhoneVO=null;
    	        }
    	       
    	        channelUserVO.setMsisdnList(msisdnList);
    	        //Multiple msisdn change end
    	        
    		    
    		    batchList.add(channelUserVO);
    	    }
    	} 
    	catch (SQLException sqe)
    	{
    	    _log.error(methodName, "SQLException : " + sqe);
    	    _log.errorTrace(methodName,sqe);
    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchUserDAO[loadBatchUserListForModify]","","","","GeographyCode="+p_geographyCode+"SQL Exception:"+sqe.getMessage());
    	    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
    	} 
    	catch (Exception ex)
    	{
    	    _log.error(methodName, "Exception : " + ex);
    	    _log.errorTrace(methodName,ex);
    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchUserDAO[loadBatchUserListForModify]","","","","GeographyCode="+p_geographyCode+"SQL Exception:"+ex.getMessage());
    	    throw new BTSLBaseException(this, methodName, "error.general.processing");
    	}
    	finally
    	{
    			try {
    				if (rs != null) {
    					rs.close();
    				}
    			} catch (Exception e) {
    				_log.errorTrace(methodName, e);
    			}
    			try {
    				if (rsServices != null) {
    					rsServices.close();
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
    			try {
    				if (pstmtServices != null) {
    					pstmtServices.close();
    				}
    			} catch (Exception e) {
    				_log.errorTrace(methodName, e);
    			}
    			try {
    				if (rsGeoDomain != null) {
    					rsGeoDomain.close();
    				}
    			} catch (Exception e) {
    				_log.errorTrace(methodName, e);
    			}
    			try {
    				if (pstmtGeoDomain != null) {
    					pstmtGeoDomain.close();
    				}
    			} catch (Exception e) {
    				_log.errorTrace(methodName, e);
    			}
    			try {
    				if (rsMsisdn != null) {
    					rsMsisdn.close();
    				}
    			} catch (Exception e) {
    				_log.errorTrace(methodName, e);
    			}
    			try {
    				if (pstmtMsisdn != null) {
    					pstmtMsisdn.close();
    				}
    			} catch (Exception e) {
    				_log.errorTrace(methodName, e);
    			}
    	   
    			if (_log.isDebugEnabled())
    				_log.debug(methodName,"Exiting: batchList size =" + batchList.size());
    		}
    	return batchList;
    }
    public ArrayList loadCommProfileList(Connection p_con,String p_domainCode, String p_networkCode, String p_categoryCode,String p_userType) throws BTSLBaseException
    {
        	final String methodName = "loadCommProfileList";
    		if (_log.isDebugEnabled())
        	    _log.debug(methodName, "Entered: p_domainCode="+p_domainCode+" p_networkCode="+p_networkCode+"p_userType="+p_userType+"p_categoryCode="+p_categoryCode);
        	 
        //Changes made for Batch user initiate for channel user
    		/*if(!BTSLUtil.isNullString(p_userType)&&!p_userType.equals(PretupsI.OPERATOR_USER_TYPE))
    	    {
    			strBuff.append(" AND CAT.sequence_no>(select sequence_no from  categories where category_code=?)");
    	    }*/
    		//End of Changes made for Batch user initiate for channel user
    		//strBuff.append(" ORDER BY CAT.sequence_no  ");
        	final String sqlSelect = batchUserQry.loadCommProfileListQry();
 
        	ArrayList commProfileList =new ArrayList();
        	try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);)
        	{
        	   
    			pstmtSelect.setString(1,p_domainCode);
                pstmtSelect.setString(2,p_networkCode);
                pstmtSelect.setString(3,p_categoryCode);
    			int i=1;
        	   // pstmtSelect.setString(1,p_domainCode);
        	    //pstmtSelect.setString(2,p_networkCode);
        	    //Changes made for Batch user initiate for channel user
        	    /*if(!BTSLUtil.isNullString(p_userType)&&!p_userType.equals(PretupsI.OPERATOR_USER_TYPE))
        	    {
        	    	pstmtSelect.setString(i++,p_categoryCode);
        	    }*/
        	    //End of Changes made for Batch user initiate for channel user
        	    try(ResultSet rs = pstmtSelect.executeQuery();)
        	    {
    			CommissionProfileSetVO commissionProfileSetVO = null;
        	    while(rs.next())
        	    {
        	        commissionProfileSetVO = new CommissionProfileSetVO();
        	        commissionProfileSetVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
        	        commissionProfileSetVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
        	        commissionProfileSetVO.setGradeCode(SqlParameterEncoder.encodeParams(rs.getString("grade_code")));
                    if("ALL".equals(commissionProfileSetVO.getGradeCode())) {
                    	commissionProfileSetVO.setGradeName("ALL");
        	        }
                    else  commissionProfileSetVO.setGradeName(SqlParameterEncoder.encodeParams(rs.getString("grade_name")));
        	        commissionProfileSetVO.setGrphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("GEOGRAPHY_CODE")));
        	        if("ALL".equals(commissionProfileSetVO.getGrphDomainCode())){
                    	commissionProfileSetVO.setGrphDomainName("ALL");
        	        }
        	        else commissionProfileSetVO.setGrphDomainName(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_NAME")));
        	        commissionProfileSetVO.setCommProfileSetId(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_id")));
        	        if(rs.getString("status").equalsIgnoreCase(PretupsI.COMMISSION_PROFILE_STATUS_SUSPEND)){
        	        	commissionProfileSetVO.setCommProfileSetName(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_name"))+BTSLUtil.NullToString(Constants.getProperty("COMM_PROFILE_SUSPENDED")));
        	        }
        	        else
        	        	commissionProfileSetVO.setCommProfileSetName(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_name")));
        	        
    			    commProfileList.add(commissionProfileSetVO);
        	    }
        	} 
        	}
        	catch (SQLException sqe)
        	{
        	    _log.error(methodName, "SQLException : " + sqe);
        	    _log.errorTrace(methodName, sqe);
        	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchUserDAO[loadCommProfileList]","","","","SQL Exception:"+sqe.getMessage());
        	    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        	} 
        	catch (Exception ex)
        	{
        	    _log.error(methodName, "Exception : " + ex);
        	    _log.errorTrace(methodName, ex);
        	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchUserDAO[loadCommProfileList]","","","","Exception:"+ex.getMessage());
        	    throw new BTSLBaseException(this, methodName, "error.general.processing");
        	}
        	finally
    		{
        	    if (_log.isDebugEnabled())
        	        _log.debug(methodName, "Exiting: commProfileList size =" + commProfileList.size());
    		}
        	return commProfileList;
    }
    /**
     * Method :loadTransferProfileList
     * This method load list of Transfer Profile on the basis of domain code and category 
     * @param p_con java.sql.Connection
     * @param p_domainCode java.lang.String
     * @param p_networkCode TODO
     * @return java.util.ArrayList
     * @throws  BTSLBaseException
     * @author ankit.agarwal
     */
    public ArrayList loadTransferProfileList(Connection p_con,String p_domainCode, String p_networkCode, String p_categoryCode,String p_userType) throws BTSLBaseException
    {
        	final String methodName = "loadMasterTransferProfileList";
    		if (_log.isDebugEnabled())
        	    _log.debug(methodName, "Entered: p_domainCode="+p_domainCode+" p_networkCode="+p_networkCode+"p_userType="+p_userType+"p_categoryCode="+p_categoryCode);
        	
    		StringBuffer strBuff = null;
    		  String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
    	        boolean tcpOn = false;
    	        Set<String> uniqueTransProfileId = new HashSet();
    	        
    	        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
    	        	tcpOn = true;
    	        }
    	      
    	        
    	        java.util.List<HashMap<String, String>> resultSet = null;
    	        
    	        if(tcpOn) {
    	        	strBuff = new StringBuffer(" SELECT CAT.category_code,CAT.category_name FROM categories CAT ");
    	    		strBuff.append(" WHERE CAT.domain_code=? AND CAT.status='Y'  " );
    	    		strBuff.append(" ");
    	    		//Changes made for Batch user initiate for channel user
    	    		if(!BTSLUtil.isNullString(p_userType)&&!p_userType.equals(PretupsI.OPERATOR_USER_TYPE))
    	    		{
    	    			strBuff.append(" AND CAT.sequence_no>(select sequence_no from  categories where category_code=?)");
    	    		}
    	    		//End of Changes made for Batch user initiate for channel user
    	    		strBuff.append(" ORDER BY CAT.sequence_no  ");
    	    		
    	 	
    	    		SearchCriteria searchCriteria = new SearchCriteria("parent_profile_id", Operator.EQUALS, "USER",
    	 					ValueType.STRING, null).addCriteria(new SearchCriteria("category_code", Operator.EQUALS, p_categoryCode,
    	 					ValueType.STRING, null), BooleanOperator.AND).addCriteria(new SearchCriteria("network_code", Operator.EQUALS, p_networkCode,
    	    	 					ValueType.STRING, null), BooleanOperator.AND);
    	         
    	         	resultSet = BTSLUtil.invokeService("TRANSFER_PROFILE", new HashSet<String>(Arrays.asList("profile_id","status","profile_name", "network_code")),
    	         			
    	         			searchCriteria);
    	         	
    	         	
    	        }else {
        	strBuff = new StringBuffer(" SELECT CAT.category_code,CAT.category_name,TP.profile_id, TP.profile_name FROM categories CAT,transfer_profile TP ");
    		strBuff.append(" WHERE CAT.domain_code=? AND CAT.status='Y' AND TP.parent_profile_id='USER' AND TP.category_code=? " );
    		strBuff.append("AND CAT.category_code=TP.category_code AND TP.status IN('Y','S')  AND TP.network_code=? ");
    		//Changes made for Batch user initiate for channel user
    		if(!BTSLUtil.isNullString(p_userType)&&!p_userType.equals(PretupsI.OPERATOR_USER_TYPE))
    		{
    			strBuff.append(" AND CAT.sequence_no>(select sequence_no from  categories where category_code=?)");
    		}
    		//End of Changes made for Batch user initiate for channel user
    		strBuff.append(" ORDER BY CAT.sequence_no  ");
    		
    	        }
    		
    		
    		
        	String sqlSelect = strBuff.toString();
        	if (_log.isDebugEnabled())
        	    _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        	ArrayList trfProfileList =new ArrayList();
        	ArrayList trfProfileList2 =new ArrayList();
        	
        	try(PreparedStatement  pstmtSelect = p_con.prepareStatement(sqlSelect);)
        	{
        	    
        		int i =1;
        		
        	    pstmtSelect.setString(i,p_domainCode);
        	    
        	    if(!tcpOn) {
        	    i++;
        	    pstmtSelect.setString(i,p_categoryCode);
        	    
        	    i++;
        	    pstmtSelect.setString(i,p_networkCode);

        	    }
        	    //Changes made for Batch user initiate for channel user
        	    if(!BTSLUtil.isNullString(p_userType)&&!p_userType.equals(PretupsI.OPERATOR_USER_TYPE))
        	    {
        	    	
        	    	i++;
        	    	pstmtSelect.setString(i,p_categoryCode);
        	    }
        	    //End of Changes made for Batch user initiate for channel user
        	   try(ResultSet rs = pstmtSelect.executeQuery();)
        	   {
    			TransferProfileVO profileVO = null;
        	    while(rs.next())
        	    {
        	        profileVO = new TransferProfileVO();
        	        profileVO.setCategory(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
        	        profileVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
        	        
        	        if(!tcpOn) {
        	        profileVO.setProfileId(SqlParameterEncoder.encodeParams(rs.getString("profile_id")));
        	        profileVO.setProfileName(SqlParameterEncoder.encodeParams(rs.getString("profile_name")));
        	        }
    			    trfProfileList.add(profileVO);
        	    }
        	} 
        	   
        	   
        	   
        	   Iterator<TransferProfileVO> itr = trfProfileList.iterator();
               
           	while(itr.hasNext()) {
           		
           		TransferProfileVO transferProfileVOObj = itr.next() ;

           		String catCode = transferProfileVOObj.getCategory();
           	
           		Iterator<HashMap<String, String>> itrTcp = resultSet.iterator();
           		
           		while(itrTcp.hasNext()) {
           			
           			HashMap<String, String> tcpMapObj = itrTcp.next();
           			if(tcpMapObj.get("categoryCode") != null && tcpMapObj.get("categoryCode").equalsIgnoreCase(transferProfileVOObj.getCategory())  && ( tcpMapObj.get("status").equalsIgnoreCase("Y") || tcpMapObj.get("status").equalsIgnoreCase("Y") )) {
           				
           				transferProfileVOObj.setProfileId(tcpMapObj.get("profileId"));
           				transferProfileVOObj.setProfileName(tcpMapObj.get("categoryName"));
           				trfProfileList2.add(transferProfileVOObj);
           				break;
           			}
           			
           		}
       			
       		}
           	
        	}
        	catch (SQLException sqe)
        	{
        	    _log.error(methodName, "SQLException : " + sqe);
        	    _log.errorTrace(methodName,sqe);
        	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchUserDAO[loadMasterTransferProfileList]","","","","SQL Exception:"+sqe.getMessage());
        	    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        	} 
        	catch (Exception ex)
        	{
        	    _log.error(methodName, "Exception : " + ex);
        	    _log.errorTrace(methodName,ex);
        	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchUserDAO[loadMasterTransferProfileList]","","","","Exception:"+ex.getMessage());
        	    throw new BTSLBaseException(this, methodName, "error.general.processing");
        	}
        	finally
    		{
        	    if (_log.isDebugEnabled())
        	        _log.debug(methodName, "Exiting: trfProfileList size =" + trfProfileList.size());
    		}
        	return trfProfileList;
    }
    
    /**
     * Method :loadBatchUserListForModifyASHU
     * This method load user details of the batch for Modification in a lightweight way.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_geographyCode
     *            java.lang.String
     * @param p_category_code
     *            java.lang.String
     * @param p_user_id
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ashutosh
     */
    public HashMap<String,ChannelUserVO> loadBatchUserListForModifyPOI(Connection p_con, String p_geographyCode, String p_category_code, String p_user_id) throws BTSLBaseException {
        final String methodName = "loadBatchUserListForModifyASHU";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_geographyCode=" + p_geographyCode + " p_user_id=" + p_user_id);
        }
        PreparedStatement pstmtSelect = null;
        
        HashMap<String,ChannelUserVO> userMap = new HashMap<String,ChannelUserVO>();        
     
        // Mutiple msisdn change start
        ArrayList msisdnList = null;
       
        ResultSet rsMsisdn = null;
        final StringBuffer strBuffMsisdn = new StringBuffer("SELECT user_phones_id,msisdn,primary_number,sms_pin,phone_language,country ");// phone_language,country
        strBuffMsisdn.append("FROM user_phones WHERE user_id=? ORDER BY primary_number DESC ");
        final String sqlSelectMsisdn = strBuffMsisdn.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectMsisdn=" + sqlSelectMsisdn);
        }
        try (PreparedStatement pstmtMsisdn = p_con.prepareStatement(sqlSelectMsisdn);){
          
            
            int i;
            pstmtSelect = batchUserQry.loadBatchUserListForModifyPOIQry(p_con, p_category_code, p_geographyCode, p_user_id);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            ChannelUserVO channelUserVO = null;
            UserPhoneVO userPhoneVO = null;
            String password = null;
            StringBuilder ashuBuild = new StringBuilder("");
            StringBuilder ashuMsisdn = new StringBuilder("");
            Runtime runtime = null;
            long memory = 0;
            while (rs.next()) {
            	if((rs.getRow() % 10000) == 0) {
            		System.out.println("Hey ASHU, rows fetched = "+rs.getRow());
            	}
            	if((rs.getRow() % 30000) == 0) {
            		 runtime = Runtime.getRuntime();
            		 memory = runtime.totalMemory() - runtime.freeMemory();
            		 System.out.println("Used memory is megabytes: " + (memory)/1048576);
                     // Run the garbage collector
                     runtime.gc();
                     // Calculate the used memory
                     memory = runtime.totalMemory() - runtime.freeMemory();
                     System.out.println("Used memory is megabytes: " + (memory)/1048576);
            	}
                channelUserVO = new ChannelUserVO();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setStatus(rs.getString("status"));
                channelUserVO.setCommissionProfileSetID(rs.getString("COMM_PROFILE_SET_ID"));
                channelUserVO.setTransferProfileID(rs.getString("TRANSFER_PROFILE_ID"));
                channelUserVO.setTrannferRuleTypeId(rs.getString("trf_rule_type"));
                channelUserVO.setUserGrade(rs.getString("USER_GRADE"));
                boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
                if (lmsAppl) {
                    channelUserVO.setLmsProfile(rs.getString("lms_profile"));
                }
                password = rs.getString("password");
                if (!BTSLUtil.isNullString(password)) {
                    channelUserVO.setPassword(BTSLUtil.decryptText(password));
                } else {
                    channelUserVO.setPassword("");
                }
                
				if (SystemPreferences.USERWISE_LOAN_ENABLE) 
                    channelUserVO.setLoanProfileId(rs.getString("profile_id"));
                
                
                
                // Multiple msisdn change start
                pstmtMsisdn.clearParameters();
                pstmtMsisdn.setString(1, rs.getString("user_id"));
                rsMsisdn = pstmtMsisdn.executeQuery();
                msisdnList = new ArrayList();
                String langcountry = null;// added by deepika aggarwal
                int count = 0;
                while (rsMsisdn.next()) {
                    userPhoneVO = new UserPhoneVO();
                    userPhoneVO.setMsisdn(rsMsisdn.getString("msisdn"));
                    userPhoneVO.setSmsPin(BTSLUtil.decryptText(rsMsisdn.getString("sms_pin")));
                    msisdnList.add(userPhoneVO);
                }
                channelUserVO.setMsisdnList(msisdnList);
                
                // Multiple msisdn change end
                userMap.put(channelUserVO.getUserID(),channelUserVO);
            }
        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchUserListForModifyASHU]", "", "", "",
                "GeographyCode=" + p_geographyCode + "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchUserListForModifyASHU]", "", "", "",
                "GeographyCode=" + p_geographyCode + "SQL Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsMsisdn != null) {
                    rsMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userMap size =" + userMap.size());
            }
        }
        return userMap;
    }
    
    
    
    
    
    /**
     * Method :downloadBatchListEnqDetails
     * This method load user details of the batch for enquiry.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_batchID
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public BulkUserAddStsRespDTO downloadBatchListEnqDetails(Connection p_con,BulkUserAddRptReqDTO bulkUserAddRptReqDTO , DownloadDataFomatReq downloadDataFomatReq) throws BTSLBaseException {
        final String methodName = "loadBatchDetailsListForEnq";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_batchID=" + bulkUserAddRptReqDTO.getBatchNo());
        }
        PreparedStatement pstmtSelect = null;
        File onlineFile = null;
        ResultSet rs = null;
        PreparedStatement pstmtSelectForSrevice = null;
        PreparedStatement pstmtSelectForRoles = null;
        ResultSet rsForService = null;
        ResultSet rsForRoles = null;
        BulkUserAddStsRespDTO bulkUserAddStsRespDTO = new BulkUserAddStsRespDTO();
        String offlineDownloadLocation = SystemPreferences.OFFLINERPT_DOWNLD_PATH;
        java.io.FileWriter outputWriter = null;
		FileOutputStream outExcel = null;
		File file = null;
		CSVWriter csvWriter=null;
		Workbook workbook =null;
		Sheet sheet =null;
		int continueLastRow=0;// For xlsx writing...
		int lastRow = 0;
		String filePath=null;

        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        Set<String> uniqueTransProfileId = new HashSet();
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        String sqlSelect = null;
        
        if(tcpOn) {
        	sqlSelect = batchUserQry.loadBatchDetailsListForEnqQry();
//        	sqlSelect = batchUserQry.loadBatchDetailsListForUsrEnqQry();
        }else {
        	sqlSelect = batchUserQry.loadBatchDetailsListForEnqQry();
//        	sqlSelect = batchUserQry.loadBatchDetailsListForUsrEnqQry();
        }
        // Modification for Service Management [by Vipul]
        final String selectServices = batchUserQry.loadBatchDetailsListForEnqSelectServicesQry();
    
        final String selectRoles = batchUserQry.loadBatchDetailsListForEnqSelectRolesQry();

        final ArrayList batchList = new ArrayList();
        
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setFetchSize(1000);
            pstmtSelectForSrevice = p_con.prepareStatement(selectServices.toString());
            pstmtSelectForRoles = p_con.prepareStatement(selectRoles.toString());

            int i = 1;
//            pstmtSelect.setString(i++, bulkUserAddRptReqDTO.getUserId());
            pstmtSelect.setString(i++, bulkUserAddRptReqDTO.getBatchNo());
            pstmtSelect.setString(i++, PretupsI.OUTLET_TYPE);
            pstmtSelect.setString(i++, PretupsI.OUTLET_TYPE);
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_TYPE);
            rs = pstmtSelect.executeQuery();
            ChannelUserVO channelUserVO = null;
            CategoryVO categoryVO = null;
            int j = 0;
            String service = null;
            String roles = null;
            
            BulkUserAddStsReportWriter bulkUserAddStsReportWriter = new BulkUserAddStsReportWriter();
//            filePath=offlineDownloadLocation+downloadDataFomatReq.getFileName()+"."+downloadDataFomatReq.getFileType().toLowerCase();
            //filePath="d://downloadedReports//"+downloadDataFomatReq.getFileName()+"."+downloadDataFomatReq.getFileType().toLowerCase();
            filePath = Constants.getProperty("DownloadBulkUserPathPrefix")+downloadDataFomatReq.getFileName()+"."+downloadDataFomatReq.getFileType().toLowerCase();
            file = new File(filePath);
            HashMap<String,String>	totalSummaryCaptureCols=null;
           	if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
    				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
        		outExcel =new FileOutputStream(file);
    		    workbook = new XSSFWorkbook();
    			sheet = workbook.createSheet(downloadDataFomatReq.getFileName());
    			try {
    				sheet.autoSizeColumn(PretupsRptUIConsts.ZERO.getNumValue());
        			sheet.autoSizeColumn(PretupsRptUIConsts.ONE.getNumValue());
        			sheet.autoSizeColumn(PretupsRptUIConsts.TWO.getNumValue());
    			}catch (Exception e) {
    				_log.error("", "Error occurred while autosizing columns");
    				e.printStackTrace();
    			}
    			
    			Font headerFont = workbook.createFont();
    			headerFont.setBold(true);
    			// headerFont.setFontHeightInPoints( (Short) 14);
    			CellStyle headerCellStyle = workbook.createCellStyle();
    			headerCellStyle.setFont(headerFont);
    	 totalSummaryCaptureCols = bulkUserAddStsReportWriter.constructXLSX(workbook,sheet, downloadDataFomatReq, bulkUserAddRptReqDTO,lastRow,headerCellStyle);
    	 continueLastRow=Integer.parseInt(totalSummaryCaptureCols.get(PretupsI.XLSX_LAST_ROW));
    		} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
    			//FileWriter already use  com.btsl.pretups.channel.transfer.util.clientutils.FileWriter,
       			//So using below package java.io.FileWriter
       			 outputWriter = new java.io.FileWriter(file);
    	    	 csvWriter = new CSVWriter(outputWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
    	    	 bulkUserAddStsReportWriter.constructCSV(csvWriter, downloadDataFomatReq, bulkUserAddRptReqDTO);
            }   
        	  Long totalNumberOfRecords =0l;
        	  bulkUserAddStsRespDTO.setNoDataFound(true);
            
            
            while (rs.next()) {
            	bulkUserAddStsRespDTO.setNoDataFound(false);
            	totalNumberOfRecords=totalNumberOfRecords+1;
                channelUserVO = new ChannelUserVO();
                channelUserVO.setRecordNumber("" + (j++));
                channelUserVO.setBatchID(rs.getString("batch_id"));
                channelUserVO.setParentStatus(rs.getString("parent_status"));
                channelUserVO.setParentLoginID(rs.getString("parent_login_id"));
                channelUserVO.setParentMsisdn(rs.getString("parent_msisdn"));
                channelUserVO.setUserName(rs.getString("user_name"));
                channelUserVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                channelUserVO.setShortName(rs.getString("short_name"));
                channelUserVO.setNetworkID(rs.getString("network_code"));
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setLoginID(rs.getString("login_id"));
                channelUserVO.setPassword(rs.getString("password"));
                channelUserVO.setCategoryCode(rs.getString("category_code"));
                channelUserVO.setParentID(rs.getString("parent_id"));
                channelUserVO.setAllowedIps(rs.getString("allowed_ip"));
                channelUserVO.setAllowedDays(rs.getString("allowed_days"));
                channelUserVO.setLastLoginOn(rs.getTimestamp("last_login_on"));
                channelUserVO.setEmpCode(rs.getString("employee_code"));
                channelUserVO.setStatus(rs.getString("status"));
                channelUserVO.setStatusDesc(rs.getString("StatusDesc"));
                channelUserVO.setEmail(rs.getString("email"));
                channelUserVO.setContactNo(rs.getString("contact_no"));
                channelUserVO.setDesignation(rs.getString("designation"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setUserType(rs.getString("user_type"));
                channelUserVO.setAddress1(rs.getString("address1"));
                channelUserVO.setAddress2(rs.getString("address2"));
                channelUserVO.setCity(rs.getString("city"));
                channelUserVO.setState(rs.getString("state"));
                channelUserVO.setCountry(rs.getString("country"));
                channelUserVO.setSsn(rs.getString("ssn"));
                channelUserVO.setExternalCode(rs.getString("external_code"));
                channelUserVO.setUserCode(rs.getString("user_code"));
                channelUserVO.setReferenceID(rs.getString("reference_id"));
                channelUserVO.setAppointmentDate(rs.getDate("appointment_date"));
                channelUserVO.setPreviousStatus(rs.getString("previous_status"));
                channelUserVO.setCreationType(rs.getString("creation_type"));
                channelUserVO.setUserGrade(rs.getString("user_grade"));
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                
                if(tcpOn) {
                	uniqueTransProfileId.add(rs.getString("transfer_profile_id"));
                }
                
                if(!tcpOn) {
                channelUserVO.setTransferProfileName(rs.getString("profile_name"));
                }
                
                channelUserVO.setSubOutletCode(rs.getString("sub_lookup_name"));
                channelUserVO.setOutletCode(rs.getString("OutletName"));
                channelUserVO.setOutSuspened(rs.getString("out_suspend"));
                channelUserVO.setInSuspend(rs.getString("in_suspend"));
                channelUserVO.setContactPerson(rs.getString("contact_person"));
                channelUserVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelUserVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                channelUserVO.setGeographicalDesc(rs.getString("grph_domain_name"));
                channelUserVO.setCategoryName(rs.getString("category_name"));
                channelUserVO.setRemarks(rs.getString("user_remarks"));
                // added by deepika aggarwal
                channelUserVO.setCompany(rs.getString("company"));
                channelUserVO.setFax(rs.getString("fax"));
                channelUserVO.setFirstName(rs.getString("firstname"));
                channelUserVO.setLastName(rs.getString("lastname"));
                // end
                categoryVO = new CategoryVO();
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                channelUserVO.setCategoryVO(categoryVO);

                // for Zebra and Tango by Sanjeew date 09/07/07
                channelUserVO.setMcommerceServiceAllow(rs.getString("mcommerce_service_allow"));
                channelUserVO.setMpayProfileID(rs.getString("mpay_profile_id"));
                channelUserVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                // End Zebra and Tango

                // added for rsa authentication by shashank
                channelUserVO.setRsaFlag(rs.getString("rsaflag"));
                channelUserVO.setAuthTypeAllowed(rs.getString("authentication_allowed"));
                // end
                if (PretupsI.NO.equals(categoryVO.getFixedRoles())) {
                    // load roles from user roles
                    pstmtSelectForRoles.clearParameters();
                    pstmtSelectForRoles.setString(1, channelUserVO.getUserID());
                    rsForRoles = pstmtSelectForRoles.executeQuery();
                    roles = "";
                    while (rsForRoles.next()) {
                        roles = roles + rsForRoles.getString("role_name") + ",";
                    }
                    if (roles.length() > 1) {
                        roles = roles.substring(0, roles.length() - 1);
                    }
                    if (rsForRoles != null) {
                        rsForRoles.close();
                    }
                    channelUserVO.setGroupRoleCode(roles);
                }
                if (PretupsI.YES.equals(categoryVO.getServiceAllowed())) {
                    // load services from user services
                    pstmtSelectForSrevice.clearParameters();
                    pstmtSelectForSrevice.setString(1, channelUserVO.getUserID());
                    rsForService = pstmtSelectForSrevice.executeQuery();
                    service = "";
                    while (rsForService.next()) {
                        service = service + rsForService.getString("name") + ",";
                    }
                    if (service.length() > 1) {
                        service = service.substring(0, service.length() - 1);
                    }
                    if (rsForService != null) {
                        rsForService.close();
                    }
                    channelUserVO.setServiceTypes(service);
                }
                batchList.add(channelUserVO);
                
                if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
        				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
    				bulkUserAddStsReportWriter.writeXLSXRow(workbook,sheet,downloadDataFomatReq,continueLastRow,channelUserVO);
    				continueLastRow=continueLastRow+1;
    				if(totalNumberOfRecords%5000==0) {
    					outExcel.flush();
    					workbook.write(outExcel);
    				}
    			} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
    				bulkUserAddStsReportWriter.writeCSVRow(csvWriter,downloadDataFomatReq,channelUserVO);
                	if(totalNumberOfRecords%5000==0) {
    					csvWriter.flush();
    					outputWriter.flush();
    				}
                	
                }
            }
            if (tcpOn) {
				SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, uniqueTransProfileId,
						ValueType.STRING);
				 BTSLUtil.updateMapViaMicroServiceResultSet(batchList, BTSLUtil
						.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","profile_Name","status")), searchCriteria));
			}
            
        
		
		if(outExcel!=null && workbook!=null) {
    		outExcel.flush();
			workbook.write(outExcel);
		}
		if(csvWriter!=null && outputWriter!=null) {
			csvWriter.flush();
			outputWriter.flush();
		}
		   	    bulkUserAddStsRespDTO.setTotalDownloadedRecords(String.valueOf(totalNumberOfRecords));

            
            
            
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchDetailsListForEnq]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchDetailsListForEnq]", "", "", "",
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsForService != null) {
                    rsForService.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectForSrevice != null) {
                    pstmtSelectForSrevice.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsForRoles != null) {
                    rsForRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectForRoles != null) {
                    pstmtSelectForRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: batchList size =" + batchList.size());
            }
                    	try{
                    if (pstmtSelect!= null){
                    	pstmtSelect.close();
                    }
                  }
                  catch (SQLException e){
                	  _log.error("An error occurred closing prepared statement.", e);
                  }
            	 
            	  if(csvWriter!=null) {
            		  try {
    					csvWriter.close();
    				} catch (IOException e) {
    					_log.error("An error occurred closing csvwriter.", e);
    				}
            	  }
            	  
            	  if(outputWriter!=null) {
            		  try {
            			  outputWriter.close();
    				} catch (IOException e) {
    					_log.error("An error occurred closing csvwriter.", e);
    				}
            	  }
            	  
            	   if(outExcel!=null) {
            		   try {
    					outExcel.close();
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					_log.error("An error occurred closing XSLX Writer.", e);
    				}
            	   }
            	   
            	   
            	   if(workbook!=null) {
            		   try {
            			   workbook.close();
            			   
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					_log.error("An error occurred closing XSLX Writer.", e);
    				}
            	   }
            	   
            	   
            	    onlineFile= new File(filePath);
            	   	byte[] fileContent=null;
    				try {
    					fileContent = Files.readAllBytes(file.toPath());
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					_log.error("An error while reading online file.", e);
    				}
        	        String fileContentString = Base64.getEncoder().encodeToString(fileContent);
        	        bulkUserAddStsRespDTO.setOnlineDownloadFileData(fileContentString);
        	         if(onlineFile.delete()) {
        	        	 _log.info(methodName,"An Online file deleted"+ offlineDownloadLocation +bulkUserAddRptReqDTO.getFileName());
        	         }
            
        }
        return bulkUserAddStsRespDTO;
    }
    
    
    
    
    /**
     * Method :loadBatchListForEnquiry
     * This method load list of batches on the basis of geographyCode,
     * domainCode, fromDate and toDate
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            java.lang.String
     * @param p_geographyCode
     *            java.lang.String
     * @param p_domainCode
     *            java.lang.String
     * @param p_fromDate
     *            java.lang.String
     * @param p_toDate
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Amit singh
     */
    public ArrayList<BatchesVO> loadBatchListForUsrEnquiry(Connection p_con, String p_networkCode, String p_geographyCode, String p_domainCode, String p_fromDate, String p_toDate, String p_categoryType, String p_userType,String loggedinUserID) throws BTSLBaseException {
        final String methodName = "loadBatchListForApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered: p_geographyCode=" + p_geographyCode + " p_networkCode=" + p_networkCode + " p_domainCode=" + p_domainCode + "p_fromDate=" + p_fromDate + "p_toDate=" + p_toDate + "p_categoryType=" + p_categoryType + "p_userType=" + p_userType);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        final String geographyCode = p_geographyCode.replaceAll("'", "");
        final String gg = geographyCode.replaceAll("\" ", "");
        final String m_geographyCode[] = gg.split(",");
       
        
        final String sqlSelect = batchUserQry.loadBatchListForUsrEnquiryQry(m_geographyCode, p_userType,loggedinUserID);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList batchList = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_NEW);
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_ACTIVE);
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(i++, loggedinUserID);
            pstmtSelect.setString(i++, PretupsI.BATCH_USR_CREATION_TYPE);
            
            for (int x = 0; x < m_geographyCode.length; x++) {
                pstmtSelect.setString(i++, m_geographyCode[x]);
            }
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getDateFromDateString(p_fromDate)));
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getDateFromDateString(p_toDate)));
            pstmtSelect.setString(i++, p_domainCode);
            pstmtSelect.setString(i++, PretupsI.BATCH_STATUS_LOOKUP);
            if (p_userType.equals(PretupsI.CHANNEL_USER_TYPE)) {
                pstmtSelect.setString(i++, p_categoryType);
            }
            rs = pstmtSelect.executeQuery();
            BatchesVO bacthesVO = null;
            while (rs.next()) {
                bacthesVO = new BatchesVO();
                bacthesVO.setBatchID(rs.getString("batch_id"));
                bacthesVO.setStatus(rs.getString("status"));
                bacthesVO.setStatusDesc(rs.getString("lookup_name"));
                bacthesVO.setBatchName(rs.getString("batch_name"));
                bacthesVO.setFileName(rs.getString("file_name"));
                bacthesVO.setBatchSize(rs.getLong("batch_size"));
                bacthesVO.setBatchType(rs.getString("batch_type"));
                bacthesVO.setCreatedBy(rs.getString("created_by"));
                bacthesVO.setCreatedOn(rs.getTimestamp("created_on"));
                if (bacthesVO.getCreatedOn() != null) {
                    bacthesVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(bacthesVO.getCreatedOn())));
                }
                bacthesVO.setModifiedBy(rs.getString("modified_by"));
                bacthesVO.setModifiedOn(rs.getTimestamp("modified_on"));
                if (bacthesVO.getModifiedOn() != null) {
                    bacthesVO.setModifiedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(bacthesVO.getModifiedOn())));
                }
                bacthesVO.setActiveRecords(rs.getInt("active"));
                bacthesVO.setNewRecords(rs.getInt("new"));
                bacthesVO.setRejectRecords(rs.getInt("reject"));

                batchList.add(bacthesVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchListForApproval]", "", "",
                "NetworkCode=" + p_networkCode, "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchListForApproval]", "", "",
                "NetworkCode=" + p_networkCode, "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: batchList size =" + batchList.size());
            }
        }
        return batchList;
    }

    
    /**
     * Method :loadBatchListForEnquiry
     * This method load list of batches on the basis of batchID.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            java.lang.String
     * @return BatchesVO
     * @throws BTSLBaseException
     * @author Amit singh
     */
    public BatchesVO loadBatchListForUsrEnquiry(Connection p_con, String p_networkCode, String p_batchID,String loggedInUserID) throws BTSLBaseException {
        final String methodName = "loadBatchListForEnquiry";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkCode=" + p_networkCode + " p_batchID=" + p_batchID);
        }
        
       
        final String sqlSelect = batchUserQry.loadBatchListForEnquiryQryUsr();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        BatchesVO batchesVO = null;
        try(PreparedStatement  pstmtSelect = p_con.prepareStatement(sqlSelect);) {
          
            int i = 1;

            pstmtSelect.setString(i++, PretupsI.USER_STATUS_NEW);
            if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
                pstmtSelect.setString(i++, PretupsI.USER_STATUS_PREACTIVE);
            } else {
                pstmtSelect.setString(i++, PretupsI.USER_STATUS_ACTIVE);
            }
            pstmtSelect.setString(i++, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(i++,loggedInUserID);
            pstmtSelect.setString(i++, p_batchID);
            pstmtSelect.setString(i++, PretupsI.BATCH_USR_CREATION_TYPE);
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, PretupsI.BATCH_STATUS_LOOKUP);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (rs.next()) {
                batchesVO = new BatchesVO();
                batchesVO.setBatchID(rs.getString("batch_id"));
                batchesVO.setStatus(rs.getString("status"));
                batchesVO.setStatusDesc(rs.getString("lookup_name"));
                batchesVO.setBatchName(rs.getString("batch_name"));
                batchesVO.setFileName(rs.getString("file_name"));
                batchesVO.setBatchSize(rs.getLong("batch_size"));
                batchesVO.setBatchType(rs.getString("batch_type"));
                batchesVO.setCreatedBy(rs.getString("created_by"));
                batchesVO.setCreatedOn(rs.getTimestamp("created_on"));
                if (batchesVO.getCreatedOn() != null) {
                    batchesVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchesVO.getCreatedOn())));
                }
                batchesVO.setModifiedOn(rs.getTimestamp("modified_on"));
                if (batchesVO.getModifiedOn() != null) {
                    batchesVO.setModifiedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchesVO.getModifiedOn())));
                }
                batchesVO.setActiveRecords(rs.getInt("active"));
                batchesVO.setNewRecords(rs.getInt("new"));
                batchesVO.setRejectRecords(rs.getInt("reject"));
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchListForEnquiry]", "", "",
                "NetworkCode=" + p_networkCode, "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchListForEnquiry]", "", "",
                "NetworkCode=" + p_networkCode, "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting batchesVO: " + batchesVO);
            }
        }
        return batchesVO;
    }
    
    public List<BulkModifyListVO> loadBulkUserListModify(Connection p_con, String p_geographyCode, String p_category_code, String p_user_id) throws BTSLBaseException {
        final String methodName = "loadBatchUserListForModify";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_geographyCode=" + p_geographyCode + " p_user_id=" + p_user_id);
        }

        List<BulkModifyListVO> list = new ArrayList<BulkModifyListVO>();
        
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
         

        PreparedStatement pstmtGeoDomain = null;
        ResultSet rsGeoDomain = null;
        String geoDomain = null;
        final StringBuffer strBuffGeodomain = new StringBuffer("SELECT grph_domain_code FROM user_geographies WHERE user_id=?");
        final String sqlSelectGeodomain = strBuffGeodomain.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectGeodomain=" + sqlSelectGeodomain);
        }

        PreparedStatement pstmtRoles = null;
        ResultSet rsRoles = null;
        ResultSet rsRoles1 = null;
        String roles = null;
        final StringBuffer strBuffRoles = new StringBuffer("SELECT UR.role_code FROM user_roles UR, roles R ");
        strBuffRoles.append("WHERE UR.role_code=R.role_code AND R.status=? AND UR.user_id=? AND R.domain_type=? AND R.group_role=? ");
        final String sqlSelectRoles = strBuffRoles.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectRoles=" + sqlSelectRoles);
        }

        PreparedStatement pstmtServices = null;
        ResultSet rsServices = null;
        String services = null;
        // Modification for Service Management [by Vipul]
        final StringBuffer strBuffServices = new StringBuffer("SELECT US.service_type");
        strBuffServices.append(" FROM user_services US,users U,category_service_type CST");
        strBuffServices
            .append(" WHERE US.user_id=? AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type and CST.network_code=U.network_code");

        final String sqlSelectServices = strBuffServices.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectServices=" + sqlSelectServices);
        }
        
        //Voucher Type
        PreparedStatement pstmtVouchers = null;
        ResultSet rsVouchers = null;
        String vouchers = null;
        StringBuffer strBuffVouchers= new StringBuffer(" SELECT uv.voucher_type, vt.name ");
        strBuffVouchers.append(" FROM user_vouchertypes uv, voms_types vt, users u ");
        strBuffVouchers.append(" WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id ");
        final String sqlSelectVouchers = strBuffVouchers.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectVouchers=" + sqlSelectVouchers);
        }
        
        PreparedStatement pstmtSegments = null;
        ResultSet rsSegments = null;
        String segments = null;
        StringBuffer strBuffSegments= new StringBuffer(" SELECT us.VOUCHER_SEGMENT, lu.LOOKUP_NAME ");
        strBuffSegments.append(" FROM USER_VOUCHER_SEGMENTS us, LOOKUPS lu, users u ");
        strBuffSegments.append(" WHERE us.user_id = ? AND us.VOUCHER_SEGMENT = lu.LOOKUP_CODE  AND u.user_id = us.user_id ");
        final String sqlSelectSegments = strBuffSegments.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectSegments=" + sqlSelectSegments);
        }
        
        // Mutiple msisdn change start
        ArrayList msisdnList = null;
        PreparedStatement pstmtMsisdn = null;
        ResultSet rsMsisdn = null;
        final StringBuffer strBuffMsisdn = new StringBuffer("SELECT user_phones_id,msisdn,primary_number,sms_pin,phone_language,country ");// phone_language,country
        // added
        // by
        // deepika
        // aggarwal
        strBuffMsisdn.append("FROM user_phones WHERE user_id=? ORDER BY primary_number DESC ");
        final String sqlSelectMsisdn = strBuffMsisdn.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectMsisdn=" + sqlSelectMsisdn);
            // Mutiple msisdn change end
        }

        final ArrayList batchList = new ArrayList();
        BulkModifyListVO bulkModListVO = null;
        
        try {
           
            pstmtGeoDomain = p_con.prepareStatement(sqlSelectGeodomain);
            pstmtRoles = p_con.prepareStatement(sqlSelectRoles);
            pstmtServices = p_con.prepareStatement(sqlSelectServices);
            pstmtVouchers = p_con.prepareStatement(sqlSelectVouchers);
            pstmtSegments = p_con.prepareStatement(sqlSelectSegments);
            pstmtMsisdn = p_con.prepareStatement(sqlSelectMsisdn);
            int i = 1;
            pstmtSelect = batchUserQry.loadBatchUserListForModifyQry(p_con, p_category_code, p_geographyCode, p_user_id);
            rs = pstmtSelect.executeQuery();


            String password = null;
            while (rs.next()) {
        		bulkModListVO = new BulkModifyListVO();
                password = rs.getString("password");
                bulkModListVO.setUserID(rs.getString("user_id"));
                bulkModListVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                bulkModListVO.setWebLoginID(rs.getString("login_id"));
                if (!BTSLUtil.isNullString(password)) {
                    bulkModListVO.setWebLoginPassword(BTSLUtil.decryptText(password));
                } else {
                    bulkModListVO.setWebLoginPassword("");
                    
                }

                bulkModListVO.setMobileNumber(rs.getString("msisdn"));
                bulkModListVO.setPin(BTSLUtil.decryptText(rs.getString("sms_pin")));


                i = 1;
                pstmtGeoDomain.clearParameters();
                pstmtGeoDomain.setString(i++, rs.getString("user_id"));
                rsGeoDomain = pstmtGeoDomain.executeQuery();
                geoDomain = "";
                while (rsGeoDomain.next()) {
                    geoDomain = geoDomain + ", " + rsGeoDomain.getString("grph_domain_code");
                }
                if (!BTSLUtil.isNullString(geoDomain)) {
                    geoDomain = geoDomain.substring(1);
                }
                bulkModListVO.setGeoDomainCode(geoDomain);

                i = 1;
                pstmtRoles.clearParameters();
                pstmtRoles.setString(i++, TypesI.YES);
                pstmtRoles.setString(i++, rs.getString("user_id"));
                pstmtRoles.setString(i++, rs.getString("domain_type_code"));
                pstmtRoles.setString(i++, PretupsI.YES);
                rsRoles = pstmtRoles.executeQuery();
                if (rsRoles.next()) {
                    bulkModListVO.setRoleCode(rsRoles.getString("role_code"));
                    bulkModListVO.setGroupRoleAllowed(PretupsI.YES);
                } else {
                    i = 1;
                    pstmtRoles.clearParameters();
                    pstmtRoles.setString(i++, TypesI.YES);
                    pstmtRoles.setString(i++, rs.getString("user_id"));
                    pstmtRoles.setString(i++, rs.getString("domain_type_code"));
                    pstmtRoles.setString(i++, PretupsI.NO);
                    rsRoles1 = pstmtRoles.executeQuery();
                    roles = "";
                    while (rsRoles1.next()) {
                        roles = roles + ", " + rsRoles1.getString("role_code");
                    }
                    if (!BTSLUtil.isNullString(roles)) {
                        roles = roles.substring(1);
                    }
//                    bulkModListVO.setRoleCode(rsRoles.getString(roles));
                    bulkModListVO.setRoleCode(roles);
                    bulkModListVO.setGroupRoleAllowed(PretupsI.NO);
                }

                i = 1;
                pstmtServices.clearParameters();
                pstmtServices.setString(i++, rs.getString("user_id"));
                rsServices = pstmtServices.executeQuery();
                services = "";
                while (rsServices.next()) {
                    services = services + ", " + rsServices.getString("service_type");
                }
                if (!BTSLUtil.isNullString(services)) {
                    services = services.substring(1);
                }
                bulkModListVO.setServices(services);

                //Payment Type
                i = 1;
                pstmtVouchers.clearParameters();
                pstmtVouchers.setString(i++, rs.getString("user_id"));
                rsVouchers = pstmtVouchers.executeQuery();
                vouchers = "";
                while (rsVouchers.next()) {
                    vouchers = vouchers + ", " + rsVouchers.getString("voucher_type");
                }
                if (!BTSLUtil.isNullString(vouchers)) {
                    vouchers = vouchers.substring(1);
                }
                bulkModListVO.setVoucherType(vouchers);
                
                i = 1;
                pstmtSegments.clearParameters();
                pstmtSegments.setString(i++, rs.getString("user_id"));
                rsSegments = pstmtSegments.executeQuery();
                segments = "";
                while (rsSegments.next()) {
                	segments = segments + ", " + rsSegments.getString("voucher_segment");
                }
                if (!BTSLUtil.isNullString(segments)) {
                	segments = segments.substring(1);
                }
                //Payment Type end
                
                bulkModListVO.setShortName(rs.getString("short_name"));
                bulkModListVO.setExternalCode(rs.getString("external_code"));
                bulkModListVO.setInSuspend(rs.getString("in_suspend"));
                bulkModListVO.setOutSuspend(rs.getString("out_suspend"));
                bulkModListVO.setContactPerson(rs.getString("contact_person"));
                bulkModListVO.setContactNumber(rs.getString("contact_no"));
                bulkModListVO.setRsaSecureID(rs.getString("ssn"));
                bulkModListVO.setDesignation(rs.getString("designation"));
                bulkModListVO.setAddress1(rs.getString("address1"));
                bulkModListVO.setAddress2(rs.getString("address2"));
                bulkModListVO.setCity(rs.getString("city"));
                bulkModListVO.setState(rs.getString("state"));
                bulkModListVO.setCountry(rs.getString("country"));
                bulkModListVO.setEmail(rs.getString("email"));
                bulkModListVO.setCompany(rs.getString("company"));
                bulkModListVO.setFax(rs.getString("fax"));
                bulkModListVO.setFirstName(rs.getString("firstname"));
                bulkModListVO.setLastName(rs.getString("lastname"));
                
                // end
                bulkModListVO.setAllowLowBalAlert(rs.getString("low_bal_alert_allow"));


                // Multiple msisdn change start
                pstmtMsisdn.clearParameters();
                pstmtMsisdn.setString(1, rs.getString("user_id"));
                rsMsisdn = pstmtMsisdn.executeQuery();
                msisdnList = new ArrayList();
                String langcountry = null;// added by deepika aggarwal

                while (rsMsisdn.next()) {
                    langcountry = rsMsisdn.getString("phone_language") + "_" + rsMsisdn.getString("country");// added
                }
                
                bulkModListVO.setLanguage(langcountry); 
                bulkModListVO.setLongitude(rs.getString("longitude"));
                bulkModListVO.setLatitude(rs.getString("latitude"));
                bulkModListVO.setDocumentType(rs.getString("document_type"));
                bulkModListVO.setDocumentNo(rs.getString("document_no"));
                bulkModListVO.setPaymentType(rs.getString("payment_type"));
                bulkModListVO.setCommProfile(rs.getString("COMM_PROFILE_SET_ID"));
                bulkModListVO.setTrfProfile(rs.getString("TRANSFER_PROFILE_ID"));
                bulkModListVO.setGrade(rs.getString("USER_GRADE"));
                
                list.add(bulkModListVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchUserListForModify]", "", "", "",
                "GeographyCode=" + p_geographyCode + "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadBatchUserListForModify]", "", "", "",
                "GeographyCode=" + p_geographyCode + "SQL Exception:" + ex.getMessage());
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
                if (rsRoles != null) {
                	rsRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsRoles1 != null) {
                	rsRoles1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsVouchers != null) {
                	rsVouchers.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSegments != null) {
                	rsSegments.close();
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
            try {
                if (pstmtVouchers != null) {
                	pstmtVouchers.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSegments != null) {
                	pstmtSegments.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsGeoDomain != null) {
                    rsGeoDomain.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtGeoDomain != null) {
                    pstmtGeoDomain.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsRoles != null) {
                    rsRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtRoles != null) {
                    pstmtRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsServices != null) {
                    rsServices.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtServices != null) {
                    pstmtServices.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsMsisdn != null) {
                    rsMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtMsisdn != null) {
                    pstmtMsisdn.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: batchList size =" + batchList.size());
            }
        }
        return list;
    }
    
    
    
	public Object getAllLoanProfileList(Connection con) throws BTSLBaseException {

		final String methodName = "getAllLoanProfileList";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered:");
		}
		List<ListValueVO> loanProfileList = new ArrayList<>();
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		String selectQuery = " select lp.profile_id,lp.profile_name,c.category_name,c.category_code from "
				+ "loan_profiles lp inner join categories c on lp.category_code=c.category_code where c.status='Y' and lp.status='Y' ";
		try {
			preparedStatement = con.prepareStatement(selectQuery);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				loanProfileList.add(new ListValueVO(resultSet.getString("profile_name")+"_"+resultSet.getString("category_code")+"_"+resultSet.getString("category_name"), resultSet.getString("profile_id")));
			}
		} catch (SQLException sqlException) {
			_log.error(methodName, "SQLException : " + sqlException);
			_log.errorTrace(methodName, sqlException);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting :" + loanProfileList.size());
			}
		}
		return loanProfileList;
	}
	
	
	
	
	
	
	
	/**
     * Method :updateUserInBatchForApproval
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            TODO
     * @param p_initiatorUserType
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Anand Swaraj
     */
    public ArrayList updateUserInBatchForApproval(Connection p_con, ArrayList p_userDetails, Locale p_locale, UserVO p_userVO, String p_initiatorUserType) throws BTSLBaseException {
        final String methodName = "updateUserInBatchForApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userDetails.size()=" + p_userDetails.size() +  " p_locale=" + p_locale + " p_userVO=" + p_userVO
                .toString() + " p_initiatorUserType " + p_initiatorUserType);
        }
        
        ResultSet rsSelectParentUsrStatus = null;
        final StringBuffer selectParentStatus = new StringBuffer("SELECT UP.status FROM users U, users UP	WHERE U.user_id=? AND U.parent_id=UP.user_id");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectParentStatus=" + selectParentStatus);
        }
        final StringBuffer strBuffUsr = new StringBuffer(" UPDATE users SET level1_approved_by=?, level1_approved_on=?,");
        strBuffUsr.append(" modified_by=?, modified_on=?, status=?, previous_status=?,remarks=?, login_id=?, rsaflag=?,ssn=? WHERE user_id=?");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY strBuffUsr=" + strBuffUsr);
        }
        // modified by shashank
        final StringBuffer strBuffChnlUsr = new StringBuffer(" UPDATE channel_users SET activated_on=? ");
        // if(p_initiatorUserType.equals(PretupsI.CHANNEL_USER_TYPE) &&
        // !SystemPreferences.BATCH_USER_PROFILE_ASSIGN)
        // {
        strBuffChnlUsr.append(" ,user_grade=?, TRANSFER_PROFILE_ID=?, COMM_PROFILE_SET_ID=?, ");
        strBuffChnlUsr.append(" mcommerce_service_allow=?, mpay_profile_id=?, trf_rule_type=?");
        // }
        strBuffChnlUsr.append(" WHERE user_id=? ");
        //
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY strBuffChnlUsr=" + strBuffChnlUsr);
        }
        final ArrayList errorList = new ArrayList();
        int updateCount = 0;
        try(PreparedStatement  pstmtUpdateUsr = p_con.prepareStatement(strBuffUsr.toString());
        		PreparedStatement pstmtUpdateChnlUsr = p_con.prepareStatement(strBuffChnlUsr.toString());
        		PreparedStatement  pstmtSelectParentUsrStatus = p_con.prepareStatement(selectParentStatus.toString());) {
            
            ChannelUserVO channelUserVO = null;
            int updateCountUsr = 0;
            int updateCountChnlUsr = 0;
            ListValueVO errorVO = null;
            int warnings = 0;
            String msg = null;
            String parentStatus = null;
            CategoryVO categoryVO = null;
            PushMessage pushMessage = null;
            BTSLMessages btslPushMessage = null;
            String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
            String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
            final Locale defaultLocale = new Locale(defaultLanguage, defaultCountry);
            final Map userMap = null;
            String paymentNumber = null;
            final CommonUtil commonUtil = null;
            // Email for pin & Password
            EmailSendToUser emailSendToUser = null;
            String subject = null;
            for (int i = 0, j = p_userDetails.size(); i < j; i++) {
                try {
                    updateCountUsr = 0;
                    updateCountChnlUsr = 0;
                    channelUserVO = (ChannelUserVO) p_userDetails.get(i);
                    categoryVO = channelUserVO.getCategoryVO();
                    pstmtSelectParentUsrStatus.clearParameters();
                    pstmtSelectParentUsrStatus.setString(1, channelUserVO.getUserID());
                    rsSelectParentUsrStatus = pstmtSelectParentUsrStatus.executeQuery();
                    if (rsSelectParentUsrStatus.next() && categoryVO.getSequenceNumber() != 1) {
                        parentStatus = rsSelectParentUsrStatus.getString("status");
                        if (parentStatus.equals(PretupsI.USER_STATUS_DELETED)) {
                            channelUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
                            //msg = p_messages.getMessage(p_locale, "bulkuser.processuploadedfile.error.parentnotactivedelete", channelUserVO.getUserName());
                            String str = channelUserVO.getUserName();
                            String[] strArray = new String[] {str};  
                            msg = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_ACTIVE_DELETE, strArray);
                            channelUserVO.setRemarks(msg);
                            errorVO = new ListValueVO("WARNING", channelUserVO.getRecordNumber(), msg);
                            errorList.add(errorVO);
                            warnings++;
//                            BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Success with warning :=" + p_messages.getMessage(
//                                "bulkuser.processuploadedfile.error.parentnotactivedelete", channelUserVO.getUserName()));
                            BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Success with warning :=" + msg);
                        }

                        if (channelUserVO.getStatus().equals(PretupsI.USER_STATUS_DELETED) || parentStatus.equals(PretupsI.USER_STATUS_ACTIVE)) {
                            pstmtUpdateUsr.setString(5, channelUserVO.getStatus());
                            pstmtUpdateUsr.setString(7, channelUserVO.getRemarks());
                        } else {
                            //msg = p_messages.getMessage(p_locale, "bulkuser.processuploadedfile.error.parentnotactivediscard", channelUserVO.getUserName());
                        	String str = channelUserVO.getUserName();
                            String[] strArray = new String[] {str};  
                            msg = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_ACTIVE_DISCARD, strArray);
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), msg);
                            errorList.add(errorVO);
//                            BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.processuploadedfile.error.parentnotactivediscard",
//                                channelUserVO.getUserName()));
                            BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + msg);
                            continue;
                        }
                    } else if (categoryVO.getSequenceNumber() == 1)// parent
                    // (owner
                    // user)
                    {
                        pstmtUpdateUsr.setString(5, channelUserVO.getStatus());
                        pstmtUpdateUsr.setString(7, channelUserVO.getRemarks());
                    } else {
//                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
//                            "bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                    	errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_EXIST, null));
                        errorList.add(errorVO);
//                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_EXIST, null));
                        continue;
                    }
                    if (channelUserVO.getStatus().trim().equalsIgnoreCase(PretupsI.USER_STATUS_DELETED)) {
                        channelUserVO.setLoginID(channelUserVO.getUserID());
                    }
                    pstmtUpdateUsr.setString(1, channelUserVO.getLevel1ApprovedBy());
                    pstmtUpdateUsr.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLevel1ApprovedOn()));
                    pstmtUpdateUsr.setString(3, channelUserVO.getModifiedBy());
                    pstmtUpdateUsr.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                    pstmtUpdateUsr.setString(6, channelUserVO.getPreviousStatus());
                    pstmtUpdateUsr.setString(8, channelUserVO.getLoginID());
                    // added by shashank for rsa
                    pstmtUpdateUsr.setString(9, channelUserVO.getRsaFlag());
                    pstmtUpdateUsr.setString(10, channelUserVO.getSsn());
                    // end
                    pstmtUpdateUsr.setString(11, channelUserVO.getUserID());
                    updateCountUsr = pstmtUpdateUsr.executeUpdate();
                    pstmtUpdateUsr.clearParameters();
                    if (updateCountUsr <= 0) {
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
//                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages
//                            .getMessage(p_locale, "bulkuser.processuploadedfile.error.updateusererr"));
                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), 
                        		RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.UPDATE_USER_DETAIL_ERROR, null));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.UPDATE_USER_DETAIL_ERROR, null));
                        continue;
                    }
                    pstmtUpdateChnlUsr.clearParameters();
                    int k = 1;
                    pstmtUpdateChnlUsr.setTimestamp(k, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getActivatedOn()));
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getUserGrade());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getTransferProfileID());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getCommissionProfileSetID());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getMcommerceServiceAllow());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getMpayProfileID());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getTrannferRuleTypeId());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getUserID());

                    // pstmtUpdateChnlUsr.setTimestamp(1,BTSLUtil.getTimestampFromUtilDate(channelUserVO.getActivatedOn()));
                    // pstmtUpdateChnlUsr.setString(2,channelUserVO.getUserID());
                    updateCountChnlUsr = pstmtUpdateChnlUsr.executeUpdate();
                    if (updateCountChnlUsr <= 0) {
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
//                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
//                            "bulkuser.processuploadedfile.error.updatechannelusererr"));
                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), 
                        		RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.UPDATE_CHANNEL_USER_DETAIL_ERROR, null));
                        errorList.add(errorVO);
//                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.processuploadedfile.error.updatechannelusererr"));
                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.UPDATE_CHANNEL_USER_DETAIL_ERROR, null));
                        continue;
                    }

                    // for Zebra and Tango by Sanjeew date 10/07/07
                    paymentNumber = null;
                    /*
                     * if(ptupsMobqutyMergd)
                     * {
                     * if(autoPaymentMethod &&
                     * PretupsI.SELECT_CHECKBOX
                     * .equals(channelUserVO.getMcommerceServiceAllow()))
                     * {
                     * commonUtil=new CommonUtil();
                     * userMap=commonUtil.getMapfromUserVO(channelUserVO);
                     * //this map retun payment_method_number as key this
                     * payment_method_number show in message.
                     * paymentNumber=(String)commonUtil.assignPaymentMethod(p_con
                     * ,userMap).get("payment_method_number");
                     * }
                     * }
                     */
                    // End Zebra and Tango

                    updateCount++;
                    p_con.commit();
                    boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
                    boolean loginPasswordAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
                    boolean autoPaymentMethod = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD);
                    // push message in try catch(not for deleted/rejected users)
                    if (channelUserVO.getStatus().equals(PretupsI.USER_STATUS_ACTIVE) || channelUserVO.getStatus().equals(PretupsI.USER_STATUS_PREACTIVE)) {
                        if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                            if (loginPasswordAllowed) {// send
                                // message
                                // for
                                // both
                                // login
                                // id
                                // and
                                // sms
                                // pin
                                final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
                                    .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                                // for Zebra and Tango by Sanjeew date 11/07/07
                                if (ptupsMobqutyMergd) {
                                    if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                        arrArray[2] = paymentNumber;
                                    }
                                }
                                // End Zebra and Tango

                                // Email for pin & password
                                //subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });
                                subject = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOGIN_DETAILS_INFORMATION, new String[] { channelUserVO.getUserName() });

                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                            } else {
                                if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                                    // send message for both login id and sms
                                    // pin
                                    final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
                                        .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                                    // for Zebra and Tango by Sanjeew date
                                    // 11/07/07
                                    if (ptupsMobqutyMergd) {
                                        if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                            arrArray[2] = paymentNumber;
                                        }
                                    }
                                    // End Zebra and Tango
                                    //subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });
                                    subject = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOGIN_DETAILS_INFORMATION, new String[] { channelUserVO.getUserName() });
                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                                } else {
                                    // send message only for sms pin
                                    final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
                                        .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                                    // for Zebra and Tango by Sanjeew date
                                    // 11/07/07
                                    if (ptupsMobqutyMergd) {
                                        if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                            arrArray[2] = paymentNumber;
                                        }
                                    }
                                    // End Zebra and Tango
                                    //subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });
                                    subject = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOGIN_DETAILS_INFORMATION, new String[] { channelUserVO.getUserName() });
                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                                }
                            }
                        }

                        else if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && loginPasswordAllowed) {
                            // send message for login id
                            final String[] arrArray = { channelUserVO.getLoginID(), "" };

                            // for Zebra and Tango by Sanjeew date 11/07/07
                            if (ptupsMobqutyMergd) {
                                if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                    arrArray[1] = paymentNumber;
                                }
                            }
                            // End Zebra and Tango
                            // Email for pin & password
                            //subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });
                            subject = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOGIN_DETAILS_INFORMATION, new String[] { channelUserVO.getUserName() });
                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
                        } else if (TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                            // send message for sms pin
                            final String[] arrArray = { channelUserVO.getMsisdn(), "" };

                            // for Zebra and Tango by Sanjeew date 11/07/07
                            if (ptupsMobqutyMergd) {
                                if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                    arrArray[1] = paymentNumber;
                                }
                            }
                            // End Zebra and Tango
                            // Email for pin & Password
                            //subject = p_messages.getMessage(p_locale, "subject.user.regmsidn.massage", new String[] { channelUserVO.getMsisdn() });
                            subject = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.NEW_PIN_DETAILS, new String[] { channelUserVO.getUserName() });
                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                        }

                        pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", defaultLocale, p_userVO.getNetworkID());
                        pushMessage.push();
                        // Email for pin & password- email change
                        boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
                        if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                            emailSendToUser = new EmailSendToUser(subject, btslPushMessage, p_locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
                                channelUserVO, p_userVO);
                            emailSendToUser.sendMail();
                        }
                    }

                    BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Success");
                    ChannelUserLog.log("BULKUSRAPP", channelUserVO, p_userVO, true, "Success Batch user approval");
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
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "",
                        "", "SQL Exception:" + sqe.getMessage());
//                    errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "error.general.sql.processing"));
                    errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TECHNICAL_ERROR, null));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TECHNICAL_ERROR, null));
                    continue;
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
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "",
                        "", "Exception:" + ex.getMessage());
                    //errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "error.general.processing"));
                    errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TECHNICAL_ERROR, null));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TECHNICAL_ERROR, null));
                    continue;
                }

            }
            errorVO = new ListValueVO("UPDATECOUNT", String.valueOf(warnings), String.valueOf(updateCount));
            errorList.add(errorVO);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            // close result set
            try {
                if (rsSelectParentUsrStatus != null) {
                    rsSelectParentUsrStatus.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList size =" + errorList.size() + " updateCount" + updateCount);
            }
        }
        return errorList;
    }
    
    
    
    
    
    /**
     * Method :updateUserForApproval
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userVO
     *            TODO
     * @param p_initiatorUserType
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Anand Swaraj
     */
    public ArrayList updateUserForBatchApproval(Connection p_con, ArrayList p_userDetails, Locale p_locale, UserVO p_userVO, String p_initiatorUserType) throws BTSLBaseException {
        final String methodName = "updateUserForBatchApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userDetails.size()=" + p_userDetails.size()  + " p_locale=" + p_locale + " p_userVO=" + p_userVO
                .toString() + " p_initiatorUserType " + p_initiatorUserType);
        }
        
        ResultSet rsSelectParentUsrStatus = null;
        final StringBuffer selectParentStatus = new StringBuffer("SELECT UP.status FROM users U, users UP	WHERE U.user_id=? AND U.parent_id=UP.user_id");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectParentStatus=" + selectParentStatus);
        }
        final StringBuffer strBuffUsr = new StringBuffer(" UPDATE users SET level1_approved_by=?, level1_approved_on=?,");
        strBuffUsr.append(" modified_by=?, modified_on=?, status=?, previous_status=?,remarks=?, login_id=?, rsaflag=?,ssn=? WHERE user_id=?");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY strBuffUsr=" + strBuffUsr);
        }
        // modified by shashank
        final StringBuffer strBuffChnlUsr = new StringBuffer(" UPDATE channel_users SET activated_on=? ");
        // if(p_initiatorUserType.equals(PretupsI.CHANNEL_USER_TYPE) &&
        // !SystemPreferences.BATCH_USER_PROFILE_ASSIGN)
        // {
        strBuffChnlUsr.append(" ,user_grade=?, TRANSFER_PROFILE_ID=?, COMM_PROFILE_SET_ID=?, ");
        strBuffChnlUsr.append(" mcommerce_service_allow=?, mpay_profile_id=?, trf_rule_type=?");
        // }
        strBuffChnlUsr.append(" WHERE user_id=? ");
        //
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY strBuffChnlUsr=" + strBuffChnlUsr);
        }
        final ArrayList errorList = new ArrayList();
        int updateCount = 0;
        try(PreparedStatement  pstmtUpdateUsr = p_con.prepareStatement(strBuffUsr.toString());
        		PreparedStatement pstmtUpdateChnlUsr = p_con.prepareStatement(strBuffChnlUsr.toString());
        		PreparedStatement  pstmtSelectParentUsrStatus = p_con.prepareStatement(selectParentStatus.toString());) {
            
            ChannelUserVO channelUserVO = null;
            int updateCountUsr = 0;
            int updateCountChnlUsr = 0;
            ListValueVO errorVO = null;
            int warnings = 0;
            String msg = null;
            String parentStatus = null;
            CategoryVO categoryVO = null;
            PushMessage pushMessage = null;
            BTSLMessages btslPushMessage = null;
            String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
            String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
            final Locale defaultLocale = new Locale(defaultLanguage, defaultCountry);
            final Map userMap = null;
            String paymentNumber = null;
            final CommonUtil commonUtil = null;
            // Email for pin & Password
            EmailSendToUser emailSendToUser = null;
            String subject = null;
            for (int i = 0, j = p_userDetails.size(); i < j; i++) {
                try {
                    updateCountUsr = 0;
                    updateCountChnlUsr = 0;
                    channelUserVO = (ChannelUserVO) p_userDetails.get(i);
                    categoryVO = channelUserVO.getCategoryVO();
                    pstmtSelectParentUsrStatus.clearParameters();
                    pstmtSelectParentUsrStatus.setString(1, channelUserVO.getUserID());
                    rsSelectParentUsrStatus = pstmtSelectParentUsrStatus.executeQuery();
                    if (rsSelectParentUsrStatus.next() && categoryVO.getSequenceNumber() != 1) {
                        parentStatus = rsSelectParentUsrStatus.getString("status");
                        if (parentStatus.equals(PretupsI.USER_STATUS_DELETED)) {
                            channelUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
//                            msg = p_messages.getMessage(p_locale, "bulkuser.processuploadedfile.error.parentnotactivedelete", channelUserVO.getUserName());
                            msg = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_ACTIVE_DELETE, 
                            		new String[] {channelUserVO.getUserName()});
                            channelUserVO.setRemarks(msg);
                            errorVO = new ListValueVO("WARNING", channelUserVO.getRecordNumber(), msg);
                            errorList.add(errorVO);
                            warnings++;
//                            BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Success with warning :=" + p_messages.getMessage(
//                                "bulkuser.processuploadedfile.error.parentnotactivedelete", channelUserVO.getUserName()));
                            BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Success with warning :=" + 
                            		RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_ACTIVE_DELETE,
                            				new String[] {channelUserVO.getUserName()}));
                        }

                        if (channelUserVO.getStatus().equals(PretupsI.USER_STATUS_DELETED) || parentStatus.equals(PretupsI.USER_STATUS_ACTIVE)) {
                            pstmtUpdateUsr.setString(5, channelUserVO.getStatus());
                            pstmtUpdateUsr.setString(7, channelUserVO.getRemarks());
                        } else {
                            //msg = p_messages.getMessage(p_locale, "bulkuser.processuploadedfile.error.parentnotactivediscard", channelUserVO.getUserName());
                        	msg = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_ACTIVE_DISCARD, 
                            		new String[] {channelUserVO.getUserName()});
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), msg);
                            errorList.add(errorVO);
//                            BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.processuploadedfile.error.parentnotactivediscard",
//                                channelUserVO.getUserName()));
                            BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + 
                            		RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_ACTIVE_DISCARD, 
                                    		new String[] {channelUserVO.getUserName()}));
                            continue;
                        }
                    } else if (categoryVO.getSequenceNumber() == 1)// parent
                    // (owner
                    // user)
                    {
                        pstmtUpdateUsr.setString(5, channelUserVO.getStatus());
                        pstmtUpdateUsr.setString(7, channelUserVO.getRemarks());
                    } else {
//                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
//                            "bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                    	errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(),
                    			RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_EXIST, null));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + 
                        		RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_EXIST, null));
                        continue;
                    }
                    if (channelUserVO.getStatus().trim().equalsIgnoreCase(PretupsI.USER_STATUS_DELETED)) {
                        channelUserVO.setLoginID(channelUserVO.getUserID());
                    }
                    pstmtUpdateUsr.setString(1, channelUserVO.getLevel1ApprovedBy());
                    pstmtUpdateUsr.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLevel1ApprovedOn()));
                    pstmtUpdateUsr.setString(3, channelUserVO.getModifiedBy());
                    pstmtUpdateUsr.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                    pstmtUpdateUsr.setString(6, channelUserVO.getPreviousStatus());
                    pstmtUpdateUsr.setString(8, channelUserVO.getLoginID());
                    // added by shashank for rsa
                    pstmtUpdateUsr.setString(9, channelUserVO.getRsaFlag());
                    pstmtUpdateUsr.setString(10, channelUserVO.getSsn());
                    // end
                    pstmtUpdateUsr.setString(11, channelUserVO.getUserID());
                    updateCountUsr = pstmtUpdateUsr.executeUpdate();
                    pstmtUpdateUsr.clearParameters();
                    if (updateCountUsr <= 0) {
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
//                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages
//                            .getMessage(p_locale, "bulkuser.processuploadedfile.error.updateusererr"));
                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), 
                        		RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.UPDATE_USER_DETAIL_ERROR, null));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.UPDATE_USER_DETAIL_ERROR, null));
                        continue;
                    }
                    pstmtUpdateChnlUsr.clearParameters();
                    int k = 1;
                    pstmtUpdateChnlUsr.setTimestamp(k, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getActivatedOn()));
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getUserGrade());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getTransferProfileID());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getCommissionProfileSetID());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getMcommerceServiceAllow());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getMpayProfileID());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getTrannferRuleTypeId());
                    pstmtUpdateChnlUsr.setString(++k, channelUserVO.getUserID());

                    // pstmtUpdateChnlUsr.setTimestamp(1,BTSLUtil.getTimestampFromUtilDate(channelUserVO.getActivatedOn()));
                    // pstmtUpdateChnlUsr.setString(2,channelUserVO.getUserID());
                    updateCountChnlUsr = pstmtUpdateChnlUsr.executeUpdate();
                    if (updateCountChnlUsr <= 0) {
                        try {
                            if (p_con != null) {
                                p_con.rollback();
                            }
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
//                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
//                            "bulkuser.processuploadedfile.error.updatechannelusererr"));
                        errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), 
                        		RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.UPDATE_CHANNEL_USER_DETAIL_ERROR, null));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.UPDATE_CHANNEL_USER_DETAIL_ERROR, null));
                        continue;
                    }

                    // for Zebra and Tango by Sanjeew date 10/07/07
                    paymentNumber = null;
                    /*
                     * if(ptupsMobqutyMergd)
                     * {
                     * if(autoPaymentMethod &&
                     * PretupsI.SELECT_CHECKBOX
                     * .equals(channelUserVO.getMcommerceServiceAllow()))
                     * {
                     * commonUtil=new CommonUtil();
                     * userMap=commonUtil.getMapfromUserVO(channelUserVO);
                     * //this map retun payment_method_number as key this
                     * payment_method_number show in message.
                     * paymentNumber=(String)commonUtil.assignPaymentMethod(p_con
                     * ,userMap).get("payment_method_number");
                     * }
                     * }
                     */
                    // End Zebra and Tango

                    updateCount++;
                    p_con.commit();
                    boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
                    boolean loginPasswordAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
                    boolean autoPaymentMethod = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD);
                    // push message in try catch(not for deleted/rejected users)
                    if (channelUserVO.getStatus().equals(PretupsI.USER_STATUS_ACTIVE) || channelUserVO.getStatus().equals(PretupsI.USER_STATUS_PREACTIVE)) {
                        if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                            if (loginPasswordAllowed) {// send
                                // message
                                // for
                                // both
                                // login
                                // id
                                // and
                                // sms
                                // pin
                                final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
                                    .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                                // for Zebra and Tango by Sanjeew date 11/07/07
                                if (ptupsMobqutyMergd) {
                                    if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                        arrArray[2] = paymentNumber;
                                    }
                                }
                                // End Zebra and Tango

                                // Email for pin & password
//                                subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });
                                subject = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOGIN_DETAILS_INFORMATION, new String[] { channelUserVO.getUserName() });

                                btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                            } else {
                                if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                                    // send message for both login id and sms
                                    // pin
                                    final String[] arrArray = { channelUserVO.getLoginID(), channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
                                        .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                                    // for Zebra and Tango by Sanjeew date
                                    // 11/07/07
                                    if (ptupsMobqutyMergd) {
                                        if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                            arrArray[2] = paymentNumber;
                                        }
                                    }
                                    // End Zebra and Tango
                                    //subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });
                                    subject = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOGIN_DETAILS_INFORMATION, new String[] { channelUserVO.getUserName() });
                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arrArray);
                                } else {
                                    // send message only for sms pin
                                    final String[] arrArray = { channelUserVO.getMsisdn(), "", BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil
                                        .decryptText(channelUserVO.getPrimaryMsisdnPin()) };

                                    // for Zebra and Tango by Sanjeew date
                                    // 11/07/07
                                    if (ptupsMobqutyMergd) {
                                        if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                            arrArray[2] = paymentNumber;
                                        }
                                    }
                                    // End Zebra and Tango
                                    //subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });
                                    subject = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOGIN_DETAILS_INFORMATION, new String[] { channelUserVO.getUserName() });
                                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                                }
                            }
                        }

                        else if (TypesI.YES.equals(categoryVO.getWebInterfaceAllowed()) && loginPasswordAllowed) {
                            // send message for login id
                            final String[] arrArray = { channelUserVO.getLoginID(), "" };

                            // for Zebra and Tango by Sanjeew date 11/07/07
                            if (ptupsMobqutyMergd) {
                                if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                    arrArray[1] = paymentNumber;
                                }
                            }
                            // End Zebra and Tango
                            // Email for pin & password
                            //subject = p_messages.getMessage(p_locale, "subject.channeluser.all.massage", new String[] { channelUserVO.getUserName() });
                            subject = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOGIN_DETAILS_INFORMATION, new String[] { channelUserVO.getUserName() });

                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_ACTIVATE, arrArray);
                        } else if (TypesI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                            // send message for sms pin
                            final String[] arrArray = { channelUserVO.getMsisdn(), "" };

                            // for Zebra and Tango by Sanjeew date 11/07/07
                            if (ptupsMobqutyMergd) {
                                if (autoPaymentMethod && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow())) {
                                    arrArray[1] = paymentNumber;
                                }
                            }
                            // End Zebra and Tango
                            // Email for pin & Password
                            //subject = p_messages.getMessage(p_locale, "subject.user.regmsidn.massage", new String[] { channelUserVO.getMsisdn() });
                            subject = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.NEW_PIN_DETAILS, new String[] { channelUserVO.getMsisdn() });
                            btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arrArray);
                        }

                        pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslPushMessage, "", "", defaultLocale, p_userVO.getNetworkID());
                        pushMessage.push();
                        // Email for pin & password- email change
                        boolean isEmailServiceAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
                        if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                            emailSendToUser = new EmailSendToUser(subject, btslPushMessage, p_locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
                                channelUserVO, p_userVO);
                            emailSendToUser.sendMail();
                        }
                    }

                    BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Success");
                    ChannelUserLog.log("BULKUSRAPP", channelUserVO, p_userVO, true, "Success Batch user approval");
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
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "",
                        "", "SQL Exception:" + sqe.getMessage());
                    //errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "error.general.sql.processing"));
                    errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, null));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, null));
                    continue;
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
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "",
                        "", "Exception:" + ex.getMessage());
                    //errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "error.general.processing"));
                    errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, null));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRAPP", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, null));
                    continue;
                }

            }
            errorVO = new ListValueVO("UPDATECOUNT", String.valueOf(warnings), String.valueOf(updateCount));
            errorList.add(errorVO);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[updateUserForApproval]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            // close result set
            try {
                if (rsSelectParentUsrStatus != null) {
                    rsSelectParentUsrStatus.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList size =" + errorList.size() + " updateCount" + updateCount);
            }
        }
        return errorList;
    }
    
    
    
    
    /**
     * Method :loadUserIdFromCreatedByField
     * This method load cratedy by from Batches table.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_geographyCode
     *            java.lang.String
     * @param p_category_code
     *            java.lang.String
     * @param p_domainType
     *            java.lang.String
     * @param p_groupRoleAllowed
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author anand.swaraj
     */

    public String loadUserIdFromCreatedByField(Connection p_con, String b_batch_id) throws BTSLBaseException {
        final String methodName = "loadUserIdFromCreatedByField";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: b_batch_id=" + b_batch_id);
        }
         
        ResultSet rs = null;
        String created_by = null;
        final StringBuffer strBuffRoles = new StringBuffer("SELECT B.created_by FROM batches B ");
        strBuffRoles.append(" WHERE B.batch_id=? ");
        final String sqlSelectRoles = strBuffRoles.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectRoles=" + sqlSelectRoles);
        }
        final ArrayList roleList = new ArrayList();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelectRoles);){
           
            int i = 1;
            pstmtSelect.setString(i++, b_batch_id);
            rs = pstmtSelect.executeQuery();

            
            while (rs.next()) {
            	created_by = rs.getString("created_by");
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterGroupRoleList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadMasterGroupRoleList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterGroupRoleList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadMasterGroupRoleList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: roleList size =" + roleList.size());
            }
        }
        return created_by;
    }
    
}
