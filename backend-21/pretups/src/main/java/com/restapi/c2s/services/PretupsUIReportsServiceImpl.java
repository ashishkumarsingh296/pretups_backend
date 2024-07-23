package com.restapi.c2s.services;

import com.btsl.common.*;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.*;
import com.btsl.pretups.channel.transfer.businesslogic.*;
import com.btsl.pretups.channel.transfer.requesthandler.*;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductCache;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserMsisdnUserIDVO;
import com.btsl.pretups.user.businesslogic.ViewOfflineReportStatusVO;
import com.btsl.user.businesslogic.OfflineReportDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.restapi.c2s.services.repository.RestUserDAO;
import com.web.pretups.channel.profile.businesslogic.CommissionProfileWebDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * @author Subesh KCV
 *
 */
@Service
public class PretupsUIReportsServiceImpl extends CommonService implements PretupsUIReportsServiceI {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	@Autowired
	EventProcessorFactory eventProcessorFactory;
	@Autowired
	PassbookOthersProcessor passbookOthersProcessor;
	@Autowired
	PinpassHistServiceProcessor pinpassHistServiceProcessor;
	@Autowired
	C2STransferCommReportProcessor c2STransferCommReportProcessor;
	@Autowired
	C2CTransferCommReportProcessor c2CTransferCommReportProcessor;
	@Autowired
	O2CTransAcknowldgeReportProcess o2CTransAcknowldgeReportProcess;
	@Autowired
	O2CTransfDetailReportProcess o2CTransfDetailReportProcess;
	@Autowired
	AddtnlCommSummryReportProcess additionalcommSlabDetailsProcess;
	@Autowired
	BulkUserAddRptProcess bulkUserAddRptProcess;
	
	
	@Override
	public void getPassBookSearchInfo(String msisdn, PassbookSearchInfoRequestVO passbookSearchInfoRequestVO,
			Locale locale, PassbookSearchInfoResponse response) throws BTSLBaseException {

		final String methodName = "getPassBookSearchInfo";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ChannelUserDAO channelUserDAO = null;
		NetworkProductDAO networkProductDAO = null;
		ChannelUserVO channelUserVO = null;
		PassbookSearchInputVO passbookSearchInputVO = new PassbookSearchInputVO();
		Date currentDate = new Date();

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			userDao = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
			networkProductDAO = new NetworkProductDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			String fromDate = passbookSearchInfoRequestVO.getData().getFromDate();
			String toDate = passbookSearchInfoRequestVO.getData().getToDate();
			String extNgCode = passbookSearchInfoRequestVO.getData().getExtnwcode();
			String productCode = passbookSearchInfoRequestVO.getData().getProductCode();

			Date frDate = new Date();
			Date tDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
			sdf.setLenient(false);

			frDate = sdf.parse(fromDate + " 00:00:00");
			tDate = sdf.parse(toDate + " 23:59:59");

			if (BTSLUtil.getDifferenceInUtilDates(frDate, currentDate) < 0) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE, 0, null);
			}
			if (BTSLUtil.getDifferenceInUtilDates(tDate, currentDate) < 0) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
						PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE, 0, null);
			}
			if (BTSLUtil.getDifferenceInUtilDates(frDate, tDate) < 0) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE, 0, null);
			}
			final String status = "'" + PretupsI.YES + "'";
			HashMap<String, ProductVO> hmap = NetworkProductCache
					.getNetworkProductCacheFromRedis("NetworkProductCache");
			List productList = networkProductDAO.loadProductList(con, "PREPROD", status, PretupsI.C2S_MODULE,
					extNgCode);

			Map<String, ProductVO> map = (Map<String, ProductVO>) productList.stream()
					.collect(Collectors.toMap(ProductVO::getProductCode, productVO -> productVO));
			passbookSearchInputVO.setProductCode(productCode);
			if (productCode != null && !productCode.equals(PretupsI.ALL)) {
				if (!map.containsKey(productCode)) {

					throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
							PretupsErrorCodesI.VAS_PROMOVAS_REQ_SELECTOR_MISSING, 0, null);
				}
				passbookSearchInputVO.setProductCode(productCode);
			}
			
			NetworkVO networkVO =	 (NetworkVO) NetworkCache.getObject(extNgCode);
			
			 if(BTSLUtil.isNullObject(networkVO)   ) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			 }

			passbookSearchInputVO.setFromDate(BTSLDateUtil.getGregorianDate(fromDate));
			passbookSearchInputVO.setToDate(BTSLDateUtil.getGregorianDate(toDate));
			passbookSearchInputVO.setUserId(channelUserVO.getUserID());
			passbookSearchInputVO.setNetworkCode(extNgCode);

			List<PassbookSearchRecordVO> listPassbookSearch = userDao.searchPassbookDetailList(con,
					passbookSearchInputVO);
		
			 if(listPassbookSearch!=null && listPassbookSearch.size()==0   ) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			
			
			response.setPassbookSearchRecordVO(listPassbookSearch);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsController");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

	}



	@Override
	public List<PinPassHistSearchRecordVO>  getPinPasshistSearchInfo(PinPassHistoryReqDTO pinPassHistoryReqDTO, PinPassHistorySearchResp response)
			throws BTSLBaseException {
		
		final String methodName = "getPinPasshistSearchInfo";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		List<PinPassHistSearchRecordVO> listPinPassSearchRecordVO =null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		
			channelUserDAO = new ChannelUserDAO();
			UserDAO userDAO = new UserDAO();
			UserVO userVO = new UserVO();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, pinPassHistoryReqDTO.getUserId());
