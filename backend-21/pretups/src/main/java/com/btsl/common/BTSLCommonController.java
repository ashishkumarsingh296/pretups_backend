package com.btsl.common;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;

public class BTSLCommonController {

	/**
     * This method is used for loading Summary Details Of User.
     * Method loadingSummaryDetails
     * 
     * @param usersReportModel
     *            UsersReportModel
     * 
     * @param request
     *            HttpServletRequest
     * 
     * @param con
     *            Connection
     * 
     * @param loggedInUserDomainList
     *            ArrayList
     * 
     * @param listValueVO
     *            ListValueVO
     * 
     * @throws BTSLBaseException
     * @author ayush.abhijeet
     */

	public void loadingSummaryDetails(UsersReportModel usersReportModel, HttpServletRequest request, Connection con, ArrayList loggedInUserDomainList, ListValueVO listValueVO) throws BTSLBaseException {
		UserVO userVO = this.getUserFormSession(request);
		CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
		if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
			usersReportModel.setZoneList(new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO
					.getNetworkID()));
		else 
			usersReportModel.setZoneList(userVO.getGeographicalAreaList());
		usersReportModel.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
		int loginSeqNo = userVO.getCategoryVO().getSequenceNumber();
		usersReportModel.setCategorySeqNo(loginSeqNo + "");
		usersReportModel.setUserType(userVO.getUserType());
		if (userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
			usersReportModel.setParentCategoryList(categoryWebDAO.loadCategoryReportList(con));
		} else {
			usersReportModel.setParentCategoryList(categoryWebDAO.loadCategoryReporSeqtList(con, loginSeqNo));
		}

		usersReportModel.setLoginUserID(userVO.getUserID());
		usersReportModel.setLoggedInUserCategoryCode(userVO.getCategoryVO().getCategoryCode());
		if (userVO.isStaffUser()) {
			usersReportModel.setLoggedInUserName(userVO.getParentName());
		} else {
			usersReportModel.setLoggedInUserName(userVO.getUserName());
		}
		commonUserList(usersReportModel, loggedInUserDomainList, userVO);
		commonGeographicDetails(usersReportModel, listValueVO);
	}


	/**
	 * 
	 * @param request
	 * @return
	 * @throws BTSLBaseException
	 */
	protected UserVO getUserFormSession(HttpServletRequest request) throws BTSLBaseException {
		UserVO userVO = null;
		HttpSession session = request.getSession(false);
		Object obj = session.getAttribute("user");

		if (obj != null) {
			userVO = (UserVO) obj;
		}
		if (obj == null || userVO == null) {
			throw new BTSLBaseException("common.topband.message.sessionexpired", "unAuthorisedAccessF");
		}
		return userVO;
	}
	
    public void commonUserList(UsersReportModel thisModel, ArrayList loggedInUserDomainList, UserVO userVO) {
        	
    	if(loggedInUserDomainList==null) {
            loggedInUserDomainList= new ArrayList();
        }
           if (thisModel.getDomainListSize() == 0) {
            loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
            thisModel.setDomainList(loggedInUserDomainList);
            thisModel.setDomainCode(userVO.getDomainID());
            thisModel.setDomainName(userVO.getDomainName());
        } else if (thisModel.getDomainListSize() == 1) {
            ListValueVO listvo = (ListValueVO) thisModel.getDomainList().get(0);
            thisModel.setDomainCode(listvo.getValue());
            thisModel.setDomainName(listvo.getLabel());
        }
           
    }
    
    
    /**
     * Common Method used to load the zone information
     * 
     * @param thisModel
     * @param listValueVO
     */

    public void commonGeographicDetails(UsersReportModel thisModel, ListValueVO listValueVO) {

        ArrayList zoneList = thisModel.getZoneList();
        UserGeographiesVO geographyVO = null;
        ArrayList geoList = new ArrayList();

        for (int i = 0, k = zoneList.size(); i < k; i++) {
            geographyVO = (UserGeographiesVO) zoneList.get(i);
            geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
        }
        if (geoList.size() == 1) {
            listValueVO = (ListValueVO) geoList.get(0);
            thisModel.setZoneCode(listValueVO.getValue());
            thisModel.setZoneName(listValueVO.getLabel());
            thisModel.setZoneList(geoList);
        } else {
            thisModel.setZoneList(geoList);
        }
    }
    
    /**
     * Common Method used to Format the Date Type in Report
     * 
     * @param UsersReportForm
     *            usersReportModel
     * @throws Exception
     */

    public void commonReportDateFormat(UsersReportModel usersReportModel, String methodName) throws ParseException {
        Date frDate = null;
        Date tDate = null;
        Date temptDate = null;
        String fromdate = null;
        String todate = null;
        String temptodate = null;

        if (!BTSLUtil.isNullString(usersReportModel.getFromDate())) {
            frDate = BTSLUtil.getDateFromDateString(usersReportModel.getFromDate());
        }
        if (!BTSLUtil.isNullString(usersReportModel.getToDate())) {
            tDate = BTSLUtil.getDateFromDateString(usersReportModel.getToDate());
        }
        if (frDate != null) {
            fromdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(frDate));
        }
        if (tDate != null) {
            todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tDate));
        }
        if (!BTSLUtil.isNullString(fromdate)) {
        	usersReportModel.setRptfromDate(BTSLUtil.reportDateFormat(fromdate));
        }
        if (!BTSLUtil.isNullString(todate)) {
        	usersReportModel.setRpttoDate(BTSLUtil.reportDateFormat(todate));
        }

         if(!BTSLUtil.isNullString(usersReportModel.getDailyDate()))
          {
             temptDate = BTSLUtil.getDateFromDateString(usersReportModel.getDailyDate());
          }
         
         if(temptDate != null)
          {
             temptodate=BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(temptDate));
             usersReportModel.setRptcurrentDate(BTSLUtil.reportDateFormat(temptodate)); // report format date
          }
        
    }
    
    /**
     * Common Method used to Validate the MSISDN
     * 
     * @param UserVO
     *            userVO
     * @param UsersReportForm
     *            thisForm
     * @param ChannelUserVO
     *            toMsisdnChannelUserVO
     * @param ChannelUserVO
     *            fromMsisdnChannelUserVO
     * @throws Exception
     */

    public void commonValidationMSISDN(UserVO userVO, UsersReportModel usersReportModel, ChannelUserVO toMsisdnChannelUserVO, ChannelUserVO fromMsisdnChannelUserVO, String methodName) throws BTSLBaseException {

        if (PretupsBL.getFilteredMSISDN(usersReportModel.getFromMsisdn()).equals(userVO.getMsisdn()) && (!(PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())))) {
        	usersReportModel.setUserName(userVO.getUserName());
        	usersReportModel.setUserID(userVO.getUserID());
        	usersReportModel.setFromtransferCategoryCode(userVO.getCategoryCode());
        	usersReportModel.setFromtransferCategoryName(userVO.getCategoryVO().getCategoryName());
        	usersReportModel.setDomainCode(userVO.getCategoryVO().getDomainCodeforCategory());
        	usersReportModel.setDomainName(userVO.getDomainName());
            UserGeographiesVO userGeoVO = (UserGeographiesVO) userVO.getGeographicalAreaList().get(0);
            // thisForm.setZoneCode(userGeoVO.getGraphDomainCode());
            usersReportModel.setZoneCode(PretupsI.ALL);
            usersReportModel.setZoneName(userGeoVO.getGraphDomainName());

            usersReportModel.setTouserName(toMsisdnChannelUserVO.getUserName());
            usersReportModel.setTouserID(toMsisdnChannelUserVO.getUserID());
            usersReportModel.setTotransferCategoryName(toMsisdnChannelUserVO.getCategoryVO().getCategoryName());
            usersReportModel.setTotransferCategoryCode(toMsisdnChannelUserVO.getCategoryVO().getCategoryCode());
        } else {
            if (PretupsBL.getFilteredMSISDN(usersReportModel.getToMsisdn()).equals(userVO.getMsisdn()) && (!(PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())))) {
            	usersReportModel.setUserName(fromMsisdnChannelUserVO.getUserName());
            	usersReportModel.setUserID(fromMsisdnChannelUserVO.getUserID());
            	usersReportModel.setFromtransferCategoryName(fromMsisdnChannelUserVO.getCategoryVO().getCategoryName());
            	usersReportModel.setFromtransferCategoryCode(fromMsisdnChannelUserVO.getCategoryVO().getCategoryCode());
            	usersReportModel.setDomainName(fromMsisdnChannelUserVO.getDomainName());
            	usersReportModel.setDomainCode(fromMsisdnChannelUserVO.getCategoryVO().getDomainCodeforCategory());
            	usersReportModel.setZoneName(fromMsisdnChannelUserVO.getGeographicalDesc());
                // thisForm.setZoneCode(fromMsisdnChannelUserVO.getGeographicalCode());
            	usersReportModel.setZoneCode(PretupsI.ALL);

            	usersReportModel.setTouserName(userVO.getUserName());
            	usersReportModel.setTouserID(userVO.getUserID());
            	usersReportModel.setTotransferCategoryName(userVO.getCategoryVO().getCategoryName());
            	usersReportModel.setTotransferCategoryCode(userVO.getCategoryVO().getCategoryCode());

            } else {
            	usersReportModel.setUserName(fromMsisdnChannelUserVO.getUserName());
            	usersReportModel.setUserID(fromMsisdnChannelUserVO.getUserID());
            	usersReportModel.setFromtransferCategoryName(fromMsisdnChannelUserVO.getCategoryVO().getCategoryName());
            	usersReportModel.setFromtransferCategoryCode(fromMsisdnChannelUserVO.getCategoryVO().getCategoryCode());
            	usersReportModel.setDomainName(fromMsisdnChannelUserVO.getDomainName());
            	usersReportModel.setDomainCode(fromMsisdnChannelUserVO.getCategoryVO().getDomainCodeforCategory());
            	usersReportModel.setZoneName(fromMsisdnChannelUserVO.getGeographicalDesc());
                // thisForm.setZoneCode(fromMsisdnChannelUserVO.getGeographicalCode());
            	usersReportModel.setZoneCode(PretupsI.ALL);

            	usersReportModel.setTouserName(toMsisdnChannelUserVO.getUserName());
            	usersReportModel.setTouserID(toMsisdnChannelUserVO.getUserID());
            	usersReportModel.setTotransferCategoryName(toMsisdnChannelUserVO.getCategoryVO().getCategoryName());
            	usersReportModel.setTotransferCategoryCode(toMsisdnChannelUserVO.getCategoryVO().getCategoryCode());
            }
        }

    }
    
    /**
     * 
     * @param thisModel
     * @param listValueVO
     * @param request
     */
    public void domainValidate(UsersReportModel thisModel, ListValueVO listValueVO, HttpServletRequest request) {
    	if (thisModel.getDomainCode().equals(PretupsI.ALL)) {
    		thisModel.setDomainName(PretupsRestUtil.getMessageString("list.all"));
    		StringBuilder sb = new StringBuilder();
    		if (!thisModel.getDomainList().isEmpty()) {
    			String domainCode = "";
    			for (int i = 0, j = thisModel.getDomainList().size(); i < j; i++) {
    				//for the DomainList whose Return Type is ListValueVO
    				if(thisModel.getDomainList().get(i).getClass().equals(ListValueVO.class)){
    					ListValueVO listvalueVO =(ListValueVO)thisModel.getDomainList().get(i);
    					domainCode = domainCode + listvalueVO.getValue() + "','";
    				}
    				//for the DomainList whose Return Type is DomainVO
    				else if(thisModel.getDomainList().get(i).getClass().equals(DomainVO.class)){
    					DomainVO domainVO =(DomainVO)thisModel.getDomainList().get(i);
    					 sb.setLength(0);
    					 sb.append(domainCode);
    					 sb.append(domainVO.getDomainCode());
    					 sb.append("','");
    					domainCode = sb.toString() ;
    				}
    				else{
    					 sb.setLength(0);
   					 sb.append(domainCode);
   					 sb.append(thisModel.getDomainCode());
   					 sb.append("','");
   					domainCode = sb.toString() ;
    				
    				}
    			}
    			domainCode = domainCode.substring(0, domainCode.length() - 3);
    			thisModel.setDomainListString(domainCode);
    		}
    	} else {	

    		int i,j;
    		for ( i = 0,  j = thisModel.getDomainList().size(); i < j; i++)
    		{
    			if (thisModel.getDomainList().get(i).getClass().equals(DomainVO.class))

    			{
    				DomainVO domainVO = (DomainVO) thisModel.getDomainList().get(i);
    				if (domainVO.getDomainCode().equals(thisModel.getDomainCode())) {
    					thisModel.setDomainName(domainVO.getDomainName());
    					thisModel.setDomainListString(thisModel.getDomainCode());
    				}

    			} else if (thisModel.getDomainList().get(i).getClass().equals(ListValueVO.class)) {

    				ListValueVO listVO = (ListValueVO) thisModel.getDomainList().get(i);
    				if (listVO.getValue().equals(thisModel.getDomainCode())) {
    					thisModel.setDomainName(listVO.getLabel());
    					thisModel.setDomainListString(thisModel.getDomainCode());
    				}
    			} else {
    				thisModel.setDomainName(thisModel.getDomainName());
    				thisModel.setDomainListString(thisModel.getDomainCode());
    			}
    		}

    	}
    }

    /**
     * 
     * @param filteredMSISDN
     * @param userVO
     * @param thisForm
     * @param channelUserVO
     * @param con
     * @throws BTSLBaseException
     */
    public void validateMsisdn(String filteredMSISDN, UserVO userVO, UsersReportModel usersReportModel, ChannelUserVO channelUserVO, Connection con) throws BTSLBaseException {
    	if (filteredMSISDN.equals(userVO.getMsisdn()) && (!(PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())))) {
    		usersReportModel.setUserName(userVO.getUserName());
    		usersReportModel.setUserID(userVO.getUserID());
    		StringBuilder sb = new StringBuilder();
    		sb.append(userVO.getUserName());
			sb.append("(");
			sb.append(userVO.getUserID());
			sb.append(")");
    		usersReportModel.setUsersName(sb.toString());
    		usersReportModel.setTransferUserCategoryCode(userVO.getCategoryCode());
    		usersReportModel.setCategoryName(userVO.getCategoryVO().getCategoryName());

    		usersReportModel.setDomainCode(userVO.getCategoryVO().getDomainCodeforCategory());
    		usersReportModel.setDomainName(userVO.getDomainName());

    		usersReportModel.setParentCategoryCode(userVO.getCategoryVO().getCategoryCode());

    		UserGeographiesVO userGeoVO = (UserGeographiesVO) userVO.getGeographicalAreaList().get(0);
    		usersReportModel.setZoneCode(userGeoVO.getGraphDomainCode());
    		usersReportModel.setZoneName(userGeoVO.getGraphDomainName());
    	} else {
    		usersReportModel.setUserName(channelUserVO.getUserName());
    		usersReportModel.setUserID(channelUserVO.getUserID());
    		StringBuilder sb = new StringBuilder();
    		sb.append(channelUserVO.getUserName());
			sb.append("(");
			sb.append(channelUserVO.getUserID());
			sb.append(")");
    		usersReportModel.setUsersName(sb.toString());
    		usersReportModel.setTransferUserCategoryCode(channelUserVO.getCategoryVO().getCategoryCode());
    		usersReportModel.setCategoryName(channelUserVO.getCategoryVO().getCategoryName());

    		usersReportModel.setDomainCode(channelUserVO.getCategoryVO().getDomainCodeforCategory());
    		usersReportModel.setDomainName(channelUserVO.getDomainName());
    		usersReportModel.setParentCategoryCode(channelUserVO.getCategoryVO().getCategoryCode());

    		ArrayList list = new GeographicalDomainDAO().loadGeoDomainCodeHeirarchy(con, userVO.getCategoryVO().getGrphDomainType(), channelUserVO.getGeographicalCode(), false);
    		GeographicalDomainVO geographicalDomainVO = (GeographicalDomainVO) list.get(0);

    		usersReportModel.setZoneCode(geographicalDomainVO.getGrphDomainCode());
    		usersReportModel.setZoneName(geographicalDomainVO.getGrphDomainName());
    	}
    }
    
    /**
     * 
     * @param usersReportModel
     * @param request
     * @param listValueVO
     */
    public void commonDomainInfo(UsersReportModel usersReportModel, HttpServletRequest request, ListValueVO listValueVO) {
    	
        if (!BTSLUtil.isNullString(usersReportModel.getZoneCode()) && usersReportModel.getZoneCode().equals(PretupsI.ALL)) {
            usersReportModel.setZoneName(PretupsRestUtil.getMessageString("list.all"));
        } else if (!BTSLUtil.isNullString(usersReportModel.getZoneCode())) {
            listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getZoneCode(), usersReportModel.getZoneList());
            usersReportModel.setZoneName(listValueVO.getLabel());
        }

        if (!BTSLUtil.isNullString(usersReportModel.getDomainCode()) && usersReportModel.getDomainCode().equals(PretupsI.ALL)) {
            usersReportModel.setDomainName(PretupsRestUtil.getMessageString("list.all"));
            if (!usersReportModel.getDomainList().isEmpty()) {
                String domainCode = "";
                for (int i = 0, j = usersReportModel.getDomainList().size(); i < j; i++) {
                    listValueVO = (ListValueVO) usersReportModel.getDomainList().get(i);
                    domainCode = domainCode + listValueVO.getValue() + "','";
                }
                domainCode = domainCode.substring(0, domainCode.length() - 3);
                usersReportModel.setDomainListString(domainCode);
            }
        } else if (!BTSLUtil.isNullString(usersReportModel.getDomainCode())) {
            listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getDomainCode(), usersReportModel.getDomainList());
            usersReportModel.setDomainName(listValueVO.getLabel());
            usersReportModel.setDomainListString(usersReportModel.getDomainCode());
        }
        if (!BTSLUtil.isNullString(usersReportModel.getParentCategoryCode()) && usersReportModel.getParentCategoryCode().equals(PretupsI.ALL)) {
            usersReportModel.setCategoryName(PretupsRestUtil.getMessageString("list.all"));
        } else if (!BTSLUtil.isNullString(usersReportModel.getParentCategoryCode())) {
            listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getParentCategoryCode(), usersReportModel.getParentCategoryList());
            usersReportModel.setCategoryName(listValueVO.getLabel());
            String[] arr = usersReportModel.getParentCategoryCode().split("\\|");
            usersReportModel.setParentCategoryCode(arr[1]);
        }
    }
    
}
