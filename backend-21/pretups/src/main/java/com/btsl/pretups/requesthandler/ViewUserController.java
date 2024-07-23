package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.master.businesslogic.DivisionDeptDAO;
import com.btsl.pretups.master.businesslogic.DivisionDeptVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.txn.user.businesslogic.UserTxnDAO;

public class ViewUserController implements ServiceKeywordControllerI {

    private static final Log LOG = LogFactory.getLog(ViewUserController.class.getName());
    private HashMap requestMap = null;
    private static final String MSISDNSTR = "USERMSISDN";
    private static final String LOGINIDSTR = "USRLOGINID";

    /**
     * This method is the entry point in the class and is declared in the
     * Interface ServiceKeywordControllerI
     * This method processes the request for the MSISDN OR USERLOGINID
     * calls the validate() for validating MSISDN, USERLOGINID
     * calls the loadChannelUserDetails() that sets the Channel User details in
     * the channelUserVO
     * and sets the Channel User details in the requestVO
     * 
     * @param requestVO
     *            RequestVO
     */
    public void process(RequestVO requestVO) {
        final String methodName = "process";
        LogFactory.printLog(methodName, "Entered.....requestVO=" + requestVO, LOG);
        Connection con = null;MComConnectionI mcomCon = null;
        requestMap = requestVO.getRequestMap();
        try {
            requestVO.setNetworkCode((String) requestMap.get("EXTNWCODE"));
            // validating the request. if msisdn is given then MSISDN,
            // filteredMsisdn return or if
            // loginid is given USERLOGINID, loginId
            String[] sArrRequest = validate(requestVO);

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Loading the Channel User details
            ChannelUserVO channelUserVO = loadChannelUserDetails(con, sArrRequest, requestVO);

            // Setting the response object
            requestMap.put("CHNUSERVO", channelUserVO);
            requestVO.setValueObject(channelUserVO);
            requestVO.setSuccessTxn(true);
            
            requestVO.setMessageCode(PretupsI.TXN_STATUS_SUCCESS);

        } catch (BTSLBaseException be) {
            requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, requestVO.getRequestIDStr(), "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                String[] args = be.getArgs();
                requestVO.setMessageArguments(args);
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            LOG.errorTrace(methodName, be);
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                LOG.errorTrace(methodName, ee);
            }
            LOG.error(methodName, requestVO.getRequestIDStr(), "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ViewUserController[process]",
            		"", "", "", "Exception:" + e.getMessage());
            requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ViewUserController#process");
				mcomCon = null;
			}
            requestMap.put("RES_TYPE", "NA");
            requestVO.setRequestMap(requestMap);
            LogFactory.printLog(methodName, "Exited.....requestVO=" + requestVO, LOG);
        }
    }

    /**
     * This method checks for the mandatory value MSISDN OR USERLOGINID is not
     * null,
     * if msisdn is there, then Network Prefix and supporting network are there
     * or not
     * 
     * @param requestVO
     *            RequestVO
     * @return retStrArr String[]
     * @throws BTSLBaseException
     */
    private String[] validate(RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "validate";
        LogFactory.printLog(methodName, "Entered.....", LOG);
        String msisdn = null;
        String loginId = null;
        String filteredMsisdn = null;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
        String networkCode = null;
        String [] retStrArr = null;
        try {
            msisdn = (String) requestMap.get(MSISDNSTR);
            loginId = (String) requestMap.get(LOGINIDSTR);

            if (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(loginId)) { // if
                                                                                   // both
                                                                                   // absent
                requestMap.put("RES_ERR_KEY", "MSISDN or USERLOGINID"); // tbd
                LogFactory.printLog(methodName, "Missing mandatory value: MSISDN OR USERLOGINID", LOG);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CCE_XML_ERROR_ONE_VALUE_REQUIRED);
            }else if (!BTSLUtil.isNullString(msisdn)) {
                filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    requestMap.put("RES_ERR_KEY", MSISDNSTR);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                }
                // get prefix of the MSISDN
                msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                                                                          // the
                                                                          // prefix
                                                                          // of
                                                                          // the
                                                                          // MSISDN
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    LogFactory.printLog(methodName, "No Network prefix found for msisdn=" + msisdn, LOG);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK);
                }
                // check network support of the MSISDN
                networkCode = networkPrefixVO.getNetworkCode();
                if (!networkCode.equals(requestVO.getNetworkCode())) {
                    LogFactory.printLog(methodName, "No supporting Network for msisdn=" + msisdn, LOG);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
                retStrArr = new String[] { MSISDNSTR, filteredMsisdn };
            } else if (!BTSLUtil.isNullString(loginId)) { 
                retStrArr = new String[] { LOGINIDSTR, loginId };
            }
        } catch (BTSLBaseException e) {
            LOG.errorTrace(methodName, e);
            LOG.error(methodName, "BTSLBaseException " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            		"ViewUserController[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        LogFactory.printLog(methodName, "Exiting.....", LOG);
        return retStrArr;
    }

    /**
     * This method loads the channel user details for p_request is for msisdn or
     * loginid
     * 
     * @param con
     *            Connection
     * @param sArrRequest
     *            String []
     * @param requestVO
     *            RequestVO
     * @return channelUserVO ChannelUserVO
     * @throws BTSLBaseException
     */
    private ChannelUserVO loadChannelUserDetails(Connection con, String[] sArrRequest, RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "loadChannelUserDetails";
        LogFactory.printLog(methodName, "Entered.....", LOG);
        UserVO userVO = null;
        ChannelUserVO channelUserVO = null;
        try {
            ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            UserDAO userDAO = new UserDAO();


            if (sArrRequest[0].equals(MSISDNSTR)) {
                userVO = userDAO.loadUsersDetails(con, sArrRequest[1]);
                if (userVO == null) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4MSISDN, new String[] { sArrRequest[1] });
                }
            } else if (sArrRequest[0].equals(LOGINIDSTR)) {
                userVO = new UserTxnDAO().loadUsersDetailsByLoginId(con, sArrRequest[1]);
                if (userVO == null) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4LOGINID, new String[] { sArrRequest[1] });
                }
                if (!userVO.getNetworkID().equals(requestVO.getNetworkCode())) {
                    LogFactory.printLog(methodName, "No supporting Network for Login ID = " + sArrRequest[1], LOG);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
            }
            
            if(!BTSLUtil.isNullString(userVO.getDivisionCode())){
                userVO.setDivisionDesc(((DivisionDeptVO)new DivisionDeptDAO().loadDivDepDetailsById(con, userVO.getDivisionCode())).getDivDeptName());
            }
            
            if(!BTSLUtil.isNullString(userVO.getDepartmentCode())){
                userVO.setDepartmentDesc(((DivisionDeptVO)new DivisionDeptDAO().loadDivDepDetailsById(con, userVO.getDepartmentCode())).getDivDeptName());
            }

            channelUserVO=(ChannelUserVO)userVO;
            
            // load the channel user other details
            if(channelUserVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)){
                ChannelUserVO chUserVO = channelUserDAO.loadChannelUser(con, userVO.getUserID());
                channelUserVO.setUserGrade(chUserVO.getUserGrade());
                channelUserVO.setContactPerson(chUserVO.getContactPerson());
                channelUserVO.setTransferProfileID(chUserVO.getTransferProfileID());
                channelUserVO.setCommissionProfileSetID(chUserVO.getCommissionProfileSetID());
                channelUserVO.setInSuspend(chUserVO.getInSuspend());
                channelUserVO.setOutSuspened(chUserVO.getOutSuspened());
                channelUserVO.setOutletCode(chUserVO.getOutletCode());
                channelUserVO.setSubOutletCode(chUserVO.getSubOutletCode());

                // load the channel user other details
                // load the Commission profile Name
                List valueObjectList = new UserDAO().loadCommisionProfileListByCategoryID(con, channelUserVO.getCategoryCode(), channelUserVO.getNetworkID());
                if (valueObjectList != null && !valueObjectList.isEmpty()) {
                    ListValueVO vo = BTSLUtil.getOptionDesc(channelUserVO.getCommissionProfileSetID(), valueObjectList);
                    channelUserVO.setCommissionProfileSetName(vo.getLabel());
                }
                // load the User Grade Name
                valueObjectList = new CategoryGradeDAO().loadGradeList(con, channelUserVO.getCategoryCode());
                GradeVO gradeVO = null;
                if (valueObjectList != null && !valueObjectList.isEmpty()) {
                    for (int i = 0, j = valueObjectList.size(); i < j; i++) {
                        gradeVO = (GradeVO) valueObjectList.get(i);
                        if (gradeVO.getGradeCode().equals(channelUserVO.getUserGrade())) {
                            channelUserVO.setUserGradeName(gradeVO.getGradeName());
                            break;
                        }
                    }
                }
                // load the Transfer Profile Name
                valueObjectList = new TransferProfileDAO().loadTransferProfileByCategoryID(con, channelUserVO.getNetworkID(), 
                		channelUserVO.getCategoryCode(), PretupsI.PARENT_PROFILE_ID_USER);
                if (valueObjectList != null && !valueObjectList.isEmpty()) {
                    ListValueVO vo = BTSLUtil.getOptionDesc(channelUserVO.getTransferProfileID(), valueObjectList);
                    channelUserVO.setTransferProfileName(vo.getLabel());
                }
            }

            // Setting the ServiceType
            List alServiceType = new ServicesTypeDAO().loadUserServicesList(con, channelUserVO.getUserID());
            if (alServiceType != null && !alServiceType.isEmpty()) {
                ListValueVO listValueVO = null;
                StringBuilder strBuf = new StringBuilder(100);
                for (int i = 0, j = alServiceType.size(); i < j; i++) {
                    listValueVO = (ListValueVO) alServiceType.get(i);
                    strBuf.append(listValueVO.getLabel());
                    strBuf.append(",");
                }
                channelUserVO.setServiceTypes(strBuf.substring(0, strBuf.length() - 1));
            }
            
            List alGeographyCode = new GeographicalDomainDAO().loadUserGeographyList(con, channelUserVO.getUserID(), channelUserVO.getNetworkID());
            if (alGeographyCode != null && !alGeographyCode.isEmpty()) {
                UserGeographiesVO userGeographiesVO = null;
                StringBuilder strBuf = new StringBuilder(100);
                for (int i = 0, j = alGeographyCode.size(); i < j; i++) {
                    userGeographiesVO = (UserGeographiesVO) alGeographyCode.get(i);
                    strBuf.append(userGeographiesVO.getGraphDomainName());
                    strBuf.append(",");
                }
                channelUserVO.setGeographicalCode(strBuf.substring(0, strBuf.length() - 1));
            }
        } catch (BTSLBaseException e) {
            LOG.errorTrace(methodName, e);
            LOG.error(methodName, "BTSLBaseException " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"ViewUserController[loadChannelUserDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        LogFactory.printLog(methodName, "Exiting.....", LOG);
        return channelUserVO;
    }

}
