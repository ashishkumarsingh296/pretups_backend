package com.restapi.networkadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.cellidmgt.businesslogic.CellIdMgmtDAO;
import com.btsl.pretups.cellidmgt.businesslogic.CellIdVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.servicegpmgt.businesslogic.ServiceGpMgmtDAO;
import com.btsl.pretups.servicegpmgt.businesslogic.ServiceGpMgmtVO;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.user.businesslogic.NumberConstants;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.networkadmin.requestVO.AddPromoTransferReqVO;
import com.restapi.networkadmin.requestVO.DeletePromoTransferReqVO;
import com.restapi.networkadmin.requestVO.ModifyPromoTransferReqVO;
import com.restapi.networkadmin.requestVO.PromoLoadParentParamReq;
import com.restapi.networkadmin.requestVO.PromoTransferDropdownListReq;
import com.restapi.networkadmin.requestVO.ReceiverSectionInputs;
import com.restapi.networkadmin.requestVO.SearchPromoTransferReqVO;
import com.restapi.networkadmin.responseVO.AddPromoTransferRuleRespVO;
import com.restapi.networkadmin.responseVO.DeletePromoTransferRespVO;
import com.restapi.networkadmin.responseVO.ModifyPromoTransfRuleRespVO;
import com.restapi.networkadmin.responseVO.PromoDepDropdownlistRespVO;
import com.restapi.networkadmin.responseVO.PromoLoadParentUserRespVO;
import com.restapi.networkadmin.responseVO.SearchPromoTransferRespVO;
import com.restapi.networkadmin.serviceI.PromotionalTransfRuleServiceI;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;
import com.web.pretups.transfer.businesslogic.TransferWebDAO;

@Service("promotionalTransfRuleService")
public class PromotionalTransfServiceImpl  implements PromotionalTransfRuleServiceI   {
	protected static final Log log = LogFactory.getLog(TransferControlProfileServiceImpl.class.getName());

	@Override
	public PromoDepDropdownlistRespVO getPromoDependencyDropDownlist(
			PromoTransferDropdownListReq promoTransferDropdownListReq,String userLoginId) throws BTSLBaseException {
		log.info(this, "Entered  method getPromoDependencyDropDownlist()");
		final String methodName ="getPromoDependencyDropDownlist";
		PromoDepDropdownlistRespVO  promoDepDropdownlistRespVO = new PromoDepDropdownlistRespVO();
		//Get Geographical Domain type data
		
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		
		int rowNumber = 0;
        try {
            rowNumber = Integer.parseInt(Constants.getProperty("NO_ROW_PROMOTIONAL_TRANSFER_RULE"));
        } catch (Exception exception) {
        	log.error(methodName, "WRONG ENTRY IN THE CONSTANTS.PROPS FILE FOR \"NO_ROW_PROMOTIONAL_TRANSFER_RULE\" ");
        	log.errorTrace(methodName, exception);
            rowNumber = NumberConstants.FIVE.getIntValue();
        }

        promoDepDropdownlistRespVO.setNoOfRows(rowNumber);
		

		UserDAO userDAO = new UserDAO();
		boolean cellGroupRequired = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED))).booleanValue();
		try {
			con = mcomCon.getConnection();
		 UserVO userVO=	userDAO.loadUsersDetailsByLoginID(con,userLoginId);

		
		final GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
        ArrayList newGeoType = new ArrayList();
        newGeoType = geographicalDomainWebDAO.loadDomainTypeList(con);
        if (newGeoType == null || newGeoType.isEmpty()) {
            throw new BTSLBaseException(this, "getPromoDependencyDropDownlist", PretupsI.NO_GEOGRAPHY_EXIST);
        }
        
        promoDepDropdownlistRespVO.setGeographicalDomainType(newGeoType);
        ArrayList geoDomain = new ArrayList();
        geoDomain = geographicalDomainWebDAO.loadGeoDomainList(con, userVO.getNetworkID());
        if (geoDomain == null || geoDomain.isEmpty()) {
            throw new BTSLBaseException(this, "getPromoDependencyDropDownlist", PretupsI.NO_GEO_DOMAIN_EXIST);
        }
		
        
        final ArrayList geoTypeValueList = new ArrayList();
        int geoDomainSize = geoDomain.size();
        for (int loop = 0; loop < geoDomainSize; loop++) {
            final GeographicalDomainVO geoListObj = (GeographicalDomainVO) geoDomain.get(loop);
            ListValueVO listLOV = new ListValueVO(geoListObj.getGrphDomainName(),geoListObj.getCombinedKey());
            listLOV.setOtherInfo(geoListObj.getGrphDomainType());
            geoTypeValueList.add(listLOV);
        }
        
        promoDepDropdownlistRespVO.setGeographyListAll(geoTypeValueList);
        
        
        //Grade list        
        ArrayList gradeList = new ArrayList();
        final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
        gradeList = categoryGradeDAO.loadGradeList(con);
        if (gradeList == null || gradeList.isEmpty()) {
            throw new BTSLBaseException(this, "getPromoDependencyDropDownlist", PretupsI.NO_GRADE_EXIST);
        }
        final ArrayList gradebyCategoryList = new ArrayList();
        int gradeListSize = gradeList.size();
        for (int loop = 0; loop < gradeListSize; loop++) {
            final GradeVO geoListObj = (GradeVO) gradeList.get(loop);
            ListValueVO listValueVO=  new ListValueVO(geoListObj.getGradeName(), geoListObj.getCombinedKey());
            listValueVO.setOtherInfo(geoListObj.getCategoryCode());		
            gradebyCategoryList.add(new ListValueVO(geoListObj.getGradeName(), geoListObj.getCombinedKey()));
        }
        promoDepDropdownlistRespVO.setGradeListByCategory(gradebyCategoryList);
        
        
        if (cellGroupRequired) {
            final CellIdMgmtDAO cellGroupDAO = new CellIdMgmtDAO();

            ArrayList cellGroupList = new ArrayList();
            cellGroupList = cellGroupDAO.getCellGroupList(con, userVO.getNetworkID());
            if (cellGroupList == null || cellGroupList.isEmpty()) {
                throw new BTSLBaseException(this, "getPromoDependencyDropDownlist",PretupsI.NO_CELL_GROUP_EXIST);
            }
            final ArrayList cellGroupValueList = new ArrayList();
            for (int loop = 0; loop < cellGroupList.size(); loop++) {
                final CellIdVO cellGroupVO = (CellIdVO) cellGroupList.get(loop);
                cellGroupValueList.add(new ListValueVO(cellGroupVO.getGroupName(), cellGroupVO.getCombinedKey()));
            }
            promoDepDropdownlistRespVO.setCellGroupList(cellGroupValueList);

        }
        
        
        ServiceClassWebDAO serviceClasswebDAO = new ServiceClassWebDAO();
        final String interfaceCategory = "'" + PretupsI.INTERFACE_CATEGORY_PREPAID + "','" + PretupsI.INTERFACE_CATEGORY_POSTPAID + "','" + PretupsI.INTERFACE_CATEGORY_VOMS + "'";
        final ArrayList serviceTypeList = serviceClasswebDAO.loadServiceClassList(con, interfaceCategory);
        if (serviceTypeList == null || serviceTypeList.isEmpty()) {
            throw new BTSLBaseException(this, "getPromoDependencyDropDownlist", "promotrfrule.addtrfrule.msg.noservicetype");
        }
        promoDepDropdownlistRespVO.setServiceClassList(serviceTypeList);
        
        TransferWebDAO transferwebDAO= new TransferWebDAO();
      List  subscriberStatusList = transferwebDAO.getSubscriberStatusList(con, PretupsI.LOOKUP_TYPE_SUBSCRIBER_STATUS);
             if (subscriberStatusList == null || subscriberStatusList.isEmpty()) {
            throw new BTSLBaseException(this, "getPromoDependencyDropDownlist", "promotrfrule.addtrfrule.msg.noservicegrouplist");
        }
        final ArrayList subscriberStatusValueList = new ArrayList();
        for (int loop = 0; loop < subscriberStatusList.size(); loop++) {
            final TransferVO transferVO = (TransferVO) subscriberStatusList.get(loop);
            subscriberStatusValueList.add(new ListValueVO(transferVO.getSubscriberStatus(), transferVO.getCombinedKey()));
        }
        
        promoDepDropdownlistRespVO.setSubscriberStatusValueList(subscriberStatusValueList);
        
        
        CardGroupDAO cardGroupDAO = new CardGroupDAO();
        final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
        ArrayList  cardGroupList = cardGroupSetDAO.loadCardGroupSetForTransferRule(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, PretupsI.TRANSFER_RULE_PROMOTIONAL);
        if (cardGroupList == null || cardGroupList.isEmpty()) {
            throw new BTSLBaseException(this, "getPromoDependencyDropDownlist", "promotrfrule.addtrfrule.msg.nocardgroupset");
        }
        
        promoDepDropdownlistRespVO.setCardGroupList(cardGroupList);
        
        
        
        

            final ServiceGpMgmtDAO serviceGroupDAO = new ServiceGpMgmtDAO();
            ArrayList serviceGroupList = new ArrayList();
            serviceGroupList = serviceGroupDAO.getServiceGroupList(con, userVO.getNetworkID());
            if (serviceGroupList == null || serviceGroupList.isEmpty()) {
                throw new BTSLBaseException(this, "getPromoDependencyDropDownlist", "promotrfrule.addtrfrule.msg.noservicegrouplist");
            }
            final ArrayList serviceGroupValueList = new ArrayList();
            for (int loop = 0; loop < serviceGroupList.size(); loop++) {
                final ServiceGpMgmtVO serviceGroupVO = (ServiceGpMgmtVO) serviceGroupList.get(loop);
                serviceGroupValueList.add(new ListValueVO(serviceGroupVO.getGroupName(), serviceGroupVO.getCombinedKey()));
            }
        
            promoDepDropdownlistRespVO.setServiceProviderGroupList(serviceGroupValueList);
        
            final ArrayList subServiceTypeIdList = ServiceSelectorMappingCache.loadSelectorDropDownForTrfRule();
            if (subServiceTypeIdList == null || subServiceTypeIdList.isEmpty()) {
                throw new BTSLBaseException(this, "getPromoDependencyDropDownlist", "promotrfrule.addtrfrule.msg.nosubservice");
            }
            promoDepDropdownlistRespVO.setSubServiceList(subServiceTypeIdList);

            log.info(this, "Exit method getPromoDependencyDropDownlist()");
		
	} catch (SQLException se) {
		log.error("Sql exception occured", se);
		throw new BTSLBaseException("PromotionalTransfServiceImpl", "getPromoDependencyDropDownlist",
				"Error while executing sql statement", se);
	} finally {
		if (mcomCon != null) {
			mcomCon.close("");
			mcomCon = null;
		}
		if (con != null)
			try {
				con.close();
			} catch (SQLException se) {
				throw new BTSLBaseException("PromotionalTransfServiceImpl", "getPromoDependencyDropDownlist",
						"Error while close connection", se);
			}
	}

  		
		
		
		
		
		
		return promoDepDropdownlistRespVO;
	}

	@Override
	public PromoLoadParentUserRespVO loadParentUserList(PromoLoadParentParamReq promoLoadParentParamReq,
			String userLoginId) throws BTSLBaseException {
		log.info(this, "Entered  method loadParentUserList()");
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		UserDAO userDAO = new UserDAO();
		PromoLoadParentUserRespVO promoLoadParentUserRespVO = new PromoLoadParentUserRespVO();
		try {
			con = mcomCon.getConnection();
			UserVO userVO = userDAO.loadUsersDetailsByLoginID(con, userLoginId);
			if(BTSLUtil.isNullString(promoLoadParentParamReq.getGeoDomainCode()) ) {
				throw new BTSLBaseException(this, "loadParentUserList", PretupsI.GEOGRAPHY_REQ);
			}
			
			if(BTSLUtil.isNullString(promoLoadParentParamReq.getCategoryCode()) ) {
				throw new BTSLBaseException(this, "loadParentUserList", PretupsI.CATEGORY_REQ);
			}
			
			if(BTSLUtil.isNullString(promoLoadParentParamReq.getUserName()) ) {
				throw new BTSLBaseException(this, "loadParentUserList", PretupsI.USER_NAME_REQ);
			}
			
			
			TransferWebDAO transferWebDAO = new TransferWebDAO();
			if (!BTSLUtil.isNullString(promoLoadParentParamReq.getUserName())) {
				final ArrayList userList = transferWebDAO.loadPromoUserList(con, promoLoadParentParamReq.getGeoDomainCode(),
						userVO.getNetworkID(), promoLoadParentParamReq.getCategoryCode(),
						"%" + promoLoadParentParamReq.getUserName().toUpperCase() + "%");
				// if no user found throw a message
				if (userList == null || userList.isEmpty()) {
					final String[] str = { promoLoadParentParamReq.getUserName() };
					log.error("loadParentUserList", "Error: User not exist");
					throw new BTSLBaseException(this, "loadParentUserList", PretupsI.USER_NOT_EXIST, str);
				} else {
					promoLoadParentUserRespVO.setUserList(userList);
				}
			}
			
			log.info(this, "Entered  method loadParentUserList()");	
		} catch(SQLException se) {
			throw new BTSLBaseException("PromotionalTransfServiceImpl", "getPromoDependencyDropDownlist",
					"Error while executing sql statement", se);
		}
		catch (BTSLBaseException se) {
			throw new BTSLBaseException("PromotionalTransfServiceImpl", "getPromoDependencyDropDownlist",
					"Error while executing sql statement", se);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				try {
					con.close();
				} catch (SQLException se) {
					throw new BTSLBaseException("PromotionalTransfServiceImpl", "getPromoDependencyDropDownlist",
							"Error while close connection", se);
				}
		}

		return promoLoadParentUserRespVO;
	}

	@Override
	public SearchPromoTransferRespVO searchPromoTransferData(SearchPromoTransferReqVO searchPromoTransferReqVO,
			String userLoginId) throws BTSLBaseException {
		log.info(this, "Entered  method searchPromoTransferData()");
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		UserDAO userDAO = new UserDAO();
		SearchPromoTransferRespVO promoLoadParentUserRespVO = new SearchPromoTransferRespVO();
	    String senderSubscriberType =null;
	    TransferWebDAO transferWebDAO = new TransferWebDAO();
		try {
			con = mcomCon.getConnection();
			UserVO userVO = userDAO.loadUsersDetailsByLoginID(con, userLoginId);
			
			senderSubscriberType=getSenderSubscriberType(searchPromoTransferReqVO);
			validateFieldData(searchPromoTransferReqVO);
			List searchList = transferWebDAO.searchPromotionalTransferRulesList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE,senderSubscriberType, searchPromoTransferReqVO);
			
			if(searchList!=null &&  !searchList.isEmpty()) {
				promoLoadParentUserRespVO.setListSearchPromoData(searchList);
				log.info(this, "Data found for Promotional Transfer rule search");
			}else {
				log.info(this, PretupsErrorCodesI.NO_DATA_FOUND_CRITERIA );
				throw new BTSLBaseException(this, "searchPromoTransferData", PretupsErrorCodesI.NO_DATA_FOUND_CRITERIA );
			}
			
			
		} catch(SQLException se) {
			throw new BTSLBaseException(this, "searchPromoTransferData", PretupsErrorCodesI.NO_DATA_FOUND_CRITERIA );
		}
		 finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				try {
					con.close();
				} catch (SQLException se) {
					throw new BTSLBaseException("PromotionalTransfServiceImpl", "getPromoDependencyDropDownlist",
							"Error while close connection", se);
				}
		}

		
		
		
		
		
		

		return promoLoadParentUserRespVO;
	}
	
	
	
	
	public void validateFieldData(SearchPromoTransferReqVO searchPromoTransferReqVO) throws BTSLBaseException {
		
		
		String userSelectedPromoLevel=searchPromoTransferReqVO.getOptionTab().toUpperCase();
   		if(userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_USER)) {
   			if(BTSLUtil.isNullString(searchPromoTransferReqVO.getUserID()) ) {
   				throw new BTSLBaseException(this, "searchPromoTransferData", PretupsI.USER_NAME_REQ);
   			}
   		
   		}else if(userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_GRADE )  ) {
   			if(BTSLUtil.isNullString(searchPromoTransferReqVO.getGrade())) {
   				throw new BTSLBaseException(this, "searchPromoTransferData",PretupsErrorCodesI.GRADE_REQ );
   			}
   		
   		}else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_GEOGRAPHY)  ) {
   			if(BTSLUtil.isNullString(searchPromoTransferReqVO.getGeography()) ) {
   				throw new BTSLBaseException(this, "searchPromoTransferData", PretupsI.GEOGRAPHY_REQ);
   			}
   		
   	   }else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_CATEGORY)  ) {
   		if(BTSLUtil.isNullString(searchPromoTransferReqVO.getCategoryCode()) ) {
			throw new BTSLBaseException(this, "searchPromoTransferData", PretupsI.CATEGORY_REQ);
		}
   		
   	   }else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_CELLGROUP)  ) {
      		if(BTSLUtil.isNullString(searchPromoTransferReqVO.getCellGroupID()) ) {
    			throw new BTSLBaseException(this, "searchPromoTransferData", PretupsErrorCodesI.CELL_GROUP_REQ);
    		}
   	   }else {
   		   throw new BTSLBaseException(this, "getSenderSubscriberType",PretupsErrorCodesI.INVALID_PROM_TRANSFER_LEVEL );
   		   
   	   }
	}
	
	
	

	
	
	
public void validateAddFieldData(AddPromoTransferReqVO addPromoTransferReqVO) throws BTSLBaseException {
		
		
		String userSelectedPromoLevel=addPromoTransferReqVO.getOptionTab().toUpperCase();
   		if(userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_USER)) {
   			if(BTSLUtil.isNullString(addPromoTransferReqVO.getUserID()) ) {
   				throw new BTSLBaseException(this, "validateAddFieldData", PretupsI.USER_NAME_REQ);
   			}
   		
   		}else if(userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_GRADE )  ) {
   			if(BTSLUtil.isNullString(addPromoTransferReqVO.getGrade())) {
   				throw new BTSLBaseException(this, "validateAddFieldData",PretupsErrorCodesI.GRADE_REQ );
   			}
   		
   		}else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_GEOGRAPHY)  ) {
   			if(BTSLUtil.isNullString(addPromoTransferReqVO.getGeography()) ) {
   				throw new BTSLBaseException(this, "validateAddFieldData", PretupsI.GEOGRAPHY_REQ);
   			}
   		
   	   }else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_CATEGORY)  ) {
   		if(BTSLUtil.isNullString(addPromoTransferReqVO.getCategoryCode()) ) {
			throw new BTSLBaseException(this, "validateAddFieldData", PretupsI.CATEGORY_REQ);
		}
   		
   	   }else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_CELLGROUP)  ) {
      		if(BTSLUtil.isNullString(addPromoTransferReqVO.getCellGroupID()) ) {
    			throw new BTSLBaseException(this, "validateAddFieldData", PretupsErrorCodesI.CELL_GROUP_REQ);
    		}
   	   }else {
   		   throw new BTSLBaseException(this, "validateAddFieldData",PretupsErrorCodesI.INVALID_PROM_TRANSFER_LEVEL );
   		   
   	   }
	}

	
	public String getSenderSubscriberType(SearchPromoTransferReqVO searchPromoTransferReqVO)  throws BTSLBaseException {
   	 String senderSubscriberValue =null;
   	 String userSelectedPromoLevel =null;
   	
   	if(searchPromoTransferReqVO.getOptionTab()!=null) {
   		userSelectedPromoLevel=searchPromoTransferReqVO.getOptionTab().toUpperCase();
   		if(userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_USER)) {
   			senderSubscriberValue=searchPromoTransferReqVO.getUserID();
   		}else if(userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_GRADE )  ) {
   			senderSubscriberValue=searchPromoTransferReqVO.getGrade();
   		}else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_GEOGRAPHY)  ) {
   			senderSubscriberValue=searchPromoTransferReqVO.getGeography();
   	   }else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_CATEGORY)  ) {
   		   senderSubscriberValue=searchPromoTransferReqVO.getCategoryCode();
   	   }else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_CELLGROUP)  ) {
   		   senderSubscriberValue=searchPromoTransferReqVO.getCellGroupID();
   	   }else {
   		   throw new BTSLBaseException(this, "getSenderSubscriberType",PretupsErrorCodesI.INVALID_PROM_TRANSFER_LEVEL );
   		   
   	   }
   		   
   	   }
   	return senderSubscriberValue;
   }
	
	
	
	public String getAddSenderSubscriberType(AddPromoTransferReqVO searchPromoTransferReqVO)  throws BTSLBaseException {
	   	 String senderSubscriberValue =null;
	   	 String userSelectedPromoLevel =null;
	   	
	   	if(searchPromoTransferReqVO.getOptionTab()!=null) {
	   		userSelectedPromoLevel=searchPromoTransferReqVO.getOptionTab().toUpperCase();
	   		if(userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_USER)) {
	   			senderSubscriberValue=searchPromoTransferReqVO.getUserID();
	   		}else if(userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_GRADE )  ) {
	   			senderSubscriberValue=searchPromoTransferReqVO.getGrade();
	   		}else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_GEOGRAPHY)  ) {
	   			senderSubscriberValue=searchPromoTransferReqVO.getGeography();
	   	   }else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_CATEGORY)  ) {
	   		   senderSubscriberValue=searchPromoTransferReqVO.getCategoryCode();
	   	   }else if (userSelectedPromoLevel.equals(PretupsI.PROMO_LEVEL_CELLGROUP)  ) {
	   		   senderSubscriberValue=searchPromoTransferReqVO.getCellGroupID();
	   	   }else {
	   		   throw new BTSLBaseException(this, "getSenderSubscriberType",PretupsErrorCodesI.INVALID_PROM_TRANSFER_LEVEL );
	   		   
	   	   }
	   		   
	   	   }
	   	return senderSubscriberValue;
	   }
	

	@Override
	public AddPromoTransferRuleRespVO addPromoTransferData(AddPromoTransferReqVO addPromoTransferReqVO,
			String userLoginId,Locale locale) throws BTSLBaseException {
		log.info(this, "Entered method addPromoTransferData()");
		final String methodName = "addPromoTransferData";
		MComConnection mcomCon = new MComConnection();
		Connection con = null;
		UserDAO userDAO = new UserDAO();
		AddPromoTransferRuleRespVO addPromoTransferRuleRespVO = new AddPromoTransferRuleRespVO();
		String senderSubscriberType = null;
		TransferWebDAO transferWebDAO = new TransferWebDAO();
		boolean isRecordUpdate = false;
		CommonUtil commUtil = new CommonUtil();
		String fromTime = null;
		String toTime = null;
		int updateCount = 0;
		String msgCode =null;
		try {
			con = mcomCon.getConnection();
			UserVO userVO = userDAO.loadUsersDetailsByLoginID(con, userLoginId);
			validateAddFieldData(addPromoTransferReqVO);
			List<MasterErrorList> listOfErrors = new ArrayList<MasterErrorList>();
			String format = Constants.getProperty("PROMOTIONAL_TRANSFER_DATE_FORMAT");
			final Date currentDate = new Date();
			final String module = PretupsI.PROMOTIONAL_BATCH_TRF_MODULE;
            final String ruleType = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_TYP;
            final String network_code = userVO.getNetworkID();
            final String staus = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_STATUS;
            final String senderServiceClassID = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_SENDER_SERVICE_CLASS_ID;

			//
			if (BTSLUtil.isNullString(format)) {
				format = PretupsI.TIMESTAMP_DATESPACEHHMM;
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.INFO, "PromotionalTransferRuleController[addPromoTransferData]", "", "", "",
						"Missing entry from Constants.props for PROMOTIONAL_TRANSFER_DATE_FORMAT. Taking 'dd/MM/yy HH:mm' as default value.");
			}

			senderSubscriberType = getAddSenderSubscriberType(addPromoTransferReqVO);
			if(log.isDebugEnabled()) {
				log.debug(this, MessageFormat.format("Entered method addPromoTransferData() senderSubscriberType :: {0}", senderSubscriberType));
			}

			if (addPromoTransferReqVO != null && BTSLUtil.isNullOrEmptyList(addPromoTransferReqVO.getList())) {
				throw new BTSLBaseException(this, methodName, PretupsI.NO_RULE_DATA);
			}
			
			List<TransferRulesVO> listofTransferRule = new ArrayList<TransferRulesVO>();
			
			final int maxSlabNo = Integer.parseInt(Constants.getProperty("NO_OF_TIMESLAB_PROMOTIONAL_TRANSFER_RULE"));
			if(addPromoTransferReqVO.getList()!=null && addPromoTransferReqVO.getList().size()>maxSlabNo) {
				   String maxSlaberror[] = {String.valueOf(maxSlabNo) };
		               throw new BTSLBaseException(this, "checkbusinessValidation", PretupsI.PROMO_RULE_MAX_SLABS_EXCEED, maxSlaberror);
			}
			 
			
			TransferRulesVO transferRuleVO=null;
			for (int i = 0; i < addPromoTransferReqVO.getList().size(); i++) {
				 transferRuleVO = new TransferRulesVO();
				ReceiverSectionInputs receiverSectionInputRow = addPromoTransferReqVO.getList().get(i);
				transferRuleVO.setModule(module);
				transferRuleVO.setNetworkCode(userVO.getNetworkID());
				transferRuleVO.setSenderSubscriberType(senderSubscriberType);
				setTransferRuleVO(transferRuleVO, receiverSectionInputRow, listOfErrors, locale);
				transferRuleVO.setRuleLevel(addPromoTransferReqVO.getPromotionalLevel());
				transferRuleVO.setSenderSubscriberType(senderSubscriberType);
				transferRuleVO.setNetworkCode(userVO.getNetworkID());
				transferRuleVO.setPromotionCode(addPromoTransferReqVO.getOptionTab());
				transferRuleVO.setReceiverSubscriberType(receiverSectionInputRow.getType());
//				if (receiverSectionInputRow.getTimeSlabs() != null) {
//					String[] timeslabArry = receiverSectionInputRow.getTimeSlabs().split(",");
//					fromTime = timeslabArry[0];
//					transferRuleVO.setFromTime(fromTime);
//					toTime = timeslabArry[1];
//					transferRuleVO.setTillTime(toTime);
//
//				}
				transferRuleVO.setMultipleSlab(receiverSectionInputRow.getTimeSlabs());
				isRecordUpdate = validateTrfRule(con, transferRuleVO, listOfErrors, locale);
				if(!checkValidDatesinFromandToDate(listOfErrors)) { // if valid dates
					validateBasicTrfRule(con, transferRuleVO, listOfErrors, locale);
				}
				
				validateBasicTrfRuleTwo(con, transferRuleVO, listOfErrors, locale);
				
				listofTransferRule.add(transferRuleVO);
				
			}

			
			validateDuplicateRule(listofTransferRule,locale,listOfErrors);
			if (listOfErrors != null && listOfErrors.size() > 0) {
				ErrorMap errorMap = new ErrorMap();
				errorMap.setMasterErrorList(listOfErrors);
				addPromoTransferRuleRespVO.setErrorMap(errorMap);
				addPromoTransferRuleRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				return addPromoTransferRuleRespVO;
			}


			int j = 0;
			if (!isRecordUpdate) {
				int transferRulesVOListSize = listofTransferRule.size();
				for (int i = 0; i < transferRulesVOListSize; i++) {
					TransferRulesVO transferRulesVO = (TransferRulesVO) listofTransferRule.get(i);
					j++;
					transferRulesVO.setModifiedBy(userVO.getUserID());
					transferRulesVO.setModifiedOn(currentDate);
					transferRulesVO.setCreatedBy(userVO.getUserID());
					transferRulesVO.setCreatedOn(currentDate);
					transferRulesVO.setSelectRangeType(PretupsI.NO);
					updateCount += transferWebDAO.addPromotionalTransferRule(con, transferRulesVO);
				}
			}
			if (con != null) {
				if (updateCount == j || isRecordUpdate) {
					mcomCon.finalCommit();
					addPromoTransferRuleRespVO.setStatus(HttpStatus.SC_OK);
					
					msgCode=PretupsI.PROMO_RULES_ADDED_SUCCESS; // for multiple rule
					if(updateCount==NumberConstants.ONE.getIntValue()) {  // For single rule message.
						msgCode=PretupsI.PROMO_RULE_ADD_SCCUESS;
					}
					
					addPromoTransferRuleRespVO.setMessageCode(msgCode);
					addPromoTransferRuleRespVO.setMessage(
							RestAPIStringParser.getMessage(locale, msgCode, null));
					AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setSource(TypesI.LOGGER_PROMO_TRF_RULE_SOURCE);
                    adminOperationVO.setDate(currentDate);
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                    adminOperationVO.setInfo(RestAPIStringParser.getMessage(locale, msgCode, null));
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    AdminOperationLog.log(adminOperationVO);

				} else {
					mcomCon.finalRollback();
					addPromoTransferRuleRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
					addPromoTransferRuleRespVO.setMessageCode(PretupsI.PROMO_RULE_ADD_UNSCCUESS);
					addPromoTransferRuleRespVO.setMessage(
							RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_ADD_UNSCCUESS, null));
					
					AdminOperationVO adminOperationVO = new AdminOperationVO();
	                adminOperationVO.setSource(TypesI.LOGGER_PROMO_TRF_RULE_SOURCE);
	                adminOperationVO.setDate(currentDate);
	                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
	                adminOperationVO.setInfo(" Promotional transfer rule addition unsuccessful");
	                adminOperationVO.setLoginID(userVO.getLoginID());
	                adminOperationVO.setUserID(userVO.getUserID());
	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                adminOperationVO.setMsisdn(userVO.getMsisdn());
	                AdminOperationLog.log(adminOperationVO);
					
					
				}
			}

		} catch (SQLException se) {
			throw new BTSLBaseException("PromotionalTransfServiceImpl", "addPromoTransferData",
					"Error while executing sql statement", se);
		} catch (ParseException pe) {
			throw new BTSLBaseException("PromotionalTransfServiceImpl", "addPromoTransferData",
					"Error while parsing date", pe);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				try {
					con.close();
				} catch (SQLException se) {
					throw new BTSLBaseException("PromotionalTransfServiceImpl", "addPromoTransferData",
							"Error while close connection", se);
				}
		}		
		
		return addPromoTransferRuleRespVO;
	}
	
	
	
	
	private String setTransferRuleVO(TransferRulesVO transferRuleVO, ReceiverSectionInputs receiverSectionInputRow,List<MasterErrorList> listOfErrors,Locale locale)
			throws ParseException, BTSLBaseException {
	   final String methodName ="setTransferRuleVO";
	   final String[] rowIDArr = new String[2];
		CommonUtil commUtil = new CommonUtil();
		String  keyComb=null;
		
		String rowID[] = {receiverSectionInputRow.getRowIndex() };
		DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
		if(BTSLUtil.isNullString(receiverSectionInputRow.getApplicableFrom())) {
			
			MasterErrorList error = new MasterErrorList();
			error.setErrorCode(PretupsI.PROMO_RULE_FROM_DATE_MANDATORY);
			error.setErrorMsg(
					RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_FROM_DATE_MANDATORY, rowID));
			listOfErrors.add(error);
	   }
		
		
		if(BTSLUtil.isNullString(receiverSectionInputRow.getApplicableTo())) {
			MasterErrorList error = new MasterErrorList();
			error.setErrorCode(PretupsI.PROMO_RULE_TILL_DATE_MANDATORY);
			error.setErrorMsg(
					RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_TILL_DATE_MANDATORY, rowID));
			listOfErrors.add(error);
    	}
		
		if(!BTSLUtil.isNullString(receiverSectionInputRow.getApplicableFrom())) {
			try {
				patternDate.parse(receiverSectionInputRow.getApplicableFrom());
				transferRuleVO.setStartTime(BTSLUtil.getDateFromDateString(receiverSectionInputRow.getApplicableFrom()));
			} catch (Exception be) {
//			      throw new BTSLBaseException(this, methodName, PretupsI.PROMO_RULE_INVALID_APPLICATION_FROM_DATE, rowID);
				rowIDArr[0] = receiverSectionInputRow.getApplicableFrom();
				rowIDArr[1] = receiverSectionInputRow.getRowIndex();
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode(PretupsI.PROMO_RULE_INVALID_APPLICATION_FROM_DATE);
				error.setErrorMsg(
						RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_INVALID_APPLICATION_FROM_DATE, rowIDArr));
				listOfErrors.add(error);
				
				
			}
	    }
		if(!BTSLUtil.isNullString(receiverSectionInputRow.getApplicableTo())) {	
			try {
			patternDate.parse(receiverSectionInputRow.getApplicableTo());
			transferRuleVO.setEndTime(BTSLUtil.getDateFromDateString(receiverSectionInputRow.getApplicableTo()));
			} catch (Exception be) {
	//			throw new BTSLBaseException(this, methodName, PretupsI.PROMO_RULE_INVALID_APPLICATION_TO_DATE, rowID);
				rowIDArr[0] = receiverSectionInputRow.getApplicableTo();
				rowIDArr[1] =  receiverSectionInputRow.getRowIndex();
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode(PretupsI.PROMO_RULE_INVALID_APPLICATION_TO_DATE);
				error.setErrorMsg(
						RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_INVALID_APPLICATION_TO_DATE, rowIDArr));
				listOfErrors.add(error);
	    
			}
		}

		
		
		
		
		transferRuleVO.setApplicableFrom(receiverSectionInputRow.getApplicableFrom());
		transferRuleVO.setApplicableTO(receiverSectionInputRow.getApplicableTo());
		transferRuleVO.setRuleType(PretupsI.TRANSFER_RULE_PROMOTIONAL);
		transferRuleVO.setSelectRangeType(PretupsI.NO);
		transferRuleVO.setReceiverServiceClassID(receiverSectionInputRow.getServiceClassID());
		transferRuleVO.setReceiverSubscriberType(receiverSectionInputRow.getType()); // receiver subscriber type.
		transferRuleVO.setReceiverServiceClassID(receiverSectionInputRow.getServiceClassID());
		transferRuleVO.setSubscriberStatus(receiverSectionInputRow.getSubscriberStatusValue());
		transferRuleVO.setServiceGroupCode(receiverSectionInputRow.getServiceCardGroupID());
		transferRuleVO.setStatus(receiverSectionInputRow.getStatus());
		transferRuleVO.setSubServiceTypeId(receiverSectionInputRow.getSubservice());
		transferRuleVO.setServiceType(receiverSectionInputRow.getServiceType());
		transferRuleVO.setCardGroupSetID(receiverSectionInputRow.getCardGroupSet());
		transferRuleVO.setSenderServiceClassID(PretupsI.ALL);
		transferRuleVO.setRowID(receiverSectionInputRow.getRowIndex());
		keyComb=getPromoKey(transferRuleVO);
		log.info(this, MessageFormat.format("Entered method setTransferRuleVO() senderSubscriberType :: {0}", keyComb));
		
		commUtil.validateFieldComma(receiverSectionInputRow.getTimeSlabs(), "Time slab");
		return keyComb;
	}
	
	
 private String getPromoKey(TransferRulesVO transferRuleVO) {
	 StringBuilder key=new StringBuilder();
	 
			key.append(PretupsI.PROMO_MODULE);
			key.append(PretupsI.PROMO_SEPERATOR);
			key.append(transferRuleVO.getModule());
			key.append(PretupsI.PROMO_COMMA);
			key.append(PretupsI.PROMO_NETWORK_CODE);
			key.append(PretupsI.PROMO_SEPERATOR);
			key.append(transferRuleVO.getNetworkCode());
			key.append(PretupsI.PROMO_COMMA);
			key.append(PretupsI.PROMO_SENDER_SUBSCRIBER_TYPE);
			key.append(PretupsI.PROMO_SEPERATOR);
			key.append(transferRuleVO.getSenderSubscriberType());
			key.append(PretupsI.PROMO_COMMA);
			key.append(PretupsI.PROMO_SENDER_SERVICE_CLASSID);
			key.append(PretupsI.PROMO_SEPERATOR);
			key.append(transferRuleVO.getSenderServiceClassID());
			key.append(PretupsI.PROMO_COMMA);
			key.append(PretupsI.PROMO_RECEIVER_SERVICE_CLASSID);
			key.append(PretupsI.PROMO_SEPERATOR);
			key.append(transferRuleVO.getReceiverServiceClassID());
			key.append(PretupsI.PROMO_COMMA);
			key.append(PretupsI.PROMO_SUB_SERVICE_TYPE_ID);
			key.append(PretupsI.PROMO_SEPERATOR);
			key.append(transferRuleVO.getSubServiceTypeId());
			key.append(PretupsI.PROMO_COMMA);
			key.append(PretupsI.PROMO_SUB_SERVICE_TYPE_ID);
			key.append(PretupsI.PROMO_SEPERATOR);
			key.append(transferRuleVO.getSubServiceTypeId());
			key.append(PretupsI.PROMO_COMMA);
			key.append(PretupsI.PROMO_SERVICE_TYPE);
			key.append(PretupsI.PROMO_SEPERATOR);
			key.append(transferRuleVO.getServiceType());
			key.append(PretupsI.PROMO_COMMA);
			key.append(PretupsI.PROMO_RULE_LEVEL);
			key.append(PretupsI.PROMO_SEPERATOR);
			key.append(transferRuleVO.getRuleLevel());
			key.append(PretupsI.PROMO_COMMA);
			return key.toString();
	 
 }
 
 
 private boolean checkValidDatesinFromandToDate(List<MasterErrorList> listOfErrors) throws BTSLBaseException {
	 boolean invalidDate=false;
	 for(MasterErrorList error : listOfErrors) {
		 if(error.getErrorCode().equals(PretupsI.PROMO_RULE_INVALID_APPLICATION_FROM_DATE)  ||  error.getErrorCode().equals(PretupsI.PROMO_RULE_INVALID_APPLICATION_TO_DATE) ) {
			 invalidDate=true;
			 break;
		 }
	 }
	 return invalidDate;
 }
 
 
 private void validateBasicTrfRule(Connection con,TransferRulesVO transferRuleVO,List<MasterErrorList> listOfErrors,Locale locale) throws BTSLBaseException, ParseException {
	  final String methodName ="validateBasicTrfRule";
	  log.info(methodName," Validating timeslabs started");
		String fromTime = null;
		String tillTime = null;
		HashMap<String, String> timekeyMap = new HashMap<String, String>();
		final String[] rowIDArr = new String[2];
		final String format = Constants.getProperty("PROMOTIONAL_TRANSFER_DATE_FORMAT");
		Date currentDate = new Date();
		int currentrowIDValue=0;
		
try {		
	if(transferRuleVO.getMultipleSlab()!=null && transferRuleVO.getMultipleSlab().trim().length()>0 ) {	
		
		try {
			currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateTimeStringFromDate(currentDate), format);
		} catch (ParseException pe) {
			log.error("Parse Error", pe);
		}
		
		
		if(!BTSLUtil.isNullString(transferRuleVO.getMultipleSlab())) {
		String[] timeslabArry = transferRuleVO.getMultipleSlab().split(","); // 10:20-14:30,15:30-18:30,19:30:21:50

		for (int i = 0; i < timeslabArry.length; i++) {
			if (timekeyMap.containsKey(timeslabArry[i])) {
				rowIDArr[0] = timeslabArry[i];
				rowIDArr[1] = transferRuleVO.getRowID();
				currentrowIDValue=Integer.parseInt(transferRuleVO.getRowID());
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode(PretupsI.PROMO_RULE_TIME_RANGE_DUPLICATED);
				error.setErrorMsg(
						RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_TIME_RANGE_DUPLICATED, rowIDArr));
				listOfErrors.add(error);
			} else {
				timekeyMap.put(timeslabArry[i], timeslabArry[i]); // key,value -> "10:20-14:30" ,"10:20-14:30"
			}

		}
			
		
		

		for (int i = 0; i < timeslabArry.length; i++) {
			if(!BTSLUtil.isNullString(transferRuleVO.getApplicableFrom())) {
			rowIDArr[0] = timeslabArry[i];
			rowIDArr[1] = transferRuleVO.getRowID();
			currentrowIDValue=Integer.parseInt(transferRuleVO.getRowID());
			
			
			if (timeslabArry[i] != null && timeslabArry[i].contains("-")) {
				String[] timeslabFromTo = timeslabArry[i].split("-"); // 10:20-14:30
				if (timeslabFromTo[0] != null && timeslabFromTo[1] != null) {
					fromTime = timeslabFromTo[0].trim(); // 10:20
					tillTime = timeslabFromTo[1].trim(); // 14:30
					validateTimeRangebetween(timekeyMap, fromTime, tillTime, listOfErrors, locale,
							transferRuleVO.getRowID());
					
				}

				Date fromDate = BTSLUtil.getDateFromDateString(transferRuleVO.getApplicableFrom() + " " + fromTime,
						format);
				if (currentDate.after(fromDate) || currentDate.equals(fromDate)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("promotrfrule.addpromoc2stransferrules.error.fromdatetimeerror",
					// arr));
					rowIDArr[0] = transferRuleVO.getApplicableFrom() + " " + fromTime;
					rowIDArr[1] = transferRuleVO.getRowID();
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode(PretupsI.PROMO_RULE_FROM_DATE_ERROR);
					error.setErrorMsg(
							RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_FROM_DATE_ERROR, rowIDArr));
					listOfErrors.add(error);

				}

				Date tillDate = BTSLUtil.getDateFromDateString(transferRuleVO.getApplicableTO() + " " + tillTime,
						format);
				if (currentDate.after(tillDate)) {
					// errors.add(ActionMessages.GLOBAL_MESSAGE, new
					// ActionMessage("promotrfrule.addpromoc2stransferrules.error.tilldatetimeerror",
					// rowIDArr));
					rowIDArr[0] = transferRuleVO.getApplicableTO() + " " + tillTime;
					rowIDArr[1] = transferRuleVO.getRowID();
					MasterErrorList error = new MasterErrorList();
					error.setErrorCode(PretupsI.PROMO_RULE_TILL_DATE_ERROR);
					error.setErrorMsg(
							RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_TILL_DATE_ERROR, rowIDArr));
					listOfErrors.add(error);
				}

				if (!tillDate.equals(fromDate)) {
					if ((tillDate.before(fromDate))) {
						// errors.add(ActionMessages.GLOBAL_MESSAGE, new
						// ActionMessage("promotrfrule.addpromoc2stransferrules.error.daterange", arr));
						rowIDArr[0] = transferRuleVO.getRowID();	
						MasterErrorList error = new MasterErrorList();
						error.setErrorCode(PretupsI.PROMO_RULE_FROM_GRT_TILL_DATE_ERROR);
						error.setErrorMsg(RestAPIStringParser.getMessage(locale,
								PretupsI.PROMO_RULE_FROM_GRT_TILL_DATE_ERROR, rowIDArr));
						listOfErrors.add(error);
					}
				}

			}else {
				String errorRowValue[] = { String.valueOf(currentrowIDValue) };
		        throw new BTSLBaseException(this, methodName, PretupsI.PROMO_RULE_INVALID_TIME_SLAB_FORMAT , errorRowValue);
			}
			
			}

		}
		} // end if condition		
	}	else {
		//validate only dates, when Time slab is empty
		validateOnlyApplicableFromAndTo(con,transferRuleVO,listOfErrors,locale);
	}
}catch(Exception e) {
	String errorRowValue[] = { String.valueOf(currentrowIDValue) };
        throw new BTSLBaseException(this, methodName, PretupsI.PROMO_RULE_INVALID_TIME_SLAB_FORMAT , errorRowValue);
}
		
		log.info(methodName," Validating timeslabs ended");
	  }
 
 private void validateOnlyApplicableFromAndTo(Connection con,TransferRulesVO transferRuleVO,List<MasterErrorList> listOfErrors,Locale locale) throws BTSLBaseException, ParseException {
		Date currentDate = new Date();
		final String[] rowIDArr = new String[2];
	  String dateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
   	  try {
			currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateTimeStringFromDate(currentDate), dateFormat);
		} catch (ParseException pe) {
			log.error("Parse Error", pe);
		}

	 
		Date fromDate = BTSLUtil.getDateFromDateString(transferRuleVO.getApplicableFrom() ,
				dateFormat);
		if (currentDate.after(fromDate) ) {
			// errors.add(ActionMessages.GLOBAL_MESSAGE, new
			// ActionMessage("promotrfrule.addpromoc2stransferrules.error.fromdatetimeerror",
			// arr));
			rowIDArr[0] = transferRuleVO.getApplicableFrom();
			rowIDArr[1] = transferRuleVO.getRowID();
			MasterErrorList error = new MasterErrorList();
			error.setErrorCode(PretupsI.PROMO_RULE_FROM_DATE_CURRNTDATE_ERROR);
			error.setErrorMsg(
					RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_FROM_DATE_CURRNTDATE_ERROR, rowIDArr));
			listOfErrors.add(error);

		}

		Date tillDate = BTSLUtil.getDateFromDateString(transferRuleVO.getApplicableTO() ,
				dateFormat);
		if (currentDate.after(tillDate)) {
			// errors.add(ActionMessages.GLOBAL_MESSAGE, new
			// ActionMessage("promotrfrule.addpromoc2stransferrules.error.tilldatetimeerror",
			// rowIDArr));
			rowIDArr[0] = transferRuleVO.getApplicableTO();
			rowIDArr[1] = transferRuleVO.getRowID();
			MasterErrorList error = new MasterErrorList();
			error.setErrorCode(PretupsI.PROMO_RULE_TILL_DATE_CURRNTDATE_ERROR);
			error.setErrorMsg(
					RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_TILL_DATE_CURRNTDATE_ERROR, rowIDArr));
			listOfErrors.add(error);
		}

		if (!tillDate.equals(fromDate)) {
			if ((tillDate.before(fromDate))) {
				// errors.add(ActionMessages.GLOBAL_MESSAGE, new
				// ActionMessage("promotrfrule.addpromoc2stransferrules.error.daterange", arr));
				rowIDArr[0] = transferRuleVO.getRowID();	
				MasterErrorList error = new MasterErrorList();
				error.setErrorCode(PretupsI.PROMO_RULE_TILL_DATE_RANGE_ERROR);
				error.setErrorMsg(RestAPIStringParser.getMessage(locale,
						PretupsI.PROMO_RULE_TILL_DATE_RANGE_ERROR, rowIDArr));
				listOfErrors.add(error);
			}
		}

 }
 
 
 private void validateBasicTrfRuleTwo(Connection con,TransferRulesVO transferRuleVO,List<MasterErrorList> listOfErrors,Locale locale) throws BTSLBaseException, ParseException {
	 final String[] rowIDArr = new String[1];
		CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
		CardGroupDAO cardGroupDAO = new CardGroupDAO();
		ServiceGpMgmtDAO serviceGrpMgmtDAO = new ServiceGpMgmtDAO();
		ServiceClassWebDAO serviceClassWebDAO = new ServiceClassWebDAO();

	 rowIDArr[0] = transferRuleVO.getRowID();  
     ArrayList  cardGroupList = cardGroupSetDAO.validateCardGroupSetId(con, transferRuleVO.getNetworkCode(), PretupsI.C2S_MODULE, PretupsI.TRANSFER_RULE_PROMOTIONAL,transferRuleVO.getCardGroupSetID());
     if(cardGroupList==null  || BTSLUtil.isNullOrEmptyList(cardGroupList) ) {
    	   MasterErrorList error = new MasterErrorList();
      		error.setErrorCode(PretupsI.PROMO_RULE_INVALID_CARD_GROUP);
      		error.setErrorMsg(
      				RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_INVALID_CARD_GROUP, rowIDArr));
      		listOfErrors.add(error);
    	 
     }
   
     
    if(transferRuleVO.getServiceGroupCode()!=null && !transferRuleVO.getServiceGroupCode().equalsIgnoreCase(PretupsI.ALL)  ) { 
	     ArrayList<ServiceGpMgmtVO> serviceGroupList =		serviceGrpMgmtDAO.validateServiceGroupList(con, transferRuleVO.getNetworkCode(), transferRuleVO.getServiceGroupCode());
	     if(serviceGroupList==null  || BTSLUtil.isNullOrEmptyList(serviceGroupList) ) {
	  	   MasterErrorList error = new MasterErrorList();
	    		error.setErrorCode(PretupsI.PROMO_RULE_INVALID_SERVICE_GROUP);
	    		error.setErrorMsg(
	    				RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_INVALID_SERVICE_GROUP, rowIDArr));
	    		listOfErrors.add(error);
	     }
    }
     
    if(transferRuleVO.getReceiverServiceClassID()!=null && !transferRuleVO.getReceiverServiceClassID().equalsIgnoreCase(PretupsI.ALL) ) { 
     final String interfaceCategory = "'" + PretupsI.INTERFACE_CATEGORY_PREPAID + "','" + PretupsI.INTERFACE_CATEGORY_POSTPAID + "','" + PretupsI.INTERFACE_CATEGORY_VOMS + "'";
      ArrayList serviceClassList=  serviceClassWebDAO.validateServiceClassList(con,interfaceCategory,transferRuleVO.getReceiverServiceClassID());
      if(serviceClassList==null  || BTSLUtil.isNullOrEmptyList(serviceClassList) ) {
     	   MasterErrorList error = new MasterErrorList();
       		error.setErrorCode(PretupsI.PROMO_RULE_INVALID_SERVICE_GROUP);
       		error.setErrorMsg(
       				RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_INVALID_SERVICE_GROUP, rowIDArr));
       		listOfErrors.add(error);
     	 
      }
    }
     
     
     if(transferRuleVO.getServiceType()!=null && !transferRuleVO.getServiceType().equalsIgnoreCase(PretupsI.ALL)) {
       ArrayList serviceTypeList = cardGroupDAO.ValidateServiceTypeList(con, transferRuleVO.getNetworkCode(), PretupsI.C2S_MODULE,transferRuleVO.getServiceType());
       if(serviceTypeList==null  || BTSLUtil.isNullOrEmptyList(serviceTypeList) ) {
    	   MasterErrorList error = new MasterErrorList();
      		error.setErrorCode(PretupsI.PROMO_RULE_INVALID_SERVICE_TYPE);
      		error.setErrorMsg(
      				RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_INVALID_SERVICE_TYPE, rowIDArr));
      		listOfErrors.add(error);
       }
     }

 }
 
 
 private void validateTimeRangebetween(HashMap<String,String> htimemap,String fromtime, String tillTime , List<MasterErrorList> listOfErrors,Locale locale,String rowID) {
	 
	 boolean rangeCheck=false;
	 String[] timeArray =null;
	 String[] fromhourMin= new String[2];
	 String[] tillhourMin= new String[2];
	 String[]  rowIDArr = new String[3];
	 
	 
	 String [] fromtimeArr =fromtime.split(":");
	 String [] tillTimeArry =tillTime.split(":");
	 
	//10:20			14:30	
	 Long fromTimeLong = Long.valueOf(fromtimeArr[0].concat(fromtimeArr[1])); // Hr:Min -> HrMin 10:20 -> 1020
	 Long tillTimeLong = Long.valueOf(tillTimeArry[0].concat(tillTimeArry[1])); // Hr:Min -> HrMin 14:30- > 1430
	 
	 //commenting for PRETUPS-23023
	 //	 if(fromTimeLong > tillTimeLong) {
	 //		 rowIDArr[0]=fromtime;
	 //		 rowIDArr[1]=tillTime;
	 //		 rowIDArr[2]=rowID;
	 //		 MasterErrorList error = new MasterErrorList();
	 //	 		error.setErrorCode(PretupsI.PROMO_RULE_FROMTIME_grt_TILLTIME);
	 //	 		error.setErrorMsg(
	 //	 				RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_FROMTIME_grt_TILLTIME, rowIDArr));
	 //	 		listOfErrors.add(error);
	 //	 }
	 
	 for (Entry<String, String> entry : htimemap.entrySet()) //using map.entrySet() for iteration  
	 {  

		// hmap key,value -> ("10:20-14:30" ,"10:20-14:30")
		 timeArray=  entry.getKey().split("-");  //10:20-14:30  -> split     
		 //for(int i=0;i<timeArray.length;i++) {
			 fromhourMin=   timeArray[0].split(":");
			 Long fromconcatHourMin = Long.valueOf(fromhourMin[0].concat(fromhourMin[1]));
			 tillhourMin=   timeArray[1].split(":");
			 Long tillconcatHourMin = Long.valueOf(tillhourMin[0].concat(tillhourMin[1]));
			  if(!(fromtime.concat("-").concat(tillTime)).equalsIgnoreCase(entry.getKey())) {
				  
				  if( (fromconcatHourMin>= fromTimeLong &&  fromconcatHourMin <=tillTimeLong ) || (tillconcatHourMin >=fromTimeLong && tillconcatHourMin <=tillTimeLong) ) {
					  rowIDArr[0]=fromtime;
						 rowIDArr[1]=tillTime;
						 rowIDArr[2]=rowID;
						 MasterErrorList error = new MasterErrorList();
					 		error.setErrorCode(PretupsI.PROMO_RULE_FROMTIME_TILLTIME_ALREADYDEFRANGE);
					 		error.setErrorMsg(
					 				RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_FROMTIME_TILLTIME_ALREADYDEFRANGE, rowIDArr));
					 		listOfErrors.add(error);
				  }
			  }
		 }
		 
		 
	 }   
	 
	 

 
	
  private boolean validateTrfRule(Connection con,TransferRulesVO transferRuleVO,List<MasterErrorList> listOfErrors,Locale locale) throws BTSLBaseException {
	  boolean isRecordUpdate = false;
	  final Date currentDate = new Date();
	  TransferWebDAO  transferWebDAO = new TransferWebDAO();
	  if (transferWebDAO.isPromotionalTransferRuleExist(con, transferRuleVO)) {
          if (log.isDebugEnabled()) {
       	   log.debug("add", "isTransferRuleExist=true");
          }
//          isRecordUpdate = transferWebDAO.isPromotionalTransferRuleUpdates(con, transferRuleVO, currentDate);
//          if (!isRecordUpdate) {
              final String[] rowIDArr = new String[1];
              rowIDArr[0] = transferRuleVO.getRowID();
              MasterErrorList error = new MasterErrorList();
  			error.setErrorCode(PretupsI.PROMO_RECORD_EXISTS);
  			error.setErrorMsg(
  					RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RECORD_EXISTS, rowIDArr));
  			listOfErrors.add(error);
              
          

      }
	  return isRecordUpdate;
  }
  
  private boolean validateModifyTrfRule(Connection con,TransferRulesVO transferRuleVO,List<MasterErrorList> listOfErrors,Locale locale) throws BTSLBaseException {
	  boolean isRecordExist = false;
	  final Date currentDate = new Date();
	  TransferWebDAO  transferWebDAO = new TransferWebDAO();
	  isRecordExist=transferWebDAO.isPromotionalTransferRuleExist(con, transferRuleVO);
	  if (!isRecordExist) {
          if (log.isDebugEnabled()) {
       	   log.debug("add", "isTransferRuleExist=false");
          }
              final String[] rowIDArr = new String[1];
              rowIDArr[0] = transferRuleVO.getRowID();
              MasterErrorList error = new MasterErrorList();
  			error.setErrorCode(PretupsI.PROMO_RECORD_NOT_EXISTS);
  			error.setErrorMsg(
  					RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RECORD_NOT_EXISTS, rowIDArr));
  			listOfErrors.add(error);
    
      }
	  return isRecordExist;

  }

  
  
  private boolean validateDuplicateRule(List<TransferRulesVO> listofRules,Locale locale,List<MasterErrorList> listOfErrors) {
	  boolean serviceProviderPromoAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);
      boolean cellGroupRequired = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED))).booleanValue();
	  
	  int   k=0;
	  int l=0;// for the for loop
	  int j=0;
      String key1, key2;// for the comparison of the selected rows(for
	  if(listofRules!=null) {
		  for(int i=0;i<listofRules.size();i++) {
			  TransferRulesVO transferRulesVO =listofRules.get(i);
			  
			  key1 = transferRulesVO.getSenderSubscriberType() + transferRulesVO.getSenderServiceClassID() + transferRulesVO.getReceiverSubscriberType() + transferRulesVO
                      .getReceiverServiceClassID() + transferRulesVO.getSubServiceTypeId() + transferRulesVO.getRuleLevel() + transferRulesVO.getServiceType();
		      if (cellGroupRequired || serviceProviderPromoAllow) {
		          final String Key4 = transferRulesVO.getSubscriberStatus() + transferRulesVO.getServiceGroupCode();
		          key1 = key1 + Key4;
		      }

		      for ( j = i + 1, k = listofRules.size(); j < k; j++) {
                  transferRulesVO = (TransferRulesVO) listofRules.get(j);
                  key2 = transferRulesVO.getSenderSubscriberType() + transferRulesVO.getSenderServiceClassID() + transferRulesVO.getReceiverSubscriberType() + transferRulesVO
                                  .getReceiverServiceClassID() + transferRulesVO.getSubServiceTypeId() + transferRulesVO.getRuleLevel() + transferRulesVO.getServiceType();
                  if (cellGroupRequired || serviceProviderPromoAllow) {
                      final String Key5 = transferRulesVO.getSubscriberStatus() + transferRulesVO.getServiceGroupCode();
                      key2 = key2 + Key5;

                  }
                  if (key1.equals(key2)) {
                      if (log.isDebugEnabled()) {
                          log.debug("confirm", "SameRuleAssigned=true");
                      }
                      final String[] rowIDArr = new String[2];//
                      rowIDArr[0] = String.valueOf(i + 1);
                      rowIDArr[1] = String.valueOf(j + 1);
                      MasterErrorList error = new MasterErrorList();
            			error.setErrorCode(PretupsI.PROMO_RULE_DUPLICATE);
            			error.setErrorMsg(
            					RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_DUPLICATE, rowIDArr));
            			listOfErrors.add(error);
                      
                      
                      
                  }

              }

			  
			  
			  
		  }
	  
	  }
	  
	  
	  return true;
  }

@Override
public ModifyPromoTransfRuleRespVO modifyPromoTransferData(ModifyPromoTransferReqVO modifyPromoTransferReqVO,
		String userLoginId, Locale locale) throws BTSLBaseException {
	
	
	final String methodName = "modifyPromoTransferData";
	MComConnection mcomCon = new MComConnection();
	Connection con = null;
	UserDAO userDAO = new UserDAO();
	ModifyPromoTransfRuleRespVO modPromoTransferRuleRespVO = new ModifyPromoTransfRuleRespVO();
	String senderSubscriberType = null;
	TransferWebDAO transferWebDAO = new TransferWebDAO();
	boolean isRecordUpdate = false;
	CommonUtil commUtil = new CommonUtil();
	String fromTime = null;
	String toTime = null;
	int updateCount = 0;
	String msgCode=null;
	final String[] rowIDArr = new String[2];
	try {
		con = mcomCon.getConnection();
		UserVO userVO = userDAO.loadUsersDetailsByLoginID(con, userLoginId);
		validateAddFieldData(modifyPromoTransferReqVO);

		List<MasterErrorList> listOfErrors = new ArrayList<MasterErrorList>();

		String format = Constants.getProperty("PROMOTIONAL_TRANSFER_DATE_FORMAT");
		final Date currentDate = new Date();
		final String module = PretupsI.PROMOTIONAL_BATCH_TRF_MODULE;
        final String ruleType = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_TYP;
        final String network_code = userVO.getNetworkID();
        final String staus = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_STATUS;
        final String senderServiceClassID = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_SENDER_SERVICE_CLASS_ID;

		//
		if (BTSLUtil.isNullString(format)) {
			format = PretupsI.TIMESTAMP_DATESPACEHHMM;
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
					EventLevelI.INFO, "PromotionalTransferRuleController[addPromoTransferData]", "", "", "",
					"Missing entry from Constants.props for PROMOTIONAL_TRANSFER_DATE_FORMAT. Taking 'dd/MM/yy HH:mm' as default value.");
		}

		senderSubscriberType = getAddSenderSubscriberType(modifyPromoTransferReqVO);

		if (modifyPromoTransferReqVO != null && BTSLUtil.isNullOrEmptyList(modifyPromoTransferReqVO.getList())) {
			throw new BTSLBaseException(this, methodName, PretupsI.NO_RULE_DATA);
		}
		
		List<TransferRulesVO> listofTransferRule = new ArrayList<TransferRulesVO>();

		for (int i = 0; i < modifyPromoTransferReqVO.getList().size(); i++) {
			TransferRulesVO transferRuleVO = new TransferRulesVO();
			ReceiverSectionInputs receiverSectionInputRow = modifyPromoTransferReqVO.getList().get(i);
			transferRuleVO.setModule(module);
			transferRuleVO.setNetworkCode(userVO.getNetworkID());
			transferRuleVO.setSenderSubscriberType(senderSubscriberType);
			setTransferRuleVO(transferRuleVO, receiverSectionInputRow,listOfErrors, locale);
			transferRuleVO.setRuleLevel(modifyPromoTransferReqVO.getPromotionalLevel());
			transferRuleVO.setSenderSubscriberType(senderSubscriberType);
			transferRuleVO.setNetworkCode(userVO.getNetworkID());
			transferRuleVO.setPromotionCode(modifyPromoTransferReqVO.getOptionTab());
			transferRuleVO.setReceiverSubscriberType(receiverSectionInputRow.getType());
			transferRuleVO.setMultipleSlab(receiverSectionInputRow.getTimeSlabs());
			isRecordUpdate = validateModifyTrfRule(con, transferRuleVO, listOfErrors, locale);
			if(!checkValidDatesinFromandToDate(listOfErrors)) { // if valid dates
				validateBasicTrfRule(con, transferRuleVO, listOfErrors, locale);
			}
			validateBasicTrfRuleTwo(con, transferRuleVO, listOfErrors, locale);
			listofTransferRule.add(transferRuleVO);
		}

		
		validateDuplicateRule(listofTransferRule,locale,listOfErrors);
		int j = 0;
		if (listOfErrors != null && listOfErrors.size() > 0) {
			ErrorMap errorMap = new ErrorMap();
			errorMap.setMasterErrorList(listOfErrors);
			modPromoTransferRuleRespVO.setErrorMap(errorMap);
			modPromoTransferRuleRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			return modPromoTransferRuleRespVO;
		}else {
		
			int transferRulesVOListSize = listofTransferRule.size();
			for (int i = 0; i < transferRulesVOListSize; i++) {
				TransferRulesVO transferRulesVO = (TransferRulesVO) listofTransferRule.get(i);
				j++;
				transferRulesVO.setModifiedBy(userVO.getUserID());
				transferRulesVO.setModifiedOn(currentDate);
				transferRulesVO.setSelectRangeType(PretupsI.YES);
				updateCount += transferWebDAO.updatePromotionalTransferRule(con, transferRulesVO);
			}
		}
		if (con != null) {
			if (updateCount == j ) {
				mcomCon.finalCommit();
				modPromoTransferRuleRespVO.setStatus(HttpStatus.SC_OK);
				
				msgCode=PretupsI.PROMO_RULES_MODIFIED_SUCCESS;
				if(updateCount==NumberConstants.ONE.getIntValue()) {
					msgCode=PretupsI.MODIFY_PROMO_TR_SUCCESS;
				}
				
				modPromoTransferRuleRespVO.setMessageCode(msgCode);
				modPromoTransferRuleRespVO.setMessage(
						RestAPIStringParser.getMessage(locale, msgCode, null));
				
				
				AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_PROMO_TRF_RULE_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setInfo(RestAPIStringParser.getMessage(locale, msgCode, null));
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);

			} else {
				mcomCon.finalRollback();
				modPromoTransferRuleRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				modPromoTransferRuleRespVO.setMessageCode(PretupsI.MODIFY_PROMO_TR_UNSUCCESS);
				modPromoTransferRuleRespVO.setMessage(
						RestAPIStringParser.getMessage(locale, PretupsI.MODIFY_PROMO_TR_UNSUCCESS, null));
				AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_PROMO_TRF_RULE_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setInfo(" Promotional transfer rule updation failed");
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
			}
		}

	} catch (SQLException se) {
		throw new BTSLBaseException("PromotionalTransfServiceImpl", "modifyPromoTransferData",
				"Error while executing sql statement", se);
	} catch (ParseException pe) {
		throw new BTSLBaseException("PromotionalTransfServiceImpl", "modifyPromoTransferData",
				"Error while parsing date", pe);
	} catch (BTSLBaseException se) {
		throw se;
	} finally {
		if (mcomCon != null) {
			mcomCon.close("");
			mcomCon = null;
		}
		if (con != null)
			try {
				con.close();
			} catch (SQLException se) {
				throw new BTSLBaseException("PromotionalTransfServiceImpl", "modifyPromoTransferData",
						"Error while close connection", se);
			}
	}		
	
	return modPromoTransferRuleRespVO;
	
}

@Override
public DeletePromoTransferRespVO deletePromoTransferData(DeletePromoTransferReqVO deletePromoTransferReqVO,
		String userLoginId, Locale locale) throws BTSLBaseException {
	
	
	final String methodName = "deletePromoTransferData";
	MComConnection mcomCon = new MComConnection();
	Connection con = null;
	UserDAO userDAO = new UserDAO();
	DeletePromoTransferRespVO delPromoTransferRuleRespVO = new DeletePromoTransferRespVO();
	String senderSubscriberType = null;
	TransferWebDAO transferWebDAO = new TransferWebDAO();
	boolean isRecordUpdate = false;
	CommonUtil commUtil = new CommonUtil();
	String fromTime = null;
	String toTime = null;
	int deleteCount = 0;
	try {
		con = mcomCon.getConnection();
		UserVO userVO = userDAO.loadUsersDetailsByLoginID(con, userLoginId);
		validateAddFieldData(deletePromoTransferReqVO);

		List<MasterErrorList> listOfErrors = new ArrayList<MasterErrorList>();

		String format = Constants.getProperty("PROMOTIONAL_TRANSFER_DATE_FORMAT");
		final Date currentDate = new Date();
		final String module = PretupsI.PROMOTIONAL_BATCH_TRF_MODULE;
        final String ruleType = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_TYP;
        final String network_code = userVO.getNetworkID();
        final String staus = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_STATUS;
        final String senderServiceClassID = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_SENDER_SERVICE_CLASS_ID;

		//
		if (BTSLUtil.isNullString(format)) {
			format = PretupsI.TIMESTAMP_DATESPACEHHMM;
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
					EventLevelI.INFO, "PromotionalTransferRuleController[addPromoTransferData]", "", "", "",
					"Missing entry from Constants.props for PROMOTIONAL_TRANSFER_DATE_FORMAT. Taking 'dd/MM/yy HH:mm' as default value.");
		}

		senderSubscriberType = getAddSenderSubscriberType(deletePromoTransferReqVO);

		if (deletePromoTransferReqVO != null && BTSLUtil.isNullOrEmptyList(deletePromoTransferReqVO.getList())) {
			throw new BTSLBaseException(this, methodName, PretupsI.NO_RULE_DATA);
		}
		
		List<TransferRulesVO> listofTransferRule = new ArrayList<TransferRulesVO>();

		for (int i = 0; i < deletePromoTransferReqVO.getList().size(); i++) {
			TransferRulesVO transferRuleVO = new TransferRulesVO();
			ReceiverSectionInputs receiverSectionInputRow = deletePromoTransferReqVO.getList().get(i);
			transferRuleVO.setModule(module);
			transferRuleVO.setNetworkCode(userVO.getNetworkID());
			transferRuleVO.setSenderSubscriberType(senderSubscriberType);
			setTransferRuleVO(transferRuleVO, receiverSectionInputRow,listOfErrors, locale);
			transferRuleVO.setRuleLevel(deletePromoTransferReqVO.getPromotionalLevel());
			transferRuleVO.setSenderSubscriberType(senderSubscriberType);
			transferRuleVO.setNetworkCode(userVO.getNetworkID());
			transferRuleVO.setPromotionCode(deletePromoTransferReqVO.getOptionTab());
			transferRuleVO.setRuleLevel(deletePromoTransferReqVO.getOptionTab());
			transferRuleVO.setReceiverSubscriberType(receiverSectionInputRow.getType());
			transferRuleVO.setMultipleSlab(receiverSectionInputRow.getTimeSlabs());			
			isRecordUpdate = validateModifyTrfRule(con, transferRuleVO, listOfErrors, locale); // reuse method in delete
			listofTransferRule.add(transferRuleVO);
		}

		
		validateDuplicateRule(listofTransferRule,locale,listOfErrors);
		int j = 0;
		if (listOfErrors != null && listOfErrors.size() > 0) {
			ErrorMap errorMap = new ErrorMap();
			errorMap.setMasterErrorList(listOfErrors);
			delPromoTransferRuleRespVO.setErrorMap(errorMap);
			delPromoTransferRuleRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			return delPromoTransferRuleRespVO;
		}else {
		
			int transferRulesVOListSize = listofTransferRule.size();
			for (int i = 0; i < transferRulesVOListSize; i++) {
				TransferRulesVO transferRulesVO = (TransferRulesVO) listofTransferRule.get(i);
				j++;
				transferRulesVO.setNetworkCode(userVO.getNetworkID());
                transferRulesVO.setModifiedBy(userVO.getUserID());
                transferRulesVO.setModifiedOn(currentDate);
                transferRulesVO.setStatus(PretupsI.TRANSFER_RULE_STATUS_DELETE);
                deleteCount += transferWebDAO.deletePromotionalTransferRule(con, transferRulesVO);
			}
		}
		if (con != null) {
			if (deleteCount == j ) {
				mcomCon.finalCommit();
				delPromoTransferRuleRespVO.setStatus(HttpStatus.SC_OK);
				delPromoTransferRuleRespVO.setMessageCode(PretupsI.PROMO_RULE_DELETE_SUCCESS);
				delPromoTransferRuleRespVO.setMessage(
						RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_DELETE_SUCCESS, null));
				
				
				AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_PROMO_TRF_RULE_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
                adminOperationVO.setInfo(" Promotional transfer rule deleted successfully");
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);

			} else {
				mcomCon.finalRollback();
				delPromoTransferRuleRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				delPromoTransferRuleRespVO.setMessageCode(PretupsI.PROMO_RULE_DELETE_UNSUCCESS);
				delPromoTransferRuleRespVO.setMessage(
						RestAPIStringParser.getMessage(locale, PretupsI.PROMO_RULE_DELETE_UNSUCCESS, null));
				AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_PROMO_TRF_RULE_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
                adminOperationVO.setInfo(" Promotional transfer rule deletion failed");
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
			}
		}

	} catch (SQLException se) {
		throw new BTSLBaseException("PromotionalTransfServiceImpl", "modifyPromoTransferData",
				"Error while executing sql statement", se);
	} catch (ParseException pe) {
		throw new BTSLBaseException("PromotionalTransfServiceImpl", "modifyPromoTransferData",
				"Error while parsing date", pe);
	} catch (BTSLBaseException se) {
		throw new BTSLBaseException("PromotionalTransfServiceImpl", "modifyPromoTransferData", "Generic exception",
				se);
	} finally {
		if (mcomCon != null) {
			mcomCon.close("");
			mcomCon = null;
		}
		if (con != null)
			try {
				con.close();
			} catch (SQLException se) {
				throw new BTSLBaseException("PromotionalTransfServiceImpl", "modifyPromoTransferData",
						"Error while close connection", se);
			}
	}		
	
	return delPromoTransferRuleRespVO;

	
}
	
   

}
