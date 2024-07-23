package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.channel.reports.web.UsersReportModel;

public class AdditionalCommissionSummaryReportDAO {
	public static final Log log = LogFactory.getLog(AdditionalCommissionSummaryReportDAO.class.getName());
	private AdditionalCommissionSummaryReportQry additionalCommissionSummaryReportQry;
	private static final String SQLEXCEPTION = "SQLException : ";
	private static final String BTSLEXCEPTION = "BTSLException: ";
	private static final String SQLEXCEPTIONKEY = "error.general.sql.processing";
	private static final String SERVICE_TYPE_NAME = "service_type_name";
	private static final String USERNAME = "user_name";
	private static final String MSISDN = "msisdn";
	private static final String CATEGORY_NAME = "category_name";
	private static final String GRPH_DOMAIN_NAME = "grph_domain_name";
	private static final String PARENT_NAME = "parent_name";
	private static final String PARENT_MSISDN = "parent_msisdn";
	private static final String PARENT_CAT = "parent_cat";
	private static final String PARENT_GEO = "parent_geo";
	private static final String OWNER_NAME = "owner_name";
	private static final String OWNER_MSISDN = "owner_msisdn";
	private static final String OWNER_GEO = "owner_geo";
	private static final String OWNER_CAT = "owner_category";
	private static final String SELECTOR_NAME = "selector_name";
	private static final String TRANSACTION_COUNT = "transaction_count";
	private static final String DIFFERENTIAL_AMOUNT = "differential_amount";
	private static final String TRANS_DATE = "trans_date";
	private static final String LOGIN_ID = "login_id";
	private static final String GRAND_NAME = "grand_name";
	private static final String GRAND_MSISDN = "grand_msisdn";
	private static final String GRAND_CATEGORY = "grand_category";
	private static final String GRAND_GEO_DOMAIN = "grand_geo_domain";
	
