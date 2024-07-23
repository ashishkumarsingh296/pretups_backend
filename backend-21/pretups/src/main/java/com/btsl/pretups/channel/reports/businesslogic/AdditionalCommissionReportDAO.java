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

public class AdditionalCommissionReportDAO {
	public static final Log log = LogFactory.getLog(AdditionalCommissionReportDAO.class.getName());
	private AdditionalCommissionDetailsReportQry additionalCommissionDetailsReportQry;
	private static final String SQLEXCEPTION = "SQLException : ";
	private static final String BTSLEXCEPTION = "BTSLException: ";
	private static final String SQLEXCEPTIONKEY = "error.general.sql.processing";
	private static final String ADJUSTMENTID = "adjustment_id";
	private static final String USERNAME = "user_name";
	private static final String MSISDN = "msisdn";
	private static final String CATEGORY_NAME = "category_name";
	private static final String GRPH_DOMAIN_NAME = "grph_domain_name";
	private static final String PARENT_NAME = "parent_name";
	private static final String PARENT_MSISDN = "parent_msisdn";
	private static final String PARENT_CAT = "parent_cat";
	private static final String PARENT_GEO_NAME = "parent_geo_name";
	private static final String OWNER_USER = "owner_user";
	private static final String OWNER_MSISDN = "owner_msisdn";
	private static final String OWNER_GEO = "owner_geo";
	private static final String OWNER_CAT = "owner_cat";
	private static final String RECEIVER_MSISDN = "receiver_msisdn";
	private static final String TRANSFER_VALUE = "transfer_value";
	private static final String MARGIN_AMOUNT = "margin_amount";
	private static final String MARGIN_RATE = "margin_rate";
	
	
	
	public AdditionalCommissionReportDAO(){
		additionalCommissionDetailsReportQry = (AdditionalCommissionDetailsReportQry) ObjectProducer.getObject(QueryConstants.ADDITIONAL_COMMISSION_DETAILS_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
	}
	
	public ArrayList<ChannelTransferVO> loadAdditionalCommisionOpeartorDetails(Connection con, UsersReportModel usersReportModel){
		final String methodName = "loadAdditionalCommisionOpeartorDetails";
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.ENTERED);
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null ;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try
		{
			pstmt = additionalCommissionDetailsReportQry.loadAdditionalCommisionDetailsOperatorQry(con,usersReportModel);

			rs = pstmt.executeQuery();
			while(rs.next())
			{
				transferVO = new ChannelTransferVO();
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setAdjustmentID(rs.getString(ADJUSTMENTID));
				transferVO.setTime(rs.getString("time"));
				transferVO.setUserName(rs.getString(USERNAME));
				transferVO.setMsisdn(rs.getString(MSISDN));
				transferVO.setCategoryName(rs.getString(CATEGORY_NAME));
				transferVO.setGegoraphyDomainName(rs.getString(GRPH_DOMAIN_NAME));
				transferVO.setParentName(rs.getString(PARENT_NAME));
				transferVO.setParentMsisdn(rs.getString(PARENT_MSISDN));
				transferVO.setParentCategory(rs.getString(PARENT_CAT));
				transferVO.setParentGeoName(rs.getString(PARENT_GEO_NAME));
				transferVO.setOwnerUser(rs.getString(OWNER_USER));
				transferVO.setOwnerMsisdn(rs.getString(OWNER_MSISDN));
				transferVO.setOwnerGeo(rs.getString(OWNER_GEO));
				transferVO.setOwnerCat(rs.getString(OWNER_CAT));
				transferVO.setName(rs.getString("name"));
				transferVO.setReceiverMsisdn(rs.getString(RECEIVER_MSISDN));
				transferVO.setCommissionType(rs.getString("commission_type"));
				transferVO.setTransferAmt(rs.getString(TRANSFER_VALUE));
				transferVO.setMarginAmount(rs.getString(MARGIN_AMOUNT));
				transferVO.setMarginRate(rs.getString(MARGIN_RATE));
				transferVO.setOtfType(rs.getString("otf_type"));
				transferVO.setOtfRate(Double.parseDouble(rs.getString("otf_rate")));
				transferVO.setOtfAmount(Long.parseLong(rs.getString("otf_amount")));
				transfersList.add(transferVO);
			}

			usersReportModel.setOtfStatus(true);
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
	
	
	public ArrayList<ChannelTransferVO> loadAdditionalCommisionOpeartorOldDetails(Connection con, UsersReportModel usersReportModel){
		final String methodName = "loadAdditionalCommisionOpeartorOldDetails";
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.ENTERED);
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null ;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try
		{
			pstmt = additionalCommissionDetailsReportQry.loadAdditionalCommisionDetailsOperatorOldQry(con,usersReportModel);

			rs = pstmt.executeQuery();
			while(rs.next())
			{
				transferVO = new ChannelTransferVO();
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setTime(rs.getString("time"));
				transferVO.setUserName(rs.getString(USERNAME));
				transferVO.setMsisdn(rs.getString(MSISDN));
				transferVO.setCategoryName(rs.getString(CATEGORY_NAME));
				transferVO.setGegoraphyDomainName(rs.getString(GRPH_DOMAIN_NAME));
				transferVO.setParentName(rs.getString(PARENT_NAME));
				transferVO.setParentMsisdn(rs.getString(PARENT_MSISDN));
				transferVO.setParentCategory(rs.getString(PARENT_CAT));
				transferVO.setParentGeoName(rs.getString(PARENT_GEO_NAME));
				transferVO.setOwnerUser(rs.getString(OWNER_USER));
				transferVO.setOwnerMsisdn(rs.getString(OWNER_MSISDN));
				transferVO.setOwnerGeo(rs.getString(OWNER_GEO));
				transferVO.setOwnerCat(rs.getString(OWNER_CAT));
				transferVO.setName(rs.getString("name"));
				transferVO.setReceiverMsisdn(rs.getString(RECEIVER_MSISDN));
				transferVO.setCommissionType(rs.getString("commission_type"));
				transferVO.setTransferAmt(rs.getString(TRANSFER_VALUE));
				transferVO.setMarginAmount(rs.getString(MARGIN_AMOUNT));
				transferVO.setMarginRate(rs.getString(MARGIN_RATE));
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
	
	public ArrayList<ChannelTransferVO> loadAdditionalCommisionChannelDetails(Connection con, UsersReportModel usersReportModel){
		final String methodName = "loadAdditionalCommisionChannelDetails";
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.ENTERED);
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null ;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try
		{
			pstmt = additionalCommissionDetailsReportQry.loadAdditionalCommisionDetailsChannelQry(con,usersReportModel);

			rs = pstmt.executeQuery();
			while(rs.next())
			{
				transferVO = new ChannelTransferVO();
				transferVO.setAdjustmentID(rs.getString(ADJUSTMENTID));
				transferVO.setTime(rs.getString("time"));
				transferVO.setUserName(rs.getString(USERNAME));
				transferVO.setMsisdn(rs.getString(MSISDN));
				transferVO.setCategoryName(rs.getString(CATEGORY_NAME));
				transferVO.setGegoraphyDomainName(rs.getString(GRPH_DOMAIN_NAME));
				transferVO.setParentName(rs.getString(PARENT_NAME));
				transferVO.setParentMsisdn(rs.getString(PARENT_MSISDN));
				transferVO.setParentCategory(rs.getString(PARENT_CAT));
				transferVO.setParentGeoName(rs.getString(PARENT_GEO_NAME));
				transferVO.setOwnerUser(rs.getString(OWNER_USER));
				transferVO.setOwnerMsisdn(rs.getString(OWNER_MSISDN));
				transferVO.setOwnerGeo(rs.getString(OWNER_GEO));
				transferVO.setOwnerCat(rs.getString(OWNER_CAT));
				transferVO.setName(rs.getString("name"));
				transferVO.setReceiverMsisdn(rs.getString(RECEIVER_MSISDN));
				transferVO.setTransferAmt(rs.getString(TRANSFER_VALUE));
				transferVO.setMarginAmount(rs.getString(MARGIN_AMOUNT));
				transferVO.setMarginRate(rs.getString(MARGIN_RATE));
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
	
	public ArrayList<ChannelTransferVO> loadAdditionalCommisionChannelOldDetails(Connection con, UsersReportModel usersReportModel){
		final String methodName = "loadAdditionalCommisionChannelOldDetails";
		if(log.isDebugEnabled())
			log.debug(methodName,PretupsI.ENTERED);
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null ;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try
		{
			pstmt = additionalCommissionDetailsReportQry.loadAdditionalCommisionDetailsChannelOldQry(con,usersReportModel);

			rs = pstmt.executeQuery();
			while(rs.next())
			{
				transferVO = new ChannelTransferVO();
				transferVO.setAdjustmentID(rs.getString(ADJUSTMENTID));
				transferVO.setTime(rs.getString("time"));
				transferVO.setUserName(rs.getString(USERNAME));
				transferVO.setMsisdn(rs.getString(MSISDN));
				transferVO.setCategoryName(rs.getString(CATEGORY_NAME));
				transferVO.setGegoraphyDomainName(rs.getString(GRPH_DOMAIN_NAME));
				transferVO.setParentName(rs.getString(PARENT_NAME));
				transferVO.setParentMsisdn(rs.getString(PARENT_MSISDN));
				transferVO.setParentCategory(rs.getString(PARENT_CAT));
				transferVO.setParentGeoName(rs.getString(PARENT_GEO_NAME));
				transferVO.setOwnerUser(rs.getString(OWNER_USER));
				transferVO.setOwnerMsisdn(rs.getString(OWNER_MSISDN));
				transferVO.setOwnerGeo(rs.getString(OWNER_GEO));
				transferVO.setOwnerCat(rs.getString(OWNER_CAT));
				transferVO.setName(rs.getString("name"));
				transferVO.setReceiverMsisdn(rs.getString(RECEIVER_MSISDN));
				transferVO.setTransferAmt(rs.getString(TRANSFER_VALUE));
				transferVO.setMarginAmount(rs.getString(MARGIN_AMOUNT));
				transferVO.setMarginRate(rs.getString(MARGIN_RATE));
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
