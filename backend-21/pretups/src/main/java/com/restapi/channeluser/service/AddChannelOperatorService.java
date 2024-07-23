package com.restapi.channeluser.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channeluser.businesslogic.ChannelUserApprovalReqVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;

public class AddChannelOperatorService {
	public static final Log LOG = LogFactory.getLog(AddChannelOperatorService.class.getName());

	public void addOperatorGeographies(ChannelUserApprovalReqVO requestVO, CategoryVO categoryVO,
			ChannelUserVO senderVO, Connection con, ChannelUserVO channelUserVO) throws BTSLBaseException {

		final String methodName = "addOperatorGeographies";

		/*
		 * here we prepare the list of the geographies, from which it belongs first we
		 * check the area from which it belongs then we check the
		 * MultipleGeographicalArea flag
		 */
		ArrayList geographyList = new ArrayList();
		ArrayList networkList = new ArrayList();
		UserGeographiesVO geoVO = null;
		String[] userGeoDomainArr = requestVO.getData().getMultipleGeographyLoc().split(",");
		
		if( userGeoDomainArr!=null && (userGeoDomainArr.length==0 || (userGeoDomainArr.length==1  && userGeoDomainArr[0].trim().length()==0 )) )  {
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ASSIGN_GEOGRAPHY_MANDATORY);
		}

		if (!(TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(categoryVO.getCategoryCode())
				|| TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(categoryVO.getCategoryCode()))) {

			// if user belongs to multiple graph domains
			if (TypesI.YES.equalsIgnoreCase(categoryVO.getMultipleGrphDomains())) {
				if (userGeoDomainArr != null && userGeoDomainArr.length > 0) {
					for (int i = 0, j = userGeoDomainArr.length; i < j; i++) {
						geoVO = new UserGeographiesVO();
						geoVO.setUserId(channelUserVO.getUserID());
						geoVO.setGraphDomainCode(userGeoDomainArr[i]);
						geographyList.add(geoVO);
					}
				}
			} else// if user belongs to single zones
			{
				if (requestVO.getData().getGeographyCode() != null
						&& requestVO.getData().getGeographyCode().trim().length() > 0) {
					geoVO = new UserGeographiesVO();
					geoVO.setUserId(channelUserVO.getUserID());
					geoVO.setGraphDomainCode(requestVO.getData().getGeographyCode());
					geographyList.add(geoVO);
				}
			}
			// insert geography info
			if (geographyList != null && geographyList.size() > 0) {
				UserGeographiesDAO userGeographiesDAO = new UserGeographiesDAO();
				
				if(!PretupsI.NEW.equals(requestVO.getApprovalLevel())) {
					userGeographiesDAO.deleteUserGeographies(con, channelUserVO.getUserID());
				}
				int geographyCount = userGeographiesDAO.addUserGeographyList(con, geographyList);
				if (geographyCount <= 0) {
					try {
						con.rollback();
					} catch (SQLException e) {
						LOG.errorTrace(methodName, e);
					}
					LOG.error("addUserInfo", "Error: while Inserting User Geography Info");
					throw new BTSLBaseException(this, "addUserInfo", "error.general.processing");
				}
				

			}
		}

		else {
			

			// if user belongs to multiple graph domains

			if (userGeoDomainArr != null && userGeoDomainArr.length > 0) {
				for (int i = 0, j = userGeoDomainArr.length; i < j; i++) {
					geoVO = new UserGeographiesVO();
					geoVO.setUserId(channelUserVO.getUserID());
					geoVO.setGraphDomainCode(userGeoDomainArr[i]);
					networkList.add(geoVO);
				}
			}

			// insert geography info
			if (networkList != null && networkList.size() > 0) {
				UserGeographiesDAO userGeographiesDAO = new UserGeographiesDAO();
				
				if(!PretupsI.NEW.equals(requestVO.getApprovalLevel())) {
					userGeographiesDAO.deleteUserGeographies(con, channelUserVO.getUserID());
				   }

				
				int geographyCount = userGeographiesDAO.addUserGeographyList(con, networkList);
				if (geographyCount <= 0) {
					try {
						con.rollback();
					} catch (SQLException e) {
						LOG.errorTrace(methodName, e);
					}
					LOG.error("addUserInfo", "Error: while Inserting User Geography Info");
					throw new BTSLBaseException(this, "addUserInfo", "error.general.processing");
				}

			}

		}

	}

}
