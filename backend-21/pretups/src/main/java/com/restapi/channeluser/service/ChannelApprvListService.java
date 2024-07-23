package com.restapi.channeluser.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channeluser.businesslogic.ApplistReqVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserApprovalVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;

public class ChannelApprvListService {
	
	private UserDAO userDAO= new UserDAO();
	private CategoryDAO categoryDAO = new CategoryDAO();
	private DomainDAO domainDAO= new DomainDAO();
	 private GeographicalDomainDAO geoDAO = new GeographicalDomainDAO();
	
	private void validate(ApplistReqVO applistReqVO,Connection con) throws BTSLBaseException, SQLException {
		final String methodName ="validate";
		ChannelUserVO  channelUserVO=null;
		if(PretupsI.LOGIN_ID_TAB.equals(applistReqVO.getReqTab())){
			if(BTSLUtil.isNullorEmpty(applistReqVO.getLoginID())) {
				throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
						PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_BLANK);
			}

			channelUserVO=   userDAO.loadAllUserDetailsByLoginID(con, applistReqVO.getLoginID());
			if(BTSLUtil.isNullObject(channelUserVO)) {
				throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
						PretupsErrorCodesI.EXT_USRADD_INVALID_LOGINID);
			}
			
		}else if(PretupsI.MSISDN_TAB.equals(applistReqVO.getReqTab())){
			if(BTSLUtil.isNullObject(applistReqVO.getMobileNumber())) {
				throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
						PretupsErrorCodesI.EXTSYS_REQ_USER_MSISDN_BLANK);
			}
			channelUserVO=   userDAO.loadUserDetailsByMsisdn(con, applistReqVO.getMobileNumber());
			if(BTSLUtil.isNullObject(channelUserVO)) {
				throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
						PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
			}
			
		}else if(PretupsI.ADVANCED_TAB.equals(applistReqVO.getReqTab())) {
			
			if(BTSLUtil.isNullObject(applistReqVO.getDomain())) {
				throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
						PretupsErrorCodesI.INVALID_DOMAIN_OR_EMTPY);
			}
			
			if(!PretupsI.ALL.equals(applistReqVO.getDomain())  ) {
			DomainVO domainVO = domainDAO.loadDomainVO(con, applistReqVO.getDomain());
			if (BTSLUtil.isNullObject(domainVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
			}
			
			
			
			
			if(BTSLUtil.isNullorEmpty(applistReqVO.getCategory())) {
				throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
						PretupsErrorCodesI.INVALID_CATEGORY_OR_EMTPY);
			}
			if(!PretupsI.ALL.equals(applistReqVO.getCategory())  ) {
			CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
					applistReqVO.getCategory());
				if (BTSLUtil.isNullObject(categoryVO)) {
					throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
							PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, 0, null);
				}
			}
			}
			
			if(BTSLUtil.isNullorEmpty(applistReqVO.getGeography())) {
				throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
						PretupsErrorCodesI.INVALID_GEOGRAPHY_OR_EMTPY);
			}
			
		 
			if(!PretupsI.ALL.equals(applistReqVO.getGeography())  ) {
			    if (!geoDAO.isGeographicalDomainExist(con, applistReqVO.getGeography(), true)) {
			 		 throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
								PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY, 0, null);
			    }
			}
				
	    }else {
	    	// Invalid tab;
	    	throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
					PretupsErrorCodesI.INVALID_TAB_REQ);
	    }
		
	}
	
	public List<UserApprovalVO> execute(ApplistReqVO applistReqVO,Connection con) throws BTSLBaseException,SQLException {
		validate(applistReqVO,con);
		 List<UserApprovalVO> returnList = new ArrayList<>();
		if(PretupsI.ADVANCED_TAB.equals(applistReqVO.getReqTab())){
			returnList= userDAO.loadApprovalListbyCreaterAdvance(con, applistReqVO);
		}
		else {
			returnList = userDAO.loadApprovalListbyCreaterMob(con, applistReqVO);
		}
		return returnList;
		
	}

}