//			channelUserVO = channelUserDAO.loadChannelUserDetails(con, pinPassHistoryReqDTO.getMsisdn());
			pinPassHistoryReqDTO.setUserId(userVO.getUserID());
			pinpassHistServiceProcessor.validateInputs(con, pinPassHistoryReqDTO);
			listPinPassSearchRecordVO= pinpassHistServiceProcessor.searchPinPassHist(con,
					pinPassHistoryReqDTO);
			response.setPinPassHistSearchVOList(listPinPassSearchRecordVO);
			
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		return listPinPassSearchRecordVO;
		
	}


	
	
	@Override
	public void  downloadPinPassHistData(PinPassHistoryReqDTO pinPassHistoryReqDTO, PinPassHistDownloadResp response)
			throws BTSLBaseException {
		
		final String methodName = "getPinPasshistSearchInfo";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		List<PinPassHistSearchRecordVO> listPinPassSearchRecordVO =null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		
			channelUserDAO = new ChannelUserDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, pinPassHistoryReqDTO.getMsisdn());
			pinPassHistoryReqDTO.setUserId(channelUserVO.getUserID());
			pinpassHistServiceProcessor.validateInputs(con, pinPassHistoryReqDTO);
			pinpassHistServiceProcessor.execute(con,pinPassHistoryReqDTO, response);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		
		
	}



	@Override
	public void getC2StransferCommissionInfo(C2STransferCommReqDTO c2STransferCommReqDTO,
			C2StransferCommisionResp response) throws BTSLBaseException {
		final String methodName = "getC2StransferCommissionInfo";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		
		C2StransferCommRespDTO  c2StransferCommRespDTO =null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		
			channelUserDAO = new ChannelUserDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, c2STransferCommReqDTO.getMsisdn());
			c2STransferCommReqDTO.setUserId(channelUserVO.getUserID());
			c2STransferCommReportProcessor.validateInputs(con, c2STransferCommReqDTO);