	public AdditionalCommissionSummaryReportDAO(){
		additionalCommissionSummaryReportQry = (AdditionalCommissionSummaryReportQry) ObjectProducer.getObject(QueryConstants.ADDITIONAL_COMMISSION_SUMMARY_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
	}
	
	public ArrayList<ChannelTransferVO> loadAdditionalCommisionOpeartorDailySummary(Connection con, UsersReportModel usersReportModel){
		final String methodName = "loadAdditionalCommisionOpeartorDetails";
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.ENTERED);
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null ;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try
		{
			pstmt = additionalCommissionSummaryReportQry.loadAdditionalCommisionDetailsOperatorDailyQry(con,usersReportModel);

			rs = pstmt.executeQuery();
			while(rs.next())
			{
				transferVO = new ChannelTransferVO();
				transferVO.setTransferDateAsString(rs.getString(TRANS_DATE));
				transferVO.setLoginID(rs.getString(LOGIN_ID));
				transferVO.setUserName(rs.getString(USERNAME));
				transferVO.setMsisdn(rs.getString(MSISDN));
				transferVO.setCategoryName(rs.getString(CATEGORY_NAME));
				transferVO.setGegoraphyDomainName(rs.getString(GRPH_DOMAIN_NAME));
				transferVO.setParentName(rs.getString(PARENT_NAME));
				transferVO.setParentMsisdn(rs.getString(PARENT_MSISDN));
				transferVO.setParentCategory(rs.getString(PARENT_CAT));
				transferVO.setParentGeoName(rs.getString(PARENT_GEO));
				transferVO.setOwnerUser(rs.getString(OWNER_NAME));
				transferVO.setOwnerMsisdn(rs.getString(OWNER_MSISDN));
				transferVO.setOwnerCat(rs.getString(OWNER_CAT));
				transferVO.setOwnerGeo(rs.getString(OWNER_GEO));
				transferVO.setServiceTypeName(rs.getString(SERVICE_TYPE_NAME));
				transferVO.setSelectorName(rs.getString(SELECTOR_NAME));
				transferVO.setTransactionCount(rs.getString(TRANSACTION_COUNT));
				transferVO.setDifferentialAmount(rs.getString(DIFFERENTIAL_AMOUNT));
				transfersList.add(transferVO);
			}
		}
		catch(SQLException sqe){
			log.error(methodName, SQLEXCEPTION + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, SQLEXCEPTION + sqe.getMessage());
			try {
				throw new BTSLBaseException(this, "", SQLEXCEPTIONKEY,sqe);
			} catch (BTSLBaseException e) {
				log.error(methodName, BTSLEXCEPTION+e);
			}
		}
		finally{
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
		}
		return transfersList;
	}
	
	
	public ArrayList<ChannelTransferVO> loadAdditionalCommisionOpeartorMonthlySummary(Connection con, UsersReportModel usersReportModel){
		final String methodName = "loadAdditionalCommisionOpeartorOldDetails";
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.ENTERED);
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null ;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try
		{
			pstmt = additionalCommissionSummaryReportQry.loadAdditionalCommisionDetailsOperatorMonthlyQry(con, usersReportModel);

			rs = pstmt.executeQuery();
			while(rs.next())
			{
				transferVO = new ChannelTransferVO();
				transferVO.setTransferDateAsString(rs.getString(TRANS_DATE));
				transferVO.setLoginID(rs.getString(LOGIN_ID));
				transferVO.setUserName(rs.getString(USERNAME));
				transferVO.setMsisdn(rs.getString(MSISDN));
				transferVO.setCategoryName(rs.getString(CATEGORY_NAME));
				transferVO.setGegoraphyDomainName(rs.getString(GRPH_DOMAIN_NAME));
				transferVO.setParentName(rs.getString(PARENT_NAME));
				transferVO.setParentMsisdn(rs.getString(PARENT_MSISDN));
				transferVO.setParentCategory(rs.getString(PARENT_CAT));
				transferVO.setParentGeoName(rs.getString(PARENT_GEO));
				transferVO.setOwnerUser(rs.getString(OWNER_NAME));
				transferVO.setOwnerMsisdn(rs.getString(OWNER_MSISDN));
				transferVO.setOwnerCat(rs.getString(OWNER_CAT));
				transferVO.setOwnerGeo(rs.getString(OWNER_GEO));
				transferVO.setServiceTypeName(rs.getString(SERVICE_TYPE_NAME));
				transferVO.setSelectorName(rs.getString(SELECTOR_NAME));
				transferVO.setTransactionCount(rs.getString(TRANSACTION_COUNT));
				transferVO.setDifferentialAmount(rs.getString(DIFFERENTIAL_AMOUNT));
				transfersList.add(transferVO);
			}

		}
		catch(SQLException sqe){
			log.error(methodName, SQLEXCEPTION + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, SQLEXCEPTION + sqe.getMessage());
			try {
				throw new BTSLBaseException(this, "", SQLEXCEPTIONKEY,sqe);
			} catch (BTSLBaseException e) {
				log.error(methodName, BTSLEXCEPTION+e);
			}
		}
		finally{
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
		}
		return transfersList;
	}
	
	public ArrayList<ChannelTransferVO> loadAdditionalCommisionChannelDailySummary(Connection con, UsersReportModel usersReportModel){
		final String methodName = "loadAdditionalCommisionChannelDetails";
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.ENTERED);
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null ;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try
		{
			pstmt = additionalCommissionSummaryReportQry.loadAdditionalCommisionDetailsChannelDailyQry(con, usersReportModel);

			rs = pstmt.executeQuery();
			while(rs.next())
			{
				transferVO = new ChannelTransferVO();
				transferVO.setTransferDateAsString(rs.getString(TRANS_DATE));
				transferVO.setLoginID(rs.getString(LOGIN_ID));
				transferVO.setUserName(rs.getString(USERNAME));
				transferVO.setMsisdn(rs.getString(MSISDN));
				transferVO.setCategoryName(rs.getString(CATEGORY_NAME));
				transferVO.setGegoraphyDomainName(rs.getString(GRPH_DOMAIN_NAME));
				transferVO.setParentName(rs.getString(PARENT_NAME));
				transferVO.setParentMsisdn(rs.getString(PARENT_MSISDN));
				transferVO.setParentCategory(rs.getString(PARENT_CAT));
				transferVO.setParentGeoName(rs.getString(PARENT_GEO));
				transferVO.setOwnerUser(rs.getString(OWNER_NAME));
				transferVO.setOwnerMsisdn(rs.getString(OWNER_MSISDN));
				transferVO.setOwnerCat(rs.getString(OWNER_CAT));
				transferVO.setOwnerGeo(rs.getString(OWNER_GEO));
				transferVO.setGrandName(rs.getString(GRAND_NAME));
				transferVO.setGrandMsisdn(rs.getString(GRAND_MSISDN));
				transferVO.setGrandCategory(rs.getString(GRAND_CATEGORY));
				transferVO.setGrandGeo(rs.getString(GRAND_GEO_DOMAIN));
				transferVO.setServiceTypeName(rs.getString(SERVICE_TYPE_NAME));
				transferVO.setSelectorName(rs.getString(SELECTOR_NAME));
				transferVO.setTransactionCount(rs.getString(TRANSACTION_COUNT));
				transferVO.setDifferentialAmount(rs.getString(DIFFERENTIAL_AMOUNT));
				transfersList.add(transferVO);
			}

		}
		catch(SQLException sqe){
			log.error(methodName, SQLEXCEPTION + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, SQLEXCEPTION + sqe.getMessage());
			try {
				throw new BTSLBaseException(this, "", SQLEXCEPTIONKEY,sqe);
			} catch (BTSLBaseException e) {
				log.error(methodName, BTSLEXCEPTION+e);
			}
		}
		finally{
		      try{
		          if (rs!= null){
		        	  rs.close();
		          }
		        }
		        catch (SQLException e){
		      	  log.error("An error occurred closing result set.", e);
		        }
			try{
		          if (pstmt!= null){
		        	  pstmt.close();
		          }
		        }
		        catch (SQLException e){
		      	  log.error("An error occurred closing result set.", e);
		        }
		}
		return transfersList;
	}
	
	public ArrayList<ChannelTransferVO> loadAdditionalCommisionChannelMonthlySummary(Connection con, UsersReportModel usersReportModel){
		final String methodName = "loadAdditionalCommisionChannelOldDetails";
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.ENTERED);
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null ;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try
		{
			pstmt = additionalCommissionSummaryReportQry.loadAdditionalCommisionDetailsChannelMonthlyQry(con, usersReportModel);

			rs = pstmt.executeQuery();
			while(rs.next())
			{
				transferVO = new ChannelTransferVO();
				transferVO.setTransferDateAsString(rs.getString(TRANS_DATE));
				transferVO.setLoginID(rs.getString(LOGIN_ID));
				transferVO.setUserName(rs.getString(USERNAME));
				transferVO.setMsisdn(rs.getString(MSISDN));
				transferVO.setCategoryName(rs.getString(CATEGORY_NAME));
				transferVO.setGegoraphyDomainName(rs.getString(GRPH_DOMAIN_NAME));
				transferVO.setParentName(rs.getString(PARENT_NAME));
				transferVO.setParentMsisdn(rs.getString(PARENT_MSISDN));
				transferVO.setParentCategory(rs.getString(PARENT_CAT));
				transferVO.setParentGeoName(rs.getString(PARENT_GEO));
				transferVO.setOwnerUser(rs.getString(OWNER_NAME));
				transferVO.setOwnerMsisdn(rs.getString(OWNER_MSISDN));
				transferVO.setOwnerCat(rs.getString(OWNER_CAT));
				transferVO.setOwnerGeo(rs.getString(OWNER_GEO));
				transferVO.setGrandName(rs.getString(GRAND_NAME));
				transferVO.setGrandMsisdn(rs.getString(GRAND_MSISDN));
				transferVO.setGrandCategory(rs.getString(GRAND_CATEGORY));
				transferVO.setGrandGeo(rs.getString(GRAND_GEO_DOMAIN));
				transferVO.setServiceTypeName(rs.getString(SERVICE_TYPE_NAME));
				transferVO.setSelectorName(rs.getString(SELECTOR_NAME));
				transferVO.setTransactionCount(rs.getString(TRANSACTION_COUNT));
				transferVO.setDifferentialAmount(rs.getString(DIFFERENTIAL_AMOUNT));
				transfersList.add(transferVO);
			}

		}
		catch(SQLException sqe){
			log.error(methodName, SQLEXCEPTION + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, SQLEXCEPTION + sqe.getMessage());
			try {
				throw new BTSLBaseException(this, "", SQLEXCEPTIONKEY,sqe);
			} catch (BTSLBaseException e) {
				log.error(methodName, BTSLEXCEPTION+e);
			}
		}
		finally{
			 try{
			        if (rs!= null){
			        	rs.close();
			        }
			      }
			      catch (SQLException e){
			    	  log.error("An error occurred closing result set.", e);
			      }
			 try{
			        if (pstmt!= null){
			        	pstmt.close();
			        }
			      }
			      catch (SQLException e){
			    	  log.error("An error occurred closing result set.", e);
			      }
		}
		return transfersList;
	}
}
