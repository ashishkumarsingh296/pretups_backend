package com.restapi.c2s.services.repository;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.*;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.GetParentOwnerProfileReq;
import com.btsl.pretups.channel.transfer.requesthandler.GetParentOwnerProfileRespVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.DailyReportAnalysisQry;
import com.btsl.user.businesslogic.UserQry;
import com.btsl.util.BTSLUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RestUserDAO {
	

    public static boolean flag = false;
    private static final String EXCEPTION = "EXCEPTION: ";
    private static final String SQL_EXCEPTION = "SQL EXCEPTION: ";
    private static final String QUERY_KEY = "Query: ";
    
	private final Log LOG = LogFactory.getLog(this.getClass().getName());
    private UserQry userQry = (UserQry)ObjectProducer.getObject(QueryConstants.USER_QRY, QueryConstants.QUERY_PRODUCER);
    private DailyReportAnalysisQry dailyRptAnalysiQry = (DailyReportAnalysisQry)ObjectProducer.getObject(QueryConstants.DAILY_REPORT_ANALYSIS, QueryConstants.QUERY_PRODUCER);
	
    public GetParentOwnerProfileRespVO getParentOwnerProfileInfo(java.sql.Connection con, GetParentOwnerProfileReq getParentOwnerProfileReq) throws BTSLBaseException {
	    	final String methodName = "getParentOwnerProfileInfo";
	    	StringBuffer msg=new StringBuffer("");
	    	StringBuffer loggerValue=new StringBuffer("");
	    	GetParentOwnerProfileRespVO getParentOwnerProfileRespVO =null;
	        if (LOG.isDebugEnabled())
	        {
	        	msg.append("getParentOwnerProfileReq():: Entered with getParentOwnerProfileReq:");
	        	msg.append(getParentOwnerProfileReq.toString());
	        	String message = msg.toString();
	        	LOG.debug(methodName, message);
	        }
	     
	        PreparedStatement pstmt = null;
	        String sqlSelect = this.getParentOwnerInfo();
	      
	        if(LOG.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(sqlSelect.toString());
				LOG.debug(methodName, loggerValue);
			}
	        
	        try {
	        	pstmt = con.prepareStatement(sqlSelect.toString());
	        	int i = 0;
	         	++i;
	        	pstmt.setString(i, getParentOwnerProfileReq.getUserId());
	        	++i;
	        	pstmt.setString(i, getParentOwnerProfileReq.getUserId());
	        	
	            try(ResultSet rs = pstmt.executeQuery();)
	            	{
	            	
	            		if(rs.next()) {
	            			getParentOwnerProfileRespVO=new GetParentOwnerProfileRespVO();
	            			getParentOwnerProfileRespVO.setUserName(rs.getString("USER_NAME"));
	            			getParentOwnerProfileRespVO.setStatus(rs.getString("status"));
	            			getParentOwnerProfileRespVO.setMsisdn(rs.getString("msisdn"));
	            			getParentOwnerProfileRespVO.setGrade(rs.getString("grade"));
	            			getParentOwnerProfileRespVO.setEmailID(rs.getString("emailID"));
	            			
	                       	StringBuilder sb = new StringBuilder();
	                      	 if(!BTSLUtil.isNullString(rs.getString("address1")) ) {
	                      		sb.append(rs.getString("address1"));
	                      		sb.append(PretupsI.COMMA);
	                      	  }
	                      	if(!BTSLUtil.isNullString(rs.getString("address2")) ) {
	                      		sb.append(rs.getString("address2"));
	                      		sb.append(PretupsI.COMMA);
	                      	  }

	                   	if(!BTSLUtil.isNullString(rs.getString("city")) ) {
	                      		sb.append(rs.getString("city"));
	                      		sb.append(PretupsI.COMMA);
	                      	  }
	                   	if(!BTSLUtil.isNullString(rs.getString("state")) ) {
	                      		sb.append(rs.getString("state"));
	                      		sb.append(PretupsI.COMMA);
	                      	  }
	                   	if(!BTSLUtil.isNullString(rs.getString("country")) ) {
	                      		sb.append(rs.getString("country"));
	                      		sb.append(PretupsI.COMMA);
	                      	  }
	                   	String address="";
	                	if(sb.toString().length()>0) {
	    	              	int lastloc =sb.toString().length()-1;
	    	               	 address = sb.deleteCharAt(lastloc).toString();
	                	}
	                   	
	            			getParentOwnerProfileRespVO.setAddress(address);
	            			getParentOwnerProfileRespVO.setParentName(rs.getString("parent_name"));
	            			getParentOwnerProfileRespVO.setParentUserID(rs.getString("PARENTUSERID"));
	            			getParentOwnerProfileRespVO.setParentMobileNumber(rs.getString("parent_msisdn"));
	            			getParentOwnerProfileRespVO.setParentCategoryName(rs.getString("Parent_category_name"));
	            			getParentOwnerProfileRespVO.setOwnerName(rs.getString("owner_name"));
	            			getParentOwnerProfileRespVO.setOwnerMobileNumber(rs.getString("owner_msisdn"));
	            			getParentOwnerProfileRespVO.setOwnerCategoryName(rs.getString("Owner_Category"));
	            			getParentOwnerProfileRespVO.setUserNamePrefix(rs.getString("USER_NAME_PREFIX"));
	            			getParentOwnerProfileRespVO.setShortName(rs.getString("SHORT_NAME"));
	            		}
	            	}
	        }// end of try
	        catch (SQLException sqle) {
	        	msg.setLength(0);
	        	msg.append(SQL_EXCEPTION);
	        	msg.append(sqle.getMessage());
				LOG.error(methodName, msg);
	            LOG.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
	        }// end of catch
	        catch (Exception e) {
	        	msg.setLength(0);
	        	msg.append(EXCEPTION);
	        	msg.append(e.getMessage());
				LOG.error(methodName, msg);
	            LOG.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
	        }// end of catch
	        finally {
	        	try{
	                if (pstmt!= null){
	                	pstmt.close();
	                }
	              }
	              catch (SQLException e){
	            	  LOG.error("An error occurred closing prepared statement.", e);
	              }
	        	}
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(methodName, "GetParentOwnerProfileRespVO");
	            }
	            
	            return getParentOwnerProfileRespVO;
	    }  


    public GetParentOwnerProfileRespVO getParentOwnerProfileInfoForAllUsers(java.sql.Connection con, GetParentOwnerProfileReq getParentOwnerProfileReq) throws BTSLBaseException {
    	final String methodName = "getParentOwnerProfileInfo";
    	StringBuffer msg=new StringBuffer("");
    	StringBuffer loggerValue=new StringBuffer("");
    	GetParentOwnerProfileRespVO getParentOwnerProfileRespVO =null;
        if (LOG.isDebugEnabled())
        {
        	msg.append("getParentOwnerProfileReq():: Entered with getParentOwnerProfileReq:");
        	msg.append(getParentOwnerProfileReq.toString());
        	String message = msg.toString();
        	LOG.debug(methodName, message);
        }
     
        PreparedStatement pstmt = null;
        String sqlSelect = this.getParentOwnerInfoForAllUsers();
      
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect.toString());
			LOG.debug(methodName, loggerValue);
		}
        
        try {
        	pstmt = con.prepareStatement(sqlSelect.toString());
        	int i = 0;
         	++i;
        	pstmt.setString(i, getParentOwnerProfileReq.getUserId());
        	++i;
        	pstmt.setString(i, getParentOwnerProfileReq.getUserId());
        	
            try(ResultSet rs = pstmt.executeQuery();)
            	{
            	
            		if (rs.next()) {
            			getParentOwnerProfileRespVO=new GetParentOwnerProfileRespVO();
            			getParentOwnerProfileRespVO.setUserName(rs.getString("USER_NAME"));
            			getParentOwnerProfileRespVO.setStatus(rs.getString("status"));
            			getParentOwnerProfileRespVO.setMsisdn(rs.getString("msisdn"));
            		//	getParentOwnerProfileRespVO.setGrade(rs.getString("grade"));
            			getParentOwnerProfileRespVO.setEmailID(rs.getString("emailID"));
            			
                       	StringBuilder sb = new StringBuilder();
                      	 if(!BTSLUtil.isNullString(rs.getString("address1")) ) {
                      		sb.append(rs.getString("address1"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                      	if(!BTSLUtil.isNullString(rs.getString("address2")) ) {
                      		sb.append(rs.getString("address2"));
                      		sb.append(PretupsI.COMMA);
                      	  }

                   	if(!BTSLUtil.isNullString(rs.getString("city")) ) {
                      		sb.append(rs.getString("city"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                   	if(!BTSLUtil.isNullString(rs.getString("state")) ) {
                      		sb.append(rs.getString("state"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                   	if(!BTSLUtil.isNullString(rs.getString("country")) ) {
                      		sb.append(rs.getString("country"));
                      		sb.append(PretupsI.COMMA);
                      	  }
                   	String address="";
                	if(sb.toString().length()>0) {
    	              	int lastloc =sb.toString().length()-1;
    	               	 address = sb.deleteCharAt(lastloc).toString();
                	}
                   	
            			
            			
            			getParentOwnerProfileRespVO.setAddress(address);
            			getParentOwnerProfileRespVO.setParentName(rs.getString("parent_name"));
            			getParentOwnerProfileRespVO.setParentUserID(rs.getString("PARENTUSERID"));
            			getParentOwnerProfileRespVO.setParentMobileNumber(rs.getString("parent_msisdn"));
            			getParentOwnerProfileRespVO.setParentCategoryName(rs.getString("Parent_category_name"));
            			getParentOwnerProfileRespVO.setOwnerName(rs.getString("owner_name"));
            			getParentOwnerProfileRespVO.setOwnerMobileNumber(rs.getString("owner_msisdn"));
            			getParentOwnerProfileRespVO.setOwnerCategoryName(rs.getString("Owner_Category"));
            			getParentOwnerProfileRespVO.setUserNamePrefix(rs.getString("USER_NAME_PREFIX"));
            			getParentOwnerProfileRespVO.setShortName(rs.getString("SHORT_NAME"));
            		}
            	}
        }// end of try
        catch (SQLException sqle) {
        	msg.setLength(0);
        	msg.append(SQL_EXCEPTION);
        	msg.append(sqle.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	msg.setLength(0);
        	msg.append(EXCEPTION);
        	msg.append(e.getMessage());
			LOG.error(methodName, msg);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[loadUserDetailsFormUserID]", "", "", "", msg.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing prepared statement.", e);
              }
        	}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "GetParentOwnerProfileRespVO");
            }
            
            return getParentOwnerProfileRespVO;
    }  
	public String getParentOwnerInfoForAllUsers() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append ( " SELECT loggedinUser.user_id loginUserID, ");
	    strBuff.append ( " loggedinUser.status  AS status, ");
	    strBuff.append ( " ");
	    strBuff.append ( " loggedinUser.email          AS emailID, ");
	    strBuff.append ( " loggedinUser.address1 as address1,");
	    strBuff.append ( " loggedinUser.address2 as address2, ");
	    strBuff.append ( " loggedinUser.state as state, ");
	    strBuff.append ( " loggedinUser.city  as city, ");
	    strBuff.append ( " loggedinUser.country  as country, ");
	    strBuff.append (  " loggedinUser.external_code  AS ERPCODE, ");
	    strBuff.append (  " loggedinUser.category_code  AS category_Code, ");
	    strBuff.append (  " loggedinUser.msisdn         AS msisdn, ");
	    strBuff.append (  " loggedinUser.user_name      USER_NAME, ");
	    strBuff.append (  " loggedinUser.user_name_prefix      USER_NAME_PREFIX, ");
	    strBuff.append (  " loggedinUser.short_name      SHORT_NAME, ");
	    strBuff.append (  " CASE loggedinUser.PARENT_ID WHEN  'ROOT'   THEN  'ROOT'    ELSE PU.user_id   END   AS  PARENTUSERID, ");
	    strBuff.append (  " PU.user_name    AS parent_name, ");
	    strBuff.append (  " PU.msisdn as  parent_msisdn, ");
	    strBuff.append (  " PC.category_name      AS  Parent_category_name , ");
	    strBuff.append (  " OU.user_id   as    OwnerUserID, ");
	    strBuff.append (  " OU.user_name     AS owner_name, ");
	    strBuff.append (  " OU.msisdn        AS owner_msisdn, ");
	    strBuff.append (  " OC.category_name AS Owner_Category ");
	    strBuff.append (  " FROM   USERS loggedinUser , ");
	    strBuff.append (  " ");
	    strBuff.append (  " USERS PU , ");
	    strBuff.append (  " users OU, ");
	    strBuff.append (  " categories PC, ");
	    strBuff.append (  " categories OC ");
	    strBuff.append (  " WHERE  loggedinUser.user_id = ? ");
	    strBuff.append (  " ");
	    strBuff.append (  " AND  PU.USER_ID = CASE WHEN loggedinUser.PARENT_ID='SYSTEM'   THEN  ?    ELSE loggedinUser.PARENT_ID  END ");
	    strBuff.append (  " AND OU.user_id = loggedinUser.owner_id ");
	    strBuff.append (  " AND PC.category_code = PU.category_code ");
	    strBuff.append (  " AND OC.category_code = OU.category_code ");
		return strBuff.toString();
	}

		
	
	public String getParentOwnerInfo() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append ( " SELECT loggedinUser.user_id loginUserID, ");
	    strBuff.append ( " loggedinUser.status  AS status, ");
	    strBuff.append ( " cu.user_grade    AS grade, ");
	    strBuff.append ( " loggedinUser.email          AS emailID, ");
	    strBuff.append ( " loggedinUser.address1 as address1,");
	    strBuff.append ( " loggedinUser.address2 as address2, ");
	    strBuff.append ( " loggedinUser.state as state, ");
	    strBuff.append ( " loggedinUser.city  as city, ");
	    strBuff.append ( " loggedinUser.country  as country, ");
	    strBuff.append (  " loggedinUser.external_code  AS ERPCODE, ");
	    strBuff.append (  " loggedinUser.category_code  AS category_Code, ");
	    strBuff.append (  " loggedinUser.msisdn         AS msisdn, ");
	    strBuff.append (  " loggedinUser.user_name      USER_NAME, ");
	    strBuff.append (  " loggedinUser.user_name_prefix      USER_NAME_PREFIX, ");
	    strBuff.append (  " loggedinUser.short_name      SHORT_NAME, ");
	    strBuff.append (  " CASE loggedinUser.PARENT_ID WHEN  'ROOT'   THEN  'ROOT'    ELSE PU.user_id   END   AS  PARENTUSERID, ");
	    strBuff.append (  " PU.user_name    AS parent_name, ");
	    strBuff.append (  " PU.msisdn      as  parent_msisdn, ");
	    strBuff.append (  " PC.category_name      AS  Parent_category_name , ");
	    strBuff.append (  " OU.user_id   as    OwnerUserID, ");
	    strBuff.append (  " OU.user_name     AS owner_name, ");
	    strBuff.append (  " OU.msisdn        AS owner_msisdn, ");
	    strBuff.append (  " OC.category_name AS Owner_Category ");
	    strBuff.append (  " FROM   USERS loggedinUser , ");
	    strBuff.append (  " channel_users cu, ");
	    strBuff.append (  " USERS PU , ");
	    strBuff.append (  " users OU, ");
	    strBuff.append (  " categories PC, ");
	    strBuff.append (  " categories OC ");
	    strBuff.append (  " WHERE  loggedinUser.user_id = ? ");
	    strBuff.append (  " AND cu.user_id =loggedinUser.user_id ");
	    strBuff.append (  " AND  PU.USER_ID = CASE WHEN loggedinUser.PARENT_ID='ROOT'   THEN  ?    ELSE loggedinUser.PARENT_ID  END ");
	    strBuff.append (  " AND OU.user_id = loggedinUser.owner_id ");
	    strBuff.append (  " AND PC.category_code = PU.category_code ");
	    strBuff.append (  " AND OC.category_code = OU.category_code ");
		return strBuff.toString();

	}

}