//			c2StransferCommRespDTO= c2STransferCommReportProcessor.searchC2STransferCommission(
//					c2STransferCommReqDTO);
    		
			response.setC2stransferCommissionList(c2StransferCommRespDTO.getListC2sTransferCommRecordVO());
			response.setC2StransferCommSummryData(c2StransferCommRespDTO.getC2StransferCommSummryData());
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(c2STransferCommReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		
		

		
	}



	@Override
	public Map<String,String> downloadC2StransferCommData(C2STransferCommReqDTO c2sTransferCommReqDTO,
			C2STransferCommDownloadResp response) throws BTSLBaseException {
		
		final String methodName = "downloadC2StransferCommData";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		String success=null;
		String resmsg=null;
		HashMap<String,String> mp = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			boolean reportOffline=  (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_OFFLINE);
           if(!reportOffline) {
	   			channelUserDAO = new ChannelUserDAO();
	   			channelUserVO = channelUserDAO.loadChannelUserDetails(con, c2sTransferCommReqDTO.getMsisdn());
	   			c2sTransferCommReqDTO.setUserId(channelUserVO.getUserID());
	   			c2sTransferCommReqDTO.setOffline(false);
	   			mp =c2STransferCommReportProcessor.execute(
	   					c2sTransferCommReqDTO,response);
//	   			 success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
//				response.setStatus(success);
//				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				 resmsg = RestAPIStringParser.getMessage(c2sTransferCommReqDTO.getLocale(),response.getMessageCode(), null);
           }else
           {
        	   
        	   HashMap<String,String> rptInputParams =c2STransferCommReportProcessor.validateInputs(con,c2sTransferCommReqDTO);

        		 precheckOfflineValidations(con ,c2sTransferCommReqDTO.getUserId(),EventProcessorID.OFFLINE_101.getEventProcessorID()); 
        	   	  ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider();
				  PretupsBusinessServiceI pretupsService = eventProcessorFactory.getEventProcessorService(PretupsEventTypes.OFFLINE_REPORT_EVENT);
				  EventObjectData eventObjectData = new EventObjectData();
				  eventObjectData.setEventProcessingID(com.btsl.common.EventProcessorID.OFFLINE_101.getEventProcessorID());
				  eventObjectData.setRequestData(c2sTransferCommReqDTO);
				  eventObjectData.setFileExtension(c2sTransferCommReqDTO.getFileType());
				  eventObjectData.setEventInitiatedBy(c2sTransferCommReqDTO.getUserId());
				  c2sTransferCommReqDTO.setOffline(true);
				  pretupsService.executeOffineService(eventObjectData);
				  success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
				  response.setStatus(success);
					response.setMessageCode(PretupsErrorCodesI.OFFLINERPT_PROCESS_INITIATED);
					 resmsg = RestAPIStringParser.getMessage(c2sTransferCommReqDTO.getLocale(),
							PretupsErrorCodesI.OFFLINERPT_PROCESS_INITIATED, null);
           }	
    		response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

    return mp;
	}


	public void precheckOfflineValidations(Connection con ,String loggedInUserID,String reportID) throws BTSLBaseException {
		final String methodName ="precheckOfflineValidations";
		OfflineReportDAO offlineReportDAO= new OfflineReportDAO(); 
		
		boolean allowSameReportExecution = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_SAME_REPORT_EXEC);
		Integer totalReporExecInParallelPerUser = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.TOT_RPT_EXEC_PERUSER);
		
		 if(!allowSameReportExecution  && offlineReportDAO.checkSameReportAlreadyExecuting(con, reportID,loggedInUserID)) {
			 throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.SAME_REPORT_ALREADY_EXECUTING, 0, null); 
		 }
		
		 int totCount =offlineReportDAO.getTotCountRprtRunningParallelByUser(con, loggedInUserID); 
		  if(totCount>totalReporExecInParallelPerUser.intValue()) {
				throw new BTSLBaseException(PretupsUIReportsServiceImpl.class.getName(), methodName,
	        			PretupsErrorCodesI.TOTAL_ALLOWED_RPT_EXEC, new String[] {String.valueOf(totalReporExecInParallelPerUser.intValue())});
		  }
		
	}

	@Override
	public void getC2CtransferCommissionInfo(C2CTransferCommReqDTO c2cTransferCommReqDTO,
			C2CtransferCommisionResp response) throws BTSLBaseException {
		final String methodName = "getC2StransferCommissionInfo";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		
		C2CtransferCommRespDTO  c2cTransferCommRespDTO =null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
		
			channelUserDAO = new ChannelUserDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, c2cTransferCommReqDTO.getMsisdn());
			c2cTransferCommReqDTO.setUserId(channelUserVO.getUserID());
			c2CTransferCommReportProcessor.validateInputs(con, c2cTransferCommReqDTO);
			
			c2cTransferCommRespDTO= c2CTransferCommReportProcessor.searchC2CTransferCommission(
					c2cTransferCommReqDTO);
    		response.setC2ctransferCommissionList(c2cTransferCommRespDTO.getListC2CTransferCommRecordVO());
			
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(c2cTransferCommReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
		
	}


	
	
	
		

	@Override
	public void getUserNameAutoSearchData(UserNameAutoSearchReqDTO userNameAutoSearchReqDTO,
			FetchUserNameAutoSearchRespVO response) throws BTSLBaseException {
		final String methodName = "getUserNameAutoSearchData";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserDAO = new ChannelUserDAO();
			List<UserMsisdnUserIDVO> listUserMsisdnUserIDVO =channelUserDAO.loadUserNameAutoSearchOnZoneDomainCategoryQry(null, userNameAutoSearchReqDTO.getUserName(), con, userNameAutoSearchReqDTO.getCategoryCode(), userNameAutoSearchReqDTO.getDomainCode(), userNameAutoSearchReqDTO.getUserId(), userNameAutoSearchReqDTO.getGeography(),null);
			if(listUserMsisdnUserIDVO==null ||(listUserMsisdnUserIDVO!=null && listUserMsisdnUserIDVO.isEmpty())){
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			}
			
			response.setUserList(listUserMsisdnUserIDVO);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(userNameAutoSearchReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
		
	}



	@Override
	public Map<String,String> downloadC2CtransferCommData(C2CTransferCommReqDTO c2cTransferCommReqDTO,
			C2CTransferCommDownloadResp response) throws BTSLBaseException {
		
		final String methodName = "downloadC2CtransferCommData";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		String success=null;
		String resmsg=null;
		HashMap<String,String> mp=null;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserDAO = new ChannelUserDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, c2cTransferCommReqDTO.getMsisdn());
			c2cTransferCommReqDTO.setUserId(channelUserVO.getUserID());
		
			boolean reportOffline=  (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_OFFLINE);
			
		       if(!reportOffline) {
		   			channelUserDAO = new ChannelUserDAO();
		   			channelUserVO = channelUserDAO.loadChannelUserDetails(con, c2cTransferCommReqDTO.getMsisdn());
		   			c2cTransferCommReqDTO.setUserId(channelUserVO.getUserID());
		   			c2cTransferCommReqDTO.setOffline(false);
		   			mp=c2CTransferCommReportProcessor.execute(
		   					c2cTransferCommReqDTO,response);
//		   			 success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
//					response.setStatus(success);
//					response.setMessageCode(PretupsErrorCodesI.SUCCESS);
					 resmsg = RestAPIStringParser.getMessage(c2cTransferCommReqDTO.getLocale(),
							PretupsErrorCodesI.SUCCESS, null);
	           }else
	           {
	        	   
	        	   HashMap<String,String> rptInputParams =c2CTransferCommReportProcessor.validateInputs(con,c2cTransferCommReqDTO);
	        		 precheckOfflineValidations(con ,c2cTransferCommReqDTO.getUserId(),EventProcessorID.OFFLINE_103.getEventProcessorID()); 
	        	   	  ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider();
					  PretupsBusinessServiceI pretupsService = eventProcessorFactory.getEventProcessorService(PretupsEventTypes.OFFLINE_REPORT_EVENT);
					  EventObjectData eventObjectData = new EventObjectData();
					  eventObjectData.setEventProcessingID(com.btsl.common.EventProcessorID.OFFLINE_103.getEventProcessorID());
					  eventObjectData.setRequestData(c2cTransferCommReqDTO);
					  eventObjectData.setFileExtension(c2cTransferCommReqDTO.getFileType());
					  eventObjectData.setEventInitiatedBy(c2cTransferCommReqDTO.getUserId());
					  c2cTransferCommReqDTO.setOffline(true);
					  pretupsService.executeOffineService(eventObjectData);
					  success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
					  response.setStatus(success);
						response.setMessageCode(PretupsErrorCodesI.OFFLINERPT_PROCESS_INITIATED);
						 resmsg = RestAPIStringParser.getMessage(c2cTransferCommReqDTO.getLocale(),
								PretupsErrorCodesI.OFFLINERPT_PROCESS_INITIATED, null);
	           }	
	    		response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		
		return mp;
		
	}



	@Override
	public void getParentOwnerProfileInfo(GetParentOwnerProfileReq getParentOwnerProfileReq,
			GetParentOwnerProfileRespVO response) throws BTSLBaseException {
		final String methodName = "getParentOwnerProfileInfo";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;

		RestUserDAO restUserDAO= new RestUserDAO();
		UserDAO userDAO= new UserDAO();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			GetParentOwnerProfileRespVO getParentOwnerProfileRespVO=null;
			ChannelUserVO inputUserIDVO = userDAO.loadUserDetailsFormUserID(con,getParentOwnerProfileReq.getUserId());
			if(BTSLUtil.isNullObject(inputUserIDVO)) {
			  getParentOwnerProfileRespVO =restUserDAO.getParentOwnerProfileInfoForAllUsers(con, getParentOwnerProfileReq);
			}
			else {
				 getParentOwnerProfileRespVO =restUserDAO.getParentOwnerProfileInfo(con, getParentOwnerProfileReq);
			}
			if(BTSLUtil.isNullorEmpty(getParentOwnerProfileRespVO)) {
				throw new BTSLBaseException("PretupsUIReportsServiceImpl", methodName,
						PretupsErrorCodesI.NO_USER_EXIST, 0, null);
			}
			response.setUserName(getParentOwnerProfileRespVO.getUserName());
			response.setEmailID(getParentOwnerProfileRespVO.getEmailID());
			CategoryGradeDAO channeluserDao=new CategoryGradeDAO();
			
			ArrayList gradeList = channeluserDao.loadGradeList(con);
			for(int i=0;i<gradeList.size();i++) {
				GradeVO vo = (GradeVO)gradeList.get(i);
				String grade = Optional.ofNullable(getParentOwnerProfileRespVO.getGrade()).orElse("");
				if (grade.equals(vo.getGradeCode())) {
					response.setGrade(vo.getGradeName());
				}
			}
			
			response.setAddress(getParentOwnerProfileRespVO.getAddress());
			response.setMsisdn(getParentOwnerProfileRespVO.getMsisdn());
			response.setStatus(getParentOwnerProfileRespVO.getStatus());
			response.setParentName(getParentOwnerProfileRespVO.getParentName());
			response.setParentMobileNumber(getParentOwnerProfileRespVO.getParentMobileNumber());
			response.setParentCategoryName(getParentOwnerProfileRespVO.getParentCategoryName());
			response.setOwnerName(getParentOwnerProfileRespVO.getOwnerName());
			response.setOwnerMobileNumber(getParentOwnerProfileRespVO.getOwnerMobileNumber());
			response.setOwnerCategoryName(getParentOwnerProfileRespVO.getOwnerCategoryName());
			response.setParentUserID(getParentOwnerProfileRespVO.getParentUserID());
			response.setUserNamePrefix(getParentOwnerProfileRespVO.getUserNamePrefix());
			response.setShortName(getParentOwnerProfileRespVO.getShortName());
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			Locale locale=getParentOwnerProfileReq.getLocale();
			if(locale==null) {
				 locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
			}
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		
		
		
	}

	@Override
	public void getCommissionSlabDetails(GetCommissionSlabReqVO getCommissionSlabReqVO, GetCommissionSlabResp response)
			throws BTSLBaseException {
		
		final String methodName = "getCommissionSlabDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		UserDAO userDAO= new UserDAO();
		CommissionProfileWebDAO commissionProfileWebDAO=new CommissionProfileWebDAO();
		CommissionProfileSetVO commissionProfileSetVO = null;
		try {			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserDAO= new ChannelUserDAO();
			List<UserMsisdnUserIDVO> listUserMsisdnUserIDVO=null;
			
			if(!BTSLUtil.isEmpty(getCommissionSlabReqVO.getUserId())  && !getCommissionSlabReqVO.getUserId().trim().toUpperCase().equals(PretupsI.ALL)  ) {
				try {
				// c2CTransferCommReqDTO.getUser() Will contain UsrID , for Validation, For swagger purpose 
					 listUserMsisdnUserIDVO =channelUserDAO.loadUserNameAutoSearchOnZoneDomainCategoryQry(null, null, con, getCommissionSlabReqVO.getCategoryCode(), getCommissionSlabReqVO.getDomainCode(), getCommissionSlabReqVO.getLoggedInUserID(), getCommissionSlabReqVO.getGeography(),getCommissionSlabReqVO.getUserId());
					if(listUserMsisdnUserIDVO ==null || (listUserMsisdnUserIDVO!=null &&  listUserMsisdnUserIDVO.isEmpty() )) {
						throw new BTSLBaseException("PretupsUIReportsController", methodName,
								PretupsErrorCodesI.NO_USER_EXIST, 0, null);
					}
				} catch(SQLException sqex) {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.NO_USER_EXIST, 0, null);
				}
			}
			
			
			
			
			channelUserDAO = new ChannelUserDAO();
			CommissionProfileDAO commissionProfileDAO=new CommissionProfileDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, getCommissionSlabReqVO.getMsisdn());
			getCommissionSlabReqVO.setUserId(channelUserVO.getUserID());
			commissionProfileSetVO=commissionProfileWebDAO.loadUserCommSetID(con, getCommissionSlabReqVO.getUserId());
			String serviceAllowed=commissionProfileWebDAO.serviceAllowed(con, getCommissionSlabReqVO.getCategoryCode());
			CommissionProfileSetVersionVO commissionProfileSetVersionVO=commissionProfileWebDAO.loadCommProfileSetLatestVersionDetails(con, commissionProfileSetVO.getCommProfileSetId(), new Date());
			final int hour = BTSLUtil.getHour(commissionProfileSetVersionVO.getApplicableFrom());
	         final int minute = BTSLUtil.getMinute(commissionProfileSetVersionVO.getApplicableFrom());
	         final String time = BTSLUtil.getTimeinHHMM(hour, minute);

			response.setApplicableFrom(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(commissionProfileSetVersionVO.getApplicableFrom())) + ", " + time);
			
			response.setCommissionType(commissionProfileSetVersionVO.getDualCommissionType());
			final ArrayList dualCommissionTypeList = LookupsCache.loadLookupDropDown(PretupsI.DUAL_COMM_TYPE, true);
			ListValueVO   listValueVO = BTSLUtil.getOptionDesc(commissionProfileSetVersionVO.getDualCommissionType(), dualCommissionTypeList);
			response.setCommissionType(listValueVO.getLabel());
			setcommissionSlabSectionUI(con, getCommissionSlabReqVO, commissionProfileSetVO, commissionProfileSetVersionVO ,response);	
			setCBCSlabSectionUI(con, getCommissionSlabReqVO, commissionProfileSetVO, commissionProfileSetVersionVO ,response);
			setAdditionalCommissionSlabSectionUI(con, getCommissionSlabReqVO, commissionProfileSetVO, commissionProfileSetVersionVO ,response);		
			
			
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(getCommissionSlabReqVO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		
		
		
		
		
		
	}
	
	
	public void setcommissionSlabSectionUI(Connection con,GetCommissionSlabReqVO getCommissionSlabReqVO,CommissionProfileSetVO commissionProfileSetVO,CommissionProfileSetVersionVO commissionProfileSetVersionVO ,GetCommissionSlabResp response) throws BTSLBaseException {
		CommissionProfileWebDAO commissionProfileWebDAO=new CommissionProfileWebDAO();
		CommissionProfileDAO commissionProfileDAO=new CommissionProfileDAO();
		final ListSorterUtil sort = new  ListSorterUtil();
	    final List productList = commissionProfileDAO.loadCommissionProfileProductsList(con, commissionProfileSetVO.getCommProfileSetId(), commissionProfileSetVersionVO.getCommProfileSetVersion());
	     if (productList != null && !productList.isEmpty()) {
	    	 CommissionProfileProductsVO commissionProfileProductsVO=null;
//       CommissionProfileCombinedVO commissionProfileCombinedVO;
             List<CommissionSlabDetails> commissionSlabList =null;
             List<CommissionSlabDetVO>  listCommisionSlaDetVO =new ArrayList<CommissionSlabDetVO>();
             int productsLists=productList.size();
             for (int i = 0, j = productsLists; i < j; i++) {
                 commissionProfileProductsVO = (CommissionProfileProductsVO) productList.get(i);
                 CommissionSlabDetVO commissionSlabDetVO = new CommissionSlabDetVO();
                 commissionSlabDetVO.setProduct(commissionProfileProductsVO.getProductCodeDesc());
                 commissionSlabDetVO.setMultipleOf(commissionProfileProductsVO.getTransferMultipleOffAsString());
                 commissionSlabDetVO.setTransactionType(commissionProfileProductsVO.getTransactionType());
                 commissionSlabDetVO.setPaymentMode(commissionProfileProductsVO.getPaymentMode());
                 commissionSlabDetVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValueAsString());
                 commissionSlabDetVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValueAsString());
                 commissionSlabDetVO.setTaxCalcOnFOC(commissionProfileProductsVO.getTaxOnFOCApplicable());
                 commissionSlabDetVO.setTaxCalcOnC2CTransfer(commissionProfileProductsVO.getTaxOnChannelTransfer());
                 commissionSlabList = commissionProfileWebDAO.getCommissionProfileDetailList(con, commissionProfileProductsVO.getCommProfileProductID(),getCommissionSlabReqVO.getExtnwcode());
                 commissionSlabDetVO.setListCommissionSlabDet(commissionSlabList);
                 listCommisionSlaDetVO.add(commissionSlabDetVO);
             }
             response.setListcommissionSlabDetVO(listCommisionSlaDetVO);
         }
		
	}
	
	
	
	
	public void setCBCSlabSectionUI(Connection con,GetCommissionSlabReqVO getCommissionSlabReqVO,CommissionProfileSetVO commissionProfileSetVO,CommissionProfileSetVersionVO commissionProfileSetVersionVO ,GetCommissionSlabResp response) throws BTSLBaseException {	
		CommissionProfileDAO commissionProfileDAO=new CommissionProfileDAO();
	final ArrayList<OtfProfileCombinedVO> otfProfileList =new ArrayList<OtfProfileCombinedVO>();
	List<CBCcommSlabDetVO> listcBCcommSlabDetVO = new ArrayList<>();
	List<CommisionCBCDetails> listCBCCommsionDetails=new ArrayList<>();
	 //using existing methods
    ArrayList otfProfileVOList = commissionProfileDAO.loadOtfProfileVOList(con, commissionProfileSetVO.getCommProfileSetId(), commissionProfileSetVersionVO.getCommProfileSetVersion());
    if (otfProfileVOList != null && !otfProfileVOList.isEmpty()){
   	 
   	 for(int i =0 ; i<otfProfileVOList.size(); i++){
   		 OtfProfileCombinedVO otfProfileCombinedVO = new OtfProfileCombinedVO();
   		 OtfProfileVO otfProfileVO = (OtfProfileVO)otfProfileVOList.get(i);
   		CBCcommSlabDetVO cBCcommSlabDetVO = new CBCcommSlabDetVO();
   		cBCcommSlabDetVO.setProduct(otfProfileVO.getProductCodeDesc());
   		cBCcommSlabDetVO.setCbcTimeSlab(otfProfileVO.getOtfTimeSlab()); 
   		cBCcommSlabDetVO.setCbcApplicableFromNTo(otfProfileVO.getOtfApplicableFrom() + " to " +  otfProfileVO.getOtfApplicableTo());
   		listCBCCommsionDetails = commissionProfileDAO.getCBCSlabDetails(con,otfProfileVO.getCommProfileOtfID());
   		cBCcommSlabDetVO.setListCBCCommsionDetails(listCBCCommsionDetails);
   		listcBCcommSlabDetVO.add(cBCcommSlabDetVO);
   	 }
   	response.setListcBCcommSlabDetVO(listcBCcommSlabDetVO);
    }
	}

	
    public void setAdditionalCommissionSlabSectionUI(Connection con,GetCommissionSlabReqVO getCommissionSlabReqVO,CommissionProfileSetVO commissionProfileSetVO,CommissionProfileSetVersionVO commissionProfileSetVersionVO ,GetCommissionSlabResp response) throws BTSLBaseException {
    	CommissionProfileWebDAO commissionProfileWebDAO=new CommissionProfileWebDAO();
   	 
    	 List<AdditionalcommSlabVO> listAdditionalCommSlabVO = new ArrayList<>();
    	 // Reuse existing methods
    	final List<AdditionalProfileServicesVO> serviceList = commissionProfileWebDAO.loadAdditionalProfileServicesList(con, commissionProfileSetVO.getCommProfileSetId(), commissionProfileSetVersionVO.getCommProfileSetVersion());
    	   if (serviceList != null && !serviceList.isEmpty()) {
    		   AdditionalProfileServicesVO additionalProfileServicesVO=null;	   
    		   int serviceLists=serviceList.size();
               for (int i = 0, j = serviceLists; i < j; i++) {
                   additionalProfileServicesVO =serviceList.get(i);
                   AdditionalcommSlabVO additionalcommSlabVO = new AdditionalcommSlabVO();
                   additionalcommSlabVO.setService(additionalProfileServicesVO.getServiceTypeDesc());
                   additionalcommSlabVO.setSubService(additionalProfileServicesVO.getSubServiceCode());
                   additionalcommSlabVO.setApplicableFrom(additionalProfileServicesVO.getApplicableFromAdditional());
                   additionalcommSlabVO.setApplicableTo(additionalProfileServicesVO.getApplicableToAdditional());
                   additionalcommSlabVO.setTimeSlab(additionalProfileServicesVO.getAdditionalCommissionTimeSlab());
                   additionalcommSlabVO.setGateWaySelected(additionalProfileServicesVO.getGatewayCode());
                   additionalcommSlabVO.setMinTransferValue(additionalProfileServicesVO.getMinTransferValueAsString());
                   additionalcommSlabVO.setMaxTransferValue(additionalProfileServicesVO.getMaxTransferValueAsString());
                   additionalcommSlabVO.setStatus(additionalProfileServicesVO.getAddtnlComStatus());
              	 // Reuse existing methods
                   List<AdditionalcommSlabDetails> listAdditionlCommSlabDetails   = commissionProfileWebDAO.getAdditionalProfileDetailList(con, additionalProfileServicesVO.getCommProfileServiceTypeID(),getCommissionSlabReqVO.getExtnwcode());
                    if (!listAdditionlCommSlabDetails.isEmpty()) {
                       		if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,getCommissionSlabReqVO.getExtnwcode())){
                                int addProfilesDetailLists=listAdditionlCommSlabDetails.size();
                               	 for(int k =0;k<addProfilesDetailLists;k++){
                               		AdditionalcommSlabDetails aprdvo = (AdditionalcommSlabDetails)listAdditionlCommSlabDetails.get(k);
                               		listAdditionlCommSlabDetails.remove(k);
                               		//List<OTFDetailsVO> otfDetailList = commissionProfileWebDAO.loadProfileOtfDetailList(con, aprdvo.getAddtnlCommProfileDetailID(),aprdvo.getCacType(),PretupsI.COMM_TYPE_ADNLCOMM);
                               		List<OTFDetailsVO> otfDetailList = commissionProfileWebDAO.loadProfileOtfDetailList(con, aprdvo.getAddtnlCommProfileDetailID(),aprdvo.getCacDetailType(),PretupsI.COMM_TYPE_ADNLCOMM);
                                     List<CACDetails> listCACDetail  = new ArrayList<CACDetails>();                               		
	                               		 for(OTFDetailsVO otfDetailVO : otfDetailList) {
	                               			CACDetails cACDetails = new CACDetails();
	                               			cACDetails.setCacDetailValue(otfDetailVO.getOtfValue());
	                               			cACDetails.setCacDetailType(otfDetailVO.getOtfType());
	                               			cACDetails.setCacDetailRate(otfDetailVO.getOtfRate());
	                               			listCACDetail.add(cACDetails);
	                               		 }
                               		 
                               		 aprdvo.setListCACDetails(listCACDetail);
	                                	if(otfDetailList!=null && otfDetailList.size()>0) {
	                                	  aprdvo.setcACDetailsListSize(String.valueOf(otfDetailList.size()));
	                                	}
	                                	listAdditionlCommSlabDetails.add(k,aprdvo);
                                }
                   }
                       		
                       		
                       		additionalcommSlabVO.setListAdditionalCommSlabDetails(listAdditionlCommSlabDetails);
                       		listAdditionalCommSlabVO.add(additionalcommSlabVO);
                   }
    	   }
               
               response.setListAdditionalCommSlabVO(listAdditionalCommSlabVO);
               
               
    	   }
	
    	   }



	@Override
	public void getO2cTransferAcknowledgement(O2CTransfAckDownloadReqDTO getO2CTransfAcknReqVO,
			GetO2CTransferAcknowledgeResp response) throws BTSLBaseException {
		final String methodName = "getO2cTransferAcknowledgement";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		UserDAO userDAO= new UserDAO();
		
		try {			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserDAO = new ChannelUserDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, getO2CTransfAcknReqVO.getMsisdn());
			
			o2CTransAcknowldgeReportProcess.validateInputs(con, getO2CTransfAcknReqVO);
			o2CTransAcknowldgeReportProcess.getO2CTransAckReportSearch(getO2CTransfAcknReqVO, response);
			
					
			
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(getO2CTransfAcknReqVO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		

		
	}



	@Override
	public void downloadO2CTransferAcknowlege(O2CTransfAckDownloadReqDTO o2cTransfAckDownloadReqDTO,
			GetO2CTransferAckDownloadResp response) throws BTSLBaseException {
		final String methodName = "downloadO2CTransferAcknowlege";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			o2CTransAcknowldgeReportProcess.execute(
					o2cTransfAckDownloadReqDTO,response);
			
			
    		String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(o2cTransfAckDownloadReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		

	
		
	}



	@Override
	public void getO2cTransferDetails(O2CTransferDetailsReqDTO o2cTransferDetailsReqDTO,
			O2CtransferDetSearchResp response) throws BTSLBaseException {
		final String methodName = "getO2cTransferDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		UserDAO userDAO= new UserDAO();
		
		try {			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
//			channelUserDAO = new ChannelUserDAO();
//			channelUserVO = channelUserDAO.loadChannelUserDetails(con, o2cTransferDetailsReqDTO.getMsisdn());
			
			o2CTransfDetailReportProcess.validateInputs(con, o2cTransferDetailsReqDTO);
			o2CTransfDetailReportProcess.getO2CTransfDetailReportSearch(o2cTransferDetailsReqDTO, response);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(o2cTransferDetailsReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		


		
	}



	@Override
	public void downloadO2CTransferDetails(O2CTransferDetailsReqDTO o2cTransferDetailsReqDTO,
			O2CTransferDetailDownloadResp response) throws BTSLBaseException {
		final String methodName = "downloadO2CTransferAcknowlege";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			o2CTransfDetailReportProcess.execute(
					o2cTransferDetailsReqDTO,response);
    		String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(o2cTransferDetailsReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
		
	}



	@Override
	public void getAdditionCommSummryDetails(AddtnlCommSummryReqDTO addtnlCommSummryReqDTO,
			AdditionalCommissionSummryC2SResp response) throws BTSLBaseException {
		final String methodName = "getAdditionCommSummryDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			additionalcommSlabDetailsProcess.validateInputs(con, addtnlCommSummryReqDTO);
			additionalcommSlabDetailsProcess.getAddtnlCommSummryDetails(addtnlCommSummryReqDTO, response);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

		}		
	
	
	
    }


	@Override
	public void downloadAddntlCommSummry(AddtnlCommSummryReqDTO addtnlCommSummryReqDTO,
			AddtlnCommSummryDownloadResp response) throws BTSLBaseException {
		final String methodName = "downloadAddntlCommSummry";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			
			additionalcommSlabDetailsProcess.execute(addtnlCommSummryReqDTO, response);
			
			
    		String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}

		
	}
	
	
	@Override
	public void getStaffUserDetailsData(UserStaffDetailsReqDTO userStaffDetailsReqDTO,
			FetchStaffDetailsRespVO response) throws BTSLBaseException {
		final String methodName = "getUserNameAutoSearchData";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserDAO = new ChannelUserDAO();
			List<UserMsisdnUserIDVO> listUserMsisdnUserIDVO =channelUserDAO.fetchStaffUserdetailsUnderChannelUser(con, userStaffDetailsReqDTO.getCategoryCode(), userStaffDetailsReqDTO.getDomainCode(), userStaffDetailsReqDTO.getChannelUserID(), userStaffDetailsReqDTO.getGeography(),null);
			if(listUserMsisdnUserIDVO==null ||(listUserMsisdnUserIDVO!=null && listUserMsisdnUserIDVO.isEmpty())){
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			}
			
			response.setUserList(listUserMsisdnUserIDVO);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(userStaffDetailsReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
		
	}



	@Override
	public void getAllOfflineReportProcessStatus(String initiatedUserID,ViewAllOfflineRptStatusRespVO response,Locale locale) throws BTSLBaseException {
		final String methodName = "getAllOfflineReportProcessStatus";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		OfflineReportDAO offlineReportDAO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			offlineReportDAO = new OfflineReportDAO();
			List<ViewOfflineReportStatusVO> listOfflineReportStatus =offlineReportDAO.getAllOfflineReportProcessStatus(con, initiatedUserID);
			if(listOfflineReportStatus==null ||(listOfflineReportStatus!=null && listOfflineReportStatus.isEmpty())){
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			}
			
			response.setOfflineReportStatusList(listOfflineReportStatus);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}		
		
	}

	@Override
	public void delegateOfflineAction(OfflineReportActionReqDTO offlineReportActionReqDTO ,OfflineReportActionResp response) throws BTSLBaseException {
		final String methodName = "getAllOfflineReportProcessStatus";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		OfflineReportDAO offlineReportDAO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			String resmsg = null;
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessage(resmsg);

			if(offlineReportActionReqDTO.getReportAction().equals(PretupsI.OFFLINE_REPORTACTION_CANCEL)) {
				OfflineReportRunningThreadMap.pushCancelOfflineTaskThread(offlineReportActionReqDTO.getReportTaskID());
				resmsg = RestAPIStringParser.getMessage(offlineReportActionReqDTO.getLocale(),
						PretupsErrorCodesI.SUCCESS, null);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			}else {
		 	offlineReportDAO = new OfflineReportDAO();
	     	offlineReportDAO.deleteOfflineReportTaskStatus(con, offlineReportActionReqDTO.getReportTaskID());
			File file = new File(offlineReportActionReqDTO.getOfflineDownloadPath() +offlineReportActionReqDTO.getFileName());
			    if(file.delete()) {
			    	resmsg = RestAPIStringParser.getMessage(offlineReportActionReqDTO.getLocale(),
							PretupsErrorCodesI.OFFLINE_FILE_DELETE_SUCCESS, null);
					response.setMessageCode(PretupsErrorCodesI.OFFLINE_FILE_DELETE_SUCCESS);
			    }else {
			    	resmsg = RestAPIStringParser.getMessage(offlineReportActionReqDTO.getLocale(),
							PretupsErrorCodesI.OFFLINE_FILE_DELETE_FAILED, null);
					response.setMessageCode(PretupsErrorCodesI.OFFLINE_FILE_DELETE_FAILED);
			    }
			
			}
			response.setMessage(resmsg);
	
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}		
		
	}
	
	
	@Override
	public void downloadPassbookOthersData(PassbookOthersReqDTO passbookOthersReqDTO,
			PassbookOthersDownloadResp passbookOthersDownloadResp) throws BTSLBaseException {
		
		final String methodName = "downloadC2StransferCommData";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		String success=null;
		String resmsg=null;

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			boolean reportOffline=  (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_OFFLINE);
           if(!reportOffline) {
	   			channelUserDAO = new ChannelUserDAO();
	   			channelUserVO = channelUserDAO.loadChannelUserDetails(con, passbookOthersReqDTO.getMsisdn());
	   			passbookOthersReqDTO.setUserId(channelUserVO.getUserID());
	   			passbookOthersReqDTO.setOffline(false);
	   			passbookOthersProcessor.execute(
	   					passbookOthersReqDTO,passbookOthersDownloadResp);
	   			 success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
	   			passbookOthersDownloadResp.setStatus(success);
	   			passbookOthersDownloadResp.setMessageCode(PretupsErrorCodesI.SUCCESS);
				 resmsg = RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
						PretupsErrorCodesI.SUCCESS, null);
           }else
           {
        	   HashMap<String,String> rptInputParams =passbookOthersProcessor.validateInputs(con,passbookOthersReqDTO);
        	   passbookOthersProcessor.precheckOfflineValidations(con ,passbookOthersReqDTO.getUserId(),EventProcessorID.OFFLINE_102.getEventProcessorID()); 
//        	   	  ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider();
				  PretupsBusinessServiceI pretupsService = eventProcessorFactory.getEventProcessorService(PretupsEventTypes.OFFLINE_REPORT_EVENT);
				  EventObjectData eventObjectData = new EventObjectData();
				  eventObjectData.setEventProcessingID(com.btsl.common.EventProcessorID.OFFLINE_102.getEventProcessorID());
				  eventObjectData.setRequestData(passbookOthersReqDTO);
				  eventObjectData.setFileExtension(passbookOthersReqDTO.getFileType());
				  eventObjectData.setEventInitiatedBy(passbookOthersReqDTO.getUserId());
				  passbookOthersReqDTO.setOffline(true);
				  pretupsService.executeOffineService(eventObjectData);
				  success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
				  passbookOthersDownloadResp.setStatus(success);
				  passbookOthersDownloadResp.setMessageCode(PretupsErrorCodesI.OFFLINERPT_PROCESS_INITIATED);
					 resmsg = RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
							PretupsErrorCodesI.OFFLINERPT_PROCESS_INITIATED, null);
           }	
           passbookOthersDownloadResp.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("PretupsUIReportsServiceImpl");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}


	}



	@Override
	public void  searchBulkUserAddStatus(BulkUserAddRptReqDTO bulkUserAddRptReqDTO,ChannelUserVO channelUserVO,BulkUserAddStatusRptResp bulkUserAddStatusRptResp) throws BTSLBaseException {
		final String methodName = "searchBulkUserAddStatus";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
	try {	
		bulkUserAddRptProcess.searchBulkUserAddRpt(bulkUserAddRptReqDTO, channelUserVO, bulkUserAddStatusRptResp);
   		String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
   		bulkUserAddStatusRptResp.setStatus(success);
   		bulkUserAddStatusRptResp.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			bulkUserAddStatusRptResp.setMessage(resmsg);
	} catch (BTSLBaseException be) {
		_log.errorTrace(methodName, be);
		throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
				be.getArgs());
	} catch (Exception ex) {
		_log.errorTrace(methodName, ex);
		throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
	} finally {
			}
			

		

	}

	@Override
	public void downloadBulkUserAddStsDetails(BulkUserAddRptReqDTO bulkUserAddRptReqDTO,ChannelUserVO channelUserVO,BulkuserAddStsDownloadResp response) throws BTSLBaseException
	 {
		final String methodName = "downloadBulkUserAddStsDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
	try {	
		bulkUserAddRptProcess.downloadBulkUserAddRpt(bulkUserAddRptReqDTO, channelUserVO, response);
   		String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
   		response.setStatus(success);
   		response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
	} catch (BTSLBaseException be) {
		_log.errorTrace(methodName, be);
		throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
				be.getArgs());
	} catch (Exception ex) {
		_log.errorTrace(methodName, ex);
		throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
	} finally {
			}

		
	}




}