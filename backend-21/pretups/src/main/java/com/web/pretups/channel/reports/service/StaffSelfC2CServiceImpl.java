package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
//import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLCommonController;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.StaffSelfC2CDAO;
import com.btsl.pretups.channel.reports.businesslogic.StaffSelfC2CReportVO;
import com.btsl.pretups.common.DownloadCSVReportsNew;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
/**
 * @author mohit.miglani
 *
 */
@Service("StaffSelfC2CService")
public class StaffSelfC2CServiceImpl implements StaffSelfC2CService {

	public static final Log _log = LogFactory
			.getLog(ChannelUserOperatorUserRolesServiceImpl.class.getName());
	private static final String FAIL_KEY = "fail";
	

	@Override
	public boolean loadStaffC2CTransferDetails(UsersReportModel usersReportModel,
			HttpServletRequest request, HttpServletResponse response,
			UserVO userVO) {
		
		BTSLCommonController btslCommonController = new BTSLCommonController();

		final String methodName = "loadStaffC2cTransferDetails";
        
        //ActionForward forward = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        final ArrayList loggedInUserDomainList = new ArrayList();
        final ListValueVO listValueVO = null;
        
        
        
        
        
        
        
        
        
        
        
        
        try {
            
            
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            
            usersReportModel.setUserName(null);

            final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
            final int loginSeqNo = userVO.getCategoryVO().getSequenceNumber();
            if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
            	   usersReportModel.setZoneList(new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID()));
 		    else
 		    {
            usersReportModel.setZoneList(userVO.getGeographicalAreaList());
 		    }
            usersReportModel.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
           
            usersReportModel.setCategorySeqNo(Integer.toString(loginSeqNo) + "");
            usersReportModel.setUserType(userVO.getUserType());
           
            if (userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
                usersReportModel.setParentCategoryList(categoryWebDAO.loadCategoryReportList(con));
            } else {
                usersReportModel.setParentCategoryList(categoryWebDAO.loadCategoryReporSeqtList(con, loginSeqNo));
            }

            btslCommonController.commonUserList(usersReportModel, loggedInUserDomainList, userVO);

            if (!BTSLUtil.isNullString(userVO.getFromTime()) && !BTSLUtil.isNullString(userVO.getToTime())) {
                usersReportModel.setFromTime(userVO.getFromTime());
                usersReportModel.setToTime(userVO.getToTime());
            }
            btslCommonController.commonGeographicDetails(usersReportModel, listValueVO);
            usersReportModel.setLoginUserID(userVO.getUserID());
            if (PretupsI.CHANNEL_USER_TYPE.equals(userVO.getUserType())) {
                loggedinuser(usersReportModel, userVO);
            }

            usersReportModel.setTxnSubTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true));
            usersReportModel.setNetworkCode(userVO.getNetworkID());
            usersReportModel.setUserType(userVO.getUserType());

            checkstaff(usersReportModel, userVO);
            usersReportModel.setLoginUserID(userVO.getUserID());
          
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
           
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ChannelUserReportAction#loadStaffC2cTransferDetails");
        		mcomCon=null;
        		}
            /*if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:forward=" + forward);
            }*/
        }
       return true;
    }

	private void checkstaff(UsersReportModel usersReportModel, UserVO userVO) {
		if (userVO.isStaffUser()) {
		    usersReportModel.setLoggedInUserName(userVO.getParentName());
		} else {
		    usersReportModel.setLoggedInUserName(userVO.getUserName());
		}
	}

	private void loggedinuser(UsersReportModel usersReportModel, UserVO userVO) {
		usersReportModel.setLoggedInUserCategoryCode(userVO.getCategoryVO().getCategoryCode());
		checkstaff(usersReportModel, userVO);
	}
	
	@Override
	public boolean displaySelfTransactionReport(UsersReportModel usersReportModel, HttpServletRequest request, HttpServletResponse response, Model model,UserVO userVO, ChannelUserVO userVO1,BindingResult bindingResult)
	{
		
		final String methodName = "displaySelftTransactionReport";
        
        
        Connection con = null;
        MComConnectionI mcomCon = null;
        CommonValidator commonValidator = new CommonValidator(
				"configfiles/c2sreports/validator-staffSelfC2Cv.xml",
				usersReportModel, "StaffSelfC2CReportsThreshold");
		
         
	    Date date = new Date();  
	   
		curdate(usersReportModel, methodName, date);
	
        
        
        
        
        serversidevalidation(usersReportModel, request, bindingResult,
				methodName, commonValidator);

		if (bindingResult.hasFieldErrors()) {

			return false;
		}

        
        
        
        try {
            
            
            usersReportModel.setNetworkCode(userVO.getNetworkID());
            usersReportModel.setNetworkName(userVO.getNetworkName());
            usersReportModel.setReportHeaderName(userVO.getReportHeaderName());
            ListValueVO listValueVO = null;
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

           
            if (!BTSLUtil.isNullString(usersReportModel.getTxnSubType()) && (usersReportModel.getTxnSubType().trim()).equals(PretupsI.ALL)) {
                usersReportModel.setTxnSubTypeName(PretupsRestUtil
        				.getMessageString("list.all"));
            } else if (!BTSLUtil.isNullString(usersReportModel.getTxnSubType())) {
                listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getTxnSubType(), usersReportModel.getTxnSubTypeList());
                usersReportModel.setTxnSubTypeName(listValueVO.getLabel());
            }
            usersReportModel.setUserName(userVO.getUserName());

            usersReportModel.setTransferUserCategoryCode(userVO.getCategoryCode());
            usersReportModel.setCategoryName(userVO.getCategoryVO().getCategoryName());

            usersReportModel.setDomainCode(userVO.getCategoryVO().getDomainCodeforCategory());
            usersReportModel.setDomainName(userVO.getDomainName());
            usersReportModel.setUserID(userVO.getActiveUserID());
            usersReportModel.setParentCategoryCode(userVO.getCategoryVO().getCategoryCode());

            final UserGeographiesVO userGeoVO = userVO.getGeographicalAreaList().get(0);
            usersReportModel.setZoneCode(userGeoVO.getGraphDomainCode());
            usersReportModel.setZoneName(userGeoVO.getGraphDomainName());
            commonDateTimeUpdate(usersReportModel, methodName);
            
            //date range for 60 days validation
			 Date fromd = BTSLUtil.getDateFromDateString(usersReportModel.getFromDate());
			 Date tod = BTSLUtil.getDateFromDateString(usersReportModel.getToDate());
			 int diff = BTSLUtil.getDifferenceInUtilDates(fromd, tod);
			 if (diff > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue()) {
			      model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString("btsl.date.error.datecompare",new String[] { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue()) }));
			      return false;
			 }

            
            
            
            
            String rptCode;
        	if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
        		rptCode = "STFSLFC2C01";
        		usersReportModel.setrptCode(rptCode);
        		StaffSelfC2CDAO av=new StaffSelfC2CDAO();
        		List<StaffSelfC2CReportVO> staffSelfC2CList =av
        				.loadStaffSelfReport(con, usersReportModel);
        		usersReportModel.setStaffSelfC2CList(staffSelfC2CList);
           
        	}
        
            
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
           
        } finally {
        	closeconn(mcomCon);
            
        }
   
		return true;
	}

	private void serversidevalidation(UsersReportModel usersReportModel,
			HttpServletRequest request, BindingResult bindingResult,
			final String methodName, CommonValidator commonValidator) {
		try {
			
			
			
			
			
			
			
			
			Map<String,String> errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru = new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);
			request.getSession().setAttribute("usersReportModel",
					usersReportModel);
		} catch (ValidatorException | IOException | SAXException e) {

			_log.errorTrace(methodName, e);
		}
	}

	private void curdate(UsersReportModel usersReportModel,
			final String methodName, Date date) {
		String curDate;
		try {
			curDate = BTSLUtil.getDateStringFromDate(date, PretupsI.TIMESTAMP_DATESPACEHHMMSS);
			usersReportModel.setCurrentDate(curDate);
		} catch (ParseException e1) {
			_log.errorTrace(methodName,e1);
		}
	}

	private void closeconn(MComConnectionI mcomCon) {
		if(mcomCon != null)
		{
			mcomCon.close("ChannelUserReportAction#displaySelftTransactionReport");
		
		}
	}
	
	
	
	
	 protected void commonDateTimeUpdate(UsersReportModel usersReportModel, String methodName) {
	        
	        try {
	           
	            toandfromTime(usersReportModel);
	            
	            usersReportModel.setRptfromDate(usersReportModel.getFromDate() + " " + usersReportModel.getFromTime() + ":00");
usersReportModel.setRpttoDate(usersReportModel.getToDate() + " " + usersReportModel.getToTime());
	            
	        } catch (Exception e) {
	            _log.errorTrace(methodName, e);
	        }
	    }


	private void toandfromTime(UsersReportModel usersReportModel) {
		if (BTSLUtil.isNullString(usersReportModel.getFromTime())) {
		    usersReportModel.setFromTime("00:00");
		}
		
	}

	
	
	 @Override
		public String downloadFileforSumm(UsersReportModel usersReportModel)
				throws InterruptedException, BTSLBaseException, SQLException {

			DownloadCSVReportsNew downloadCSVReports = new DownloadCSVReportsNew();
			Connection con;
			MComConnectionI mcomCon;
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			String rptCode = usersReportModel.getrptCode();
			String filePath;

			filePath = downloadCSVReports.prepareData(usersReportModel, rptCode,
					con);
			return filePath;

		}
		
	
	
	
	
	
	
	
	
	
	
	}
	
	
	
