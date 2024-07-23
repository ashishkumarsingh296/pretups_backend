package com.btsl.pretups.channel.reports.businesslogic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.lms.businesslogic.LmsRedemptionDetailsVO;
import com.web.pretups.channel.reports.web.LmsRedemptionReportModel;
/**
 * 
 * @author sweta.verma
 *
 */
 
public class LmsRedemptionRetWidRptDAO {
	private static Log log = LogFactory.getLog(ChannelTransferDAO.class.getName());
	private LmsRedemptionRetWidRptQry lmsRedemptionRetWidRptQry;

	/**
	 * Constructor
	 */
	public LmsRedemptionRetWidRptDAO() {
		lmsRedemptionRetWidRptQry = (LmsRedemptionRetWidRptQry) ObjectProducer
				.getObject(QueryConstants.LMS_REDEMPTION_RET_WID_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
	}
	
/**
 * 
 * @param conn
 * @param lmsRedemptionReportModel
 * @return
 * @throws BTSLBaseException
 * @throws ParseException
 */
	public ArrayList<LmsRedemptionDetailsVO> loadLmsRedemptionDataList(Connection conn, LmsRedemptionReportModel lmsRedemptionReportModel)
			throws BTSLBaseException, ParseException {
		final String methodName = "loadLmsRedemptionDataList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		ArrayList<LmsRedemptionDetailsVO> lmsRedemptionVOList = new ArrayList<>();
		PreparedStatement pstmt = null;
		LmsRedemptionDetailsVO lmsRedemptionDetailsVO = new LmsRedemptionDetailsVO();
		ResultSet rs = null;
		try {
			pstmt = lmsRedemptionRetWidRptQry.loadRedemptionRetWidQry(conn,
					lmsRedemptionReportModel);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				lmsRedemptionDetailsVO.setUserName(rs.getString("USER_NAME"));
				lmsRedemptionDetailsVO.setMsisdn(rs.getString("MSISDN"));
				lmsRedemptionDetailsVO.setRedemptionID(rs.getString("REDEMPTION_ID"));
				lmsRedemptionDetailsVO.setReferenceID(rs.getString("REFERENCE_ID"));
				lmsRedemptionDetailsVO.setRedemptionDate(rs.getString("REDEMPTION_DATE"));
				lmsRedemptionDetailsVO.setPointsRedeemed(rs.getString("POINTS_REDEEMED"));
				lmsRedemptionDetailsVO.setProductName(rs.getString("PRODUCT_NAME"));
				lmsRedemptionDetailsVO.setCreatedBy(rs.getString("CREATED_BY"));
				lmsRedemptionDetailsVO.setRedemptionBy(rs.getString("REDEMPTION_BY"));
				lmsRedemptionDetailsVO.setCategoryName(rs.getString("CATEGORY_NAME"));
				lmsRedemptionDetailsVO.setGraphicalDomainCode(rs.getString("GRPH_DOMAIN_NAME"));
				lmsRedemptionDetailsVO.setProductCode(rs.getString("PRODUCT_CODE"));
				lmsRedemptionDetailsVO.setDomainCode(rs.getString("DOMAIN_CODE"));
				lmsRedemptionDetailsVO.setUserID(rs.getString("USER_ID"));
				lmsRedemptionDetailsVO.setAmountTransferred(rs.getLong("AMOUNT_TRANSFERED"));
				lmsRedemptionDetailsVO.setRedemptionType(rs.getString("REDEMPTION_TYPE"));
				lmsRedemptionVOList.add(lmsRedemptionDetailsVO);

			}
			rs.close();

		} catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
		} finally {
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
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return lmsRedemptionVOList;
	}
}