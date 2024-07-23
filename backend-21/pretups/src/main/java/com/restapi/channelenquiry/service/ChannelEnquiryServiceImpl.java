package com.restapi.channelenquiry.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toCollection;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.MComReportDBConnection;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.reports.businesslogic.UserClosingBalanceVO;
import com.btsl.pretups.channel.reports.businesslogic.UserReportDAO;
import com.btsl.pretups.channel.reports.businesslogic.UserZeroBalanceCounterSummaryDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserZeroBalanceCounterSummaryVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.ibm.icu.util.Calendar;
import com.opencsv.CSVWriter;
import com.restapi.channeluser.service.NotificationLanguageResponseVO;
import com.restapi.o2c.service.O2CProductResponseData;
import com.restapi.o2c.service.O2CProductsResponseVO;
import com.restapi.o2c.service.O2CServiceImpl;
import com.restapi.user.service.HeaderColumn;
import com.web.pretups.channel.reports.web.UsersReportForm;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.channel.transfer.businesslogic.C2STransferWebDAO;
import com.web.pretups.channel.transfer.businesslogic.C2CBatchTransferWebDAO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

import jxl.write.Label;

@Service("ChannelEnquiryService")
public class ChannelEnquiryServiceImpl implements ChannelEnquiryService {
    public static final String classname = "ChannelEnquiryServiceImpl";

    protected final Log log = LogFactory.getLog(getClass().getName());

    @Override
    public void loadC2STransferEnquiryList(Connection con, UserVO userVO, C2SEnquiryRequestVO c2sEnquiryRequestVO,
                                           C2SEnquiryResponseVO response, HttpServletResponse response1, Locale locale) {
        final String methodName = "loadC2STransferEnquiryList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED);
        }
        MComConnectionI mcomCon = null;
        NetworkPrefixVO networkPrefixVO = null;
        String msisdnPrefix = null;
        String networkCode = null;
        Date fromDate = null, toDate = null;
        String senderMsisdn = null;
        String receiverMsisdn = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        ChannelUserVO channelUserVO = null;
        ArrayList<ChannelUserVO> hierarchyList = null;
        Date date = new Date();
        try {

            fromDate = BTSLUtil.getDateFromDateString(c2sEnquiryRequestVO.getFromDate());
            toDate = BTSLUtil.getDateFromDateString(c2sEnquiryRequestVO.getToDate());
            int diff = BTSLUtil.getDifferenceInUtilDates(fromDate, toDate);
            if (diff > SystemPreferences.MAX_DATEDIFF) {
                throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                        PretupsErrorCodesI.C2SENQRY_INVALID_DATERANGE);
            }

            networkCode = userVO.getNetworkID();
            String service = c2sEnquiryRequestVO.getService();
            String transferID = null;
            channelUserWebDAO = new ChannelUserWebDAO();
            mcomCon = new MComConnection();
            try {
                con = mcomCon.getConnection();
            } catch (SQLException e) {

                log.error(methodName, "Exceptin:e=" + e);
                log.errorTrace(methodName, e);
            }
            // load the user hierarchy to validate the sender msisdn and
            // with in the login user hierarchy.

            if (!BTSLUtil.isNullString(userVO.getUserType())
                    && userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                String userID = null;
                if (PretupsI.CATEGORY_TYPE_AGENT.equals(userVO.getCategoryVO().getCategoryType())
                        && PretupsI.NO.equals(userVO.getCategoryVO().getHierarchyAllowed())) {
                    userID = userVO.getParentID();
                } else {
                    userID = userVO.getUserID();
                }
                // load whole hierarchy of the form user
                hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con, userID, false);

            }
            if(c2sEnquiryRequestVO.isStaffSearch())
            {
                loadC2STransferEnquiryListStaff(con, userVO, c2sEnquiryRequestVO, response, response1, locale);
            }
            else
            {

                if (!BTSLUtil.isNullString(c2sEnquiryRequestVO.getTransferID())) {
                    transferID = c2sEnquiryRequestVO.getTransferID().trim();
                }
                if (!BTSLUtil.isNullString(c2sEnquiryRequestVO.getReceiverMsisdn())) {
                    // Change ID=ACCOUNTID
                    // FilteredMSISDN is replaced by getFilteredIdentificationNumber
                    // This is done because this field can contains msisdn or
                    // account id
                    receiverMsisdn = PretupsBL.getFilteredIdentificationNumber(c2sEnquiryRequestVO.getReceiverMsisdn());
                    msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverMsisdn);
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                    if (networkPrefixVO == null) {
                        throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                                PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND);
                    }

                }
                enquiryForChanneluser(con, userVO, c2sEnquiryRequestVO, response, networkPrefixVO, senderMsisdn,
                        receiverMsisdn, transferID, service, fromDate, toDate, hierarchyList, msisdnPrefix, networkCode);
            }

            response.setStatus(HttpStatus.SC_OK);
            String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
            response.setMessageCode(PretupsErrorCodesI.SUCCESS);
            response.setMessage(msg);
        }
        catch (BTSLBaseException be) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            response.setMessageCode(be.getMessage());
            response.setMessage(msg);
        }
        catch (Exception e) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            if (response.getMessage() == null) {
                String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
                response.setMessageCode(e.getMessage());
                response.setMessage(msg);
            }
            try {
                if (mcomCon != null) {
                    mcomCon.finalRollback();
                }
            } catch (SQLException esql) {
                log.error(methodName, "SQLException : ", esql.getMessage());
            }

        } finally {
            if (mcomCon != null) {
                mcomCon.close(classname + "#" + methodName);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }

        }
    }

    private void enquiryForChanneluser(Connection con, UserVO userVO, C2SEnquiryRequestVO c2sEnquiryRequestVO,
                                       C2SEnquiryResponseVO c2sEnquiryResponseVO, NetworkPrefixVO networkPrefixVO, String senderMsisdn,
                                       String receiverMsisdn, String transferID, String service, Date fromDate, Date toDate,
                                       ArrayList<ChannelUserVO> hierarchyList, String msisdnPrefix, String networkCode) throws BTSLBaseException {
        String methodName = "enquiryForChanneluser";
        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED);
        }
        ChannelUserVO channelUserVO = null;
        C2STransferDAO c2STransferDAO = null;

        if (!BTSLUtil.isNullString(c2sEnquiryRequestVO.getSenderMsisdn())) {
            senderMsisdn = PretupsBL.getFilteredMSISDN(c2sEnquiryRequestVO.getSenderMsisdn());
            msisdnPrefix = PretupsBL.getMSISDNPrefix(senderMsisdn);
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                        PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND);

            }
            networkCode = networkPrefixVO.getNetworkCode();
            if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {

                throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                        PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND);

            }
        }
        // load the user hierarchy to validate the sender msisdn and
        // with in the login user hierarchy.

        if (!BTSLUtil.isNullString(userVO.getUserType())
                && userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
            // check to user
            // under the hierarchy.

            if (hierarchyList == null || hierarchyList.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Logged in user has no child user so there would be no transactions");
                }
                throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                        PretupsErrorCodesI.C2SENQRY_NO_CHILD_USERS);

            }

            // if sender msisdn is not null then validate it in the
            // hierarchy.
            if (!SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
                if (!BTSLUtil.isNullString(senderMsisdn)) {
                    boolean isMatched = false;
                    if (!hierarchyList.isEmpty()) {
                        isMatched = false;
                        for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                            channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                            if (channelUserVO.getMsisdn().equals(senderMsisdn)) {
                                isMatched = true;
                                break;
                            }
                        }
                        if (!isMatched) {
                            throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                                    PretupsErrorCodesI.C2SENQRY_USER_NOTAUTH);

                        }
                    }
                }
            } else {
                UserPhoneVO userPhoneVO = null;
                final UserDAO userDAO = new UserDAO();
                userPhoneVO = userDAO.loadUserAnyPhoneVO(con, senderMsisdn);
                boolean isMatched = false;
                if (userPhoneVO != null && hierarchyList != null && !hierarchyList.isEmpty()) {
                    isMatched = false;
                    for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                        channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                        if (channelUserVO.getUserID().equals(userPhoneVO.getUserId())) {
                            isMatched = true;
                            break;
                        }
                    }
                    if (!isMatched) {
                        throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                                PretupsErrorCodesI.C2SENQRY_USER_NOTAUTH);

                    }
                }
            }

        }
        c2STransferDAO = new C2STransferDAO();
        c2sEnquiryResponseVO.setC2sEnquiryDetails(c2STransferDAO.loadC2STransferVOList(con, networkCode, fromDate,
                toDate, senderMsisdn, receiverMsisdn, transferID, service));

        if(transferID!= null && !c2sEnquiryResponseVO.getC2sEnquiryDetails().isEmpty()){
            UserPhoneVO userPhoneVO = null;

            final UserDAO userDAO = new UserDAO();
            userPhoneVO = userDAO.loadUserAnyPhoneVO(con,  c2sEnquiryResponseVO.getC2sEnquiryDetails().get(0).getSenderMsisdn());

            boolean isMatched = false;
            if (userPhoneVO != null && hierarchyList != null && !hierarchyList.isEmpty()) {
                isMatched = false;
                for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                    channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                    if (channelUserVO.getUserID().equals(userPhoneVO.getUserId())) {
                        isMatched = true;
                        break;
                    }
                }
                if (!isMatched) {
                    c2sEnquiryResponseVO.setC2sEnquiryDetails(null);
                    throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                            PretupsErrorCodesI.C2SENQRY_USER_NOTAUTH);

                }
            }
        }



        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.EXITED);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void processChannelEnquiryO2c(C2cAndO2cEnquiryRequestVO requestVO, C2cAndO2cEnquiryResponseVO response,
                                         HttpServletResponse responseSwag, OAuthUser oAuthUserData, Locale locale, String enquiryType,
                                         String searchBy) throws BTSLBaseException, Exception {
        final String methodName = "processChannelEnquiryO2c";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        UserDAO userDao = null;
        ChannelTransferDAO channelTransferDAO = null;
        String transactionID = null;
        Date fromDate = null;
        Date toDate = null;
        String toUserCode = null;
        String transferSubTypeCode = null;
        String transferCategory = null;
        String userID = null;
        String status = null;
//		String productCode = null;
        ArrayList transferList = null;
        boolean isMatched = false;
        ChannelTransferVO transferVO = null;
        ChannelUserVO channelUserVO = null;
        ArrayList hierarchyList = null;
        try {

            this.validateC2cAndO2cRequest(requestVO, response, responseSwag, locale, enquiryType, searchBy);

            if (PretupsErrorCodesI.MULTI_VALIDATION_ERROR.equals(response.getMessageCode())) {
                return;
            }

            if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
                mcomCon = new MComConnection();
                con = mcomCon.getConnection();
            } else {
//				if (SystemPreferences.IS_SEPARATE_RPT_DB && PretupsI.RESET_CHECKBOX.equals(theForm.getCurrentDateFlagForUserCode())) {
                if (SystemPreferences.IS_SEPARATE_RPT_DB) {
                    mcomCon = new MComReportDBConnection();
                    con = mcomCon.getConnection();
                } else {
                    mcomCon = new MComConnection();
                    con = mcomCon.getConnection();
                }
            }

            channelUserWebDAO = new ChannelUserWebDAO();
            userDao = new UserDAO();
            channelTransferDAO = new ChannelTransferDAO();

            final ChannelUserVO sessionUserVO = userDao.loadAllUserDetailsByLoginID(con,
                    oAuthUserData.getData().getLoginid());

            if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
                transactionID = requestVO.getTransactionID().trim();

            } else if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy)) {

                fromDate = BTSLUtil.getDateFromDateString(requestVO.getFromDate());
                toDate = BTSLUtil.getDateFromDateString(requestVO.getToDate());
                transferSubTypeCode = requestVO.getTransferSubType();
                toUserCode = PretupsBL.getFilteredMSISDN(requestVO.getReceiverMsisdn().trim());
				transferCategory = requestVO.getTransferCategory();
            } else {

                fromDate = BTSLUtil.getDateFromDateString(requestVO.getFromDate());
                toDate = BTSLUtil.getDateFromDateString(requestVO.getToDate());

                userID = requestVO.getUserID();
                status = requestVO.getOrderStatus();
                transferSubTypeCode = requestVO.getTransferSubType();
//				productCode = requestVO.getProductCode();
                transferCategory = requestVO.getTransferCategory();
            }

            transferList = channelTransferDAO.loadEnquiryO2cList(con, searchBy, transactionID, userID, fromDate, toDate,
                    status, transferSubTypeCode, transferCategory, toUserCode,sessionUserVO.getUserType());

//			check tranferList is empty
            if (BTSLUtil.isNullOrEmptyList(transferList)) {

                String message = RestAPIStringParser.getMessage(locale,
                        "channeltransfer.enquirytransferlist.label.nodata", null);

                response.setMessage(message);
                response.setTransferListSize(0);
                response.setTransferList(transferList);
                response.setStatus(Integer.toString(HttpStatus.SC_OK));

                responseSwag.setStatus(HttpStatus.SC_OK);
                return;
            }
           if(sessionUserVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)){
               response.setMessage(PretupsI.SUCCESS);
               response.setTransferListSize(transferList.size());
               response.setTransferList(transferList);
               response.setStatus(Integer.toString(HttpStatus.SC_OK));

               responseSwag.setStatus(HttpStatus.SC_OK);
               return;
           }
            // load the user hierarchy to validate the sender msisdn and with in the login
            // user hierarchy.
            String sessionUserID = null;
            if (PretupsI.CATEGORY_TYPE_AGENT.equals(sessionUserVO.getCategoryVO().getCategoryType())
                    && PretupsI.NO.equals(sessionUserVO.getCategoryVO().getHierarchyAllowed())) {
                sessionUserID = sessionUserVO.getParentID();
            } else {
                sessionUserID = sessionUserVO.getUserID();
            }

            // load whole hierarchy of the form user and check to user under the hierarchy.
            hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con, sessionUserID, false);
            if (BTSLUtil.isNullOrEmptyList(hierarchyList)) {
                if (log.isDebugEnabled()) {
                    log.debug("enquirySearch", "Logged in user has no child user so there would be no transactions");
                }
                throw new BTSLBaseException(this, methodName, "o2cenquiry.transferlist.msg.nohierarchy");
            }

            // check inquiry allowed for given transaction ID(user)

            isMatched = false;

            for (int m = 0, n = transferList.size(); m < n; m++) {
                transferVO = (ChannelTransferVO) transferList.get(m);
                isMatched = false;
                for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                    channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                    if (channelUserVO.getUserID().equals(transferVO.getToUserID())
                            || channelUserVO.getUserID().equals(transferVO.getFromUserID())) {
                        isMatched = true;
                        break;
                    }
                }
                if (!isMatched) {
                    throw new BTSLBaseException(this, methodName, "o2cenquiry.viewo2ctransfers.msg.notauthorize");
                }
            }


//			response:
            response.setMessage(PretupsI.SUCCESS);
            response.setTransferListSize(transferList.size());
            response.setTransferList(transferList);
            response.setStatus(Integer.toString(HttpStatus.SC_OK));

            responseSwag.setStatus(HttpStatus.SC_OK);

        } catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            throw e;
        } finally {
            // if connection is not null then close the connection

            try {
                if (mcomCon != null) {
                    mcomCon.close("C2CBulkApprovalServiceImpl#loadAllC2cBulkApprovalDetails");
                    mcomCon = null;
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processChannelEnquiryC2c(C2cAndO2cEnquiryRequestVO requestVO, C2cAndO2cEnquiryResponseVO response,
                                         HttpServletResponse responseSwag, OAuthUser oAuthUserData, Locale locale, String enquiryType,
                                         String searchBy) throws BTSLBaseException, Exception {
        final String methodName = "processChannelEnquiryC2c";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        UserDAO userDao = null;
        ChannelTransferDAO channelTransferDAO = null;
        String transactionID = null;
        Date fromDate = null;
        Date toDate = null;
        String fromUserCode = null;
        String toUserCode = null;
        String transferSubTypeCode = null;
        String transferCategory = null;
        String userType = null;
        String staffUserID = null;
        String userID = null;
        String status = null;
//		String productCode = null;
        boolean isMatched = false;
        ChannelTransferVO transferVO = null;
        ChannelUserVO channelUserVO = null;
        ArrayList hierarchyList = null;
        ArrayList userList = null;

        try {

            this.validateC2cAndO2cRequest(requestVO, response, responseSwag, locale, enquiryType, searchBy);

            if (PretupsErrorCodesI.MULTI_VALIDATION_ERROR.equals(response.getMessageCode())) {
                return;
            }

            if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
                mcomCon = new MComConnection();
                con = mcomCon.getConnection();
            } else {
//				if (SystemPreferences.IS_SEPARATE_RPT_DB && PretupsI.RESET_CHECKBOX.equals(theForm.getCurrentDateFlagForUserCode())) {
                if (SystemPreferences.IS_SEPARATE_RPT_DB) {
                    mcomCon = new MComReportDBConnection();
                    con = mcomCon.getConnection();
                } else {
                    mcomCon = new MComConnection();
                    con = mcomCon.getConnection();
                }
            }

            channelUserWebDAO = new ChannelUserWebDAO();
            userDao = new UserDAO();
            channelTransferDAO = new ChannelTransferDAO();

            final ChannelUserVO sessionUserVO = userDao.loadAllUserDetailsByLoginID(con,
                    oAuthUserData.getData().getLoginid());

            // In case of SELFSTAFFC2C role(C2CTRFENQ and STFC2CTRFENQ roles not given to
            // the channel user), only staff user is allowed to enquire the details
            this.validateC2CEnquiryRoles(con, sessionUserVO);

            // load the user hierarchy to validate the sender msisdn and with in the login
            // user hierarchy.
            String sessionUserID = null;

            if (PretupsI.USER_TYPE_STAFF.equals(sessionUserVO.getUserType())
                    || (PretupsI.CATEGORY_TYPE_AGENT.equals(sessionUserVO.getCategoryVO().getCategoryType())
                    && PretupsI.NO.equals(sessionUserVO.getCategoryVO().getHierarchyAllowed()))) {
                sessionUserID = sessionUserVO.getParentID();
            } else {
                sessionUserID = sessionUserVO.getUserID();
            }

            // load whole hierarchy of the form user and check to user under
            // the hierarchy.
            if(!sessionUserVO.getDomainID().equals(PretupsI.OPERATOR_TYPE_OPT)){
            hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con, sessionUserID, false);
            if (BTSLUtil.isNullOrEmptyList(hierarchyList)) {
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Logged in user has no child user so there would be no transactions");
                }
                throw new BTSLBaseException(this, methodName, "o2cenquiry.transferlist.msg.nohierarchy");
            }
            }

            if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy) && !sessionUserVO.getDomainID().equals(PretupsI.OPERATOR_TYPE_OPT)) {

                // validate the from user code down to the user hierarchy of the
                // login user.
                transferVO = null;
                isMatched = false;
                channelUserVO = null;
                final String fromMsisdn = requestVO.getSenderMsisdn();
                if (!BTSLUtil.isNullString(fromMsisdn) && !SystemPreferences.SECONDARY_NUMBER_ALLOWED) {

                    for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                        channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                        if (channelUserVO.getMsisdn().equals(fromMsisdn)) {
                            isMatched = true;
                            break;
                        }
                    }
                    if (!isMatched) {
                        throw new BTSLBaseException(this, methodName,
                                "transferenquiry.enquirysearchattribute.msg.notauthorise", 0,
                                new String[] { fromMsisdn }, "searchattribute");
                    }

                } else if (!BTSLUtil.isNullString(fromMsisdn)) {
                    UserPhoneVO userPhoneVO = null;
                    final UserDAO userDAO = new UserDAO();
                    userPhoneVO = userDAO.loadUserAnyPhoneVO(con, fromMsisdn);

                    if (userPhoneVO != null) {
                        for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                            channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                            if (channelUserVO.getUserID().equals(userPhoneVO.getUserId())) {
                                isMatched = true;
                                break;
                            }
                        }
                        if (!isMatched) {
                            throw new BTSLBaseException(this, methodName,
                                    "transferenquiry.enquirysearchattribute.msg.notauthorise", 0,
                                    new String[] { fromMsisdn }, "searchattribute");
                        }
                    }

                }

            } else if (PretupsI.SEARCH_BY_ADVANCE.equalsIgnoreCase(searchBy) && !sessionUserVO.getDomainID().equals(PretupsI.OPERATOR_TYPE_OPT)) {
                transferVO = null;
                isMatched = false;
                channelUserVO = null;

                for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                    channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                    if (channelUserVO.getUserID().equals(requestVO.getUserID())) {
                        isMatched = true;
                        break;
                    }
                }

                if (!isMatched) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USERID_NOT_IN_HIERARCHY);
                }
            }

//			Getting request data
            if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
                transactionID = requestVO.getTransactionID().trim();

            } else if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy)) {

                fromDate = BTSLUtil.getDateFromDateString(requestVO.getFromDate());
                toDate = BTSLUtil.getDateFromDateString(requestVO.getToDate());
                transferSubTypeCode = requestVO.getTransferSubType();
                fromUserCode = requestVO.getSenderMsisdn();
                toUserCode = requestVO.getReceiverMsisdn();
            } else {

                fromDate = BTSLUtil.getDateFromDateString(requestVO.getFromDate());
                toDate = BTSLUtil.getDateFromDateString(requestVO.getToDate());

                userID = requestVO.getUserID();
                status = requestVO.getOrderStatus();
                transferSubTypeCode = requestVO.getTransferSubType();
                transferCategory = requestVO.getTransferCategory();
//				productCode = requestVO.getProductCode();
                userType = requestVO.getUserType();
            }

            if (PretupsI.USER_TYPE_STAFF.equalsIgnoreCase(requestVO.getUserType())) {
                userList = this.loadStaffDetails(con, requestVO, sessionUserVO);
                if (BTSLUtil.isNullOrEmptyList(userList)) {
                    String message = RestAPIStringParser.getMessage(locale,
                            PretupsErrorCodesI.NO_STAFF_LOGINID, new String[] {""});
                    response.setMessageCode(PretupsErrorCodesI.NO_STAFF_LOGINID);
                    response.setMessage(message);
                    response.setTransferListSize(0);
                    response.setStatus(Integer.toString(HttpStatus.SC_OK));
                    responseSwag.setStatus(HttpStatus.SC_OK);
                    return ;
                }
                final StringBuffer str = new StringBuffer();
                for (int k = 0; k < userList.size(); k++) {
                    ListValueVO user = (ListValueVO) userList.get(k);
                    if (!BTSLUtil.isNullString(user.getValue())) {
                        str.append("'");
                        str.append(user.getValue());
                        str.append("',");
                    }
                }

                staffUserID = str.substring(0, str.length() - 1);
            }

            ArrayList tempTransferList = channelTransferDAO.loadC2cEnquiryList(con, searchBy, transactionID, userID,
                    fromDate, toDate, status, transferSubTypeCode, transferCategory, fromUserCode, toUserCode, staffUserID, userType,sessionUserVO.getDomainID());

            ArrayList transferList = new ArrayList();

            // Filter transfer list on the basis of user type(CHANNEL/STAFF)
            if (PretupsI.USER_TYPE_STAFF.equalsIgnoreCase(requestVO.getUserType()) || sessionUserVO.getDomainID().equals(PretupsI.OPERATOR_TYPE_OPT)) {
//				transferList = this.getFinalTransferListForStaff(tempTransferList, userList);
                transferList = tempTransferList;
            } else {
                transferList = this.getFinalTransferListForChnlUser(tempTransferList, hierarchyList, searchBy);
            }

            if (BTSLUtil.isNullOrEmptyList(transferList)) {
                String message = RestAPIStringParser.getMessage(locale,
                        "channeltransfer.enquirytransferlist.label.nodata", null);

                response.setMessage(message);
                response.setTransferListSize(0);
            } else {
                response.setMessage(PretupsI.SUCCESS);
                response.setTransferListSize(transferList.size());
            }

//			response:

            response.setTransferList(transferList);
            response.setStatus(Integer.toString(HttpStatus.SC_OK));

            responseSwag.setStatus(HttpStatus.SC_OK);

        } catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            throw e;
        } finally {
            // if connection is not null then close the connection

            try {
                if (mcomCon != null) {
                    mcomCon.close("C2CBulkApprovalServiceImpl#loadAllC2cBulkApprovalDetails");
                    mcomCon = null;
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited");
            }
        }
    }


    private void validateC2CEnquiryRoles( Connection con,  ChannelUserVO sessionUserVO ) throws BTSLBaseException {

        final UserRolesDAO userRolesDAO = new UserRolesDAO();
        final HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> rolesMap =
                userRolesDAO.loadRolesListByUserId_new(con, sessionUserVO.getUserID(), sessionUserVO.getCategoryCode(), "N");

        HashMap<String, ArrayList<UserRolesVO>> c2cRolesMap = rolesMap.get(PretupsI.GROUP_ROLE_CHNL_ENQ);

        ArrayList<UserRolesVO> c2cEnqRoles = new ArrayList<UserRolesVO>();
        if(c2cRolesMap!= null) {
            c2cEnqRoles = c2cRolesMap.get(PretupsI.SUBGROUP_ROLE_CHNL_ENQ);
            if(c2cEnqRoles == null)
                c2cEnqRoles = c2cRolesMap.get("sub_group");

        }

        final boolean c2cEnqRoleFlag = c2cEnqRoles.stream().anyMatch( userRolesVO -> userRolesVO.getRoleCode().equals("C2CTRFENQ"));
        final boolean c2cStaffEnqRoleFlag = c2cEnqRoles.stream().anyMatch( userRolesVO -> userRolesVO.getRoleCode().equals("STFC2CTRFENQ"));
        final boolean c2cStaffSelfEnqRoleFlag = c2cEnqRoles.stream().anyMatch( userRolesVO -> userRolesVO.getRoleCode().equals("SELFSTAFFC2C"));

        if ((PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(sessionUserVO.getUserType())) && (!c2cEnqRoleFlag && !c2cStaffEnqRoleFlag && c2cStaffSelfEnqRoleFlag)) {
            throw new BTSLBaseException(this, "validateC2CEnquiryRoles", "staffc2senquiry.viewc2stransfers.notvaliduser");
        } else if (!c2cEnqRoleFlag && !c2cStaffEnqRoleFlag && !c2cStaffSelfEnqRoleFlag) {
            throw new BTSLBaseException(this, "validateC2CEnquiryRoles", PretupsErrorCodesI.NO_C2C_ENQ_ROLES);
        }
    }


    private ArrayList loadStaffDetails(Connection con, C2cAndO2cEnquiryRequestVO requestVO, ChannelUserVO channelUserVO)
            throws BTSLBaseException {

        String loginID = null;

        UserWebDAO userwebDAO = new UserWebDAO();
        if ((PretupsI.ALL.equalsIgnoreCase(requestVO.getStaffLoginID()))
                || (BTSLUtil.isNullString(requestVO.getStaffLoginID()))) {
            loginID = "%%%"; // for selecting all the child users
        } else if ("SELF".equals(requestVO.getStaffLoginID())) {
            loginID = channelUserVO.getLoginID();
        } else {
            loginID = requestVO.getStaffLoginID();
        }

        String userID = null;
        if(PretupsI.USER_TYPE_STAFF.equalsIgnoreCase(channelUserVO.getUserType())) {
            userID = channelUserVO.getParentID();
        } else {
            userID = channelUserVO.getUserID();
        }

        ArrayList userList = userwebDAO.loadUserListByLogin(con, userID, PretupsI.STAFF_USER_TYPE,
                loginID);

        if ((userList.size() <= 0 || "%".equals(requestVO.getStaffLoginID()))) {
            throw new BTSLBaseException(this, "staffChnlEnquirySearch",
                    "staffc2senquiry.viewc2stransfers.error.loginidnotexist", "searchattribute");
        }

        if (!BTSLUtil.isNullString(requestVO.getStaffLoginID()) && !PretupsI.ALL.equals(requestVO.getStaffLoginID())) {

            for (int i = 0; i < userList.size(); i++) {
                // for removing the user who have logged in and if he is
                // looking for transaction done by other member.
                final ListValueVO user = (ListValueVO) userList.get(i);
                if (!loginID.equals(user.getLabel())) {
                    userList.remove(i);
                }
            }
        }

        return userList;

    }

//	private ArrayList getFinalTransferListForStaff(ArrayList tempTransferList, ArrayList userList) {
//
//		final ArrayList chnlTransferVOList = new ArrayList();
//		int transfersListSize = tempTransferList.size();
//		for (int i = 0; i < transfersListSize; i++) {
//
//			ChannelTransferVO chnlTransferVO = (ChannelTransferVO) tempTransferList.get(i);
//			chnlTransferVO.setActiveUserName(chnlTransferVO.getFromUserName());
//
//			for (int k = 0; k < userList.size(); k++) {
//				final ListValueVO user = (ListValueVO) userList.get(k);
//				if (user.getValue().equals(chnlTransferVO.getActiveUserId())) {
//					chnlTransferVO.setActiveUserName(user.getOtherInfo());
//					chnlTransferVOList.add(chnlTransferVO);
//					break;
//				}
//			}
//		}
//
//		return chnlTransferVOList;
//	}

    private ArrayList getFinalTransferListForChnlUser(ArrayList tempTransferList, ArrayList hierarchyList,
                                                      String searchBy) throws BTSLBaseException {

        final String methodName = "getFinalTransferListForChnlUser";
        final ArrayList chnlTransferVOList = new ArrayList();
        ChannelUserVO channelUserVO = null;
        boolean isMatched = false;
        ChannelTransferVO transferVO = null;

        if (!BTSLUtil.isNullOrEmptyList(tempTransferList)) {
            // check inquiry allowed for given transaction ID
            if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
                isMatched = false;
                for (int m = 0, n = tempTransferList.size(); m < n; m++) {
                    transferVO = (ChannelTransferVO) tempTransferList.get(m);
                    isMatched = false;
                    for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                        channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                        if (channelUserVO.getUserID().equals(transferVO.getFromUserID())) {
                            chnlTransferVOList.add(transferVO);
                            isMatched = true;
                            break;
                        }
                    }
                    if (!isMatched) {
                        throw new BTSLBaseException(this, methodName,
                                "transferenquiry.enquirysearchattribute.msg.notauthorise2",
                                new String[] { transferVO.getTransferID() });
                    }
                }
            } else {
                for (int m = 0, n = tempTransferList.size(); m < n; m++) {
                    transferVO = (ChannelTransferVO) tempTransferList.get(m);
                    for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                        channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                        if (channelUserVO.getUserID().equals(transferVO.getFromUserID())) {
                            chnlTransferVOList.add(transferVO);
                            break;
                        }
                    }
                }
            }
        }

        return chnlTransferVOList;
    }

    private void validateC2cAndO2cRequest(C2cAndO2cEnquiryRequestVO requestVO, C2cAndO2cEnquiryResponseVO response,
                                          HttpServletResponse responseSwag, Locale locale, String enquiryType, String searchBy)
            throws BTSLBaseException {
        // basic form validation at api level
        final String methodName = "validateC2cAndO2cRequest";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }


        MasterErrorList masterError = null;

        ErrorMap errorMap = new ErrorMap();

        ArrayList<String> errorList = new ArrayList();
        ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();

        if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
            if (BTSLUtil.isNullString(requestVO.getTransactionID())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "Transaction ID" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }
        } else if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy)) {

            if (PretupsI.CHANNEL_TYPE_C2C.equals(enquiryType) && BTSLUtil.isNullString(requestVO.getSenderMsisdn())
                    && BTSLUtil.isNullString(requestVO.getReceiverMsisdn())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "Sender mobile number and Receiver mobile number both" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            } else if (PretupsI.CHANNEL_TYPE_C2C.equals(enquiryType)
                    && !BTSLUtil.isNullString(requestVO.getSenderMsisdn())
                    && requestVO.getSenderMsisdn().equals(requestVO.getReceiverMsisdn())) {
                // from and to msisdn can't be same. Only applicable for c2c

                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.FROM_AND_TO_MSISDN_CANNOT_BE_SAME);
                String msg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.FROM_AND_TO_MSISDN_CANNOT_BE_SAME, null);
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }

            if (PretupsI.CHANNEL_TYPE_O2C.equals(enquiryType) && BTSLUtil.isNullString(requestVO.getReceiverMsisdn())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "Receiver msisdn" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }

            if (BTSLUtil.isNullString(requestVO.getFromDate())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "From date" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }
            if (BTSLUtil.isNullString(requestVO.getToDate())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "To date" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }
        } else if (PretupsI.SEARCH_BY_ADVANCE.equalsIgnoreCase(searchBy)) {

            if (BTSLUtil.isNullString(requestVO.getFromDate())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "From date" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }
            if (BTSLUtil.isNullString(requestVO.getToDate())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "To date" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }

            if (BTSLUtil.isNullString(requestVO.getUserID())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "User ID" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }

//			if (BTSLUtil.isNullString(requestVO.getProductCode())) {
//				masterError = new MasterErrorList();
//				masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
//				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
//						new String[] { "Product code" });
//				masterError.setErrorMsg(msg);
//				masterErrorLists.add(masterError);
//			}

            if (BTSLUtil.isNullString(requestVO.getTransferSubType())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "Transfer sub type" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }

            if (BTSLUtil.isNullString(requestVO.getOrderStatus())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "Order status" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }

            if (BTSLUtil.isNullString(requestVO.getTransferCategory())) {
                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "Transfer category" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }

            if (PretupsI.CHANNEL_TYPE_C2C.equals(enquiryType) && PretupsI.USER_TYPE_STAFF.equalsIgnoreCase(requestVO.getUserType())
                    && BTSLUtil.isNullString(requestVO.getStaffLoginID())) {

                masterError = new MasterErrorList();
                masterError.setErrorCode(PretupsErrorCodesI.CAN_NOT_NULL);
                String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CAN_NOT_NULL,
                        new String[] { "Staff login ID" });
                masterError.setErrorMsg(msg);
                masterErrorLists.add(masterError);
            }

            // Validate USER TYPE
            if(PretupsI.CHANNEL_TYPE_C2C.equals(enquiryType)) {
                if (!(PretupsI.USER_TYPE_STAFF.equalsIgnoreCase(requestVO.getUserType())
                        || PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(requestVO.getUserType())
                        || PretupsI.ALL.equalsIgnoreCase(requestVO.getUserType())))
                {

                    masterError = new MasterErrorList();
                    masterError.setErrorCode(PretupsErrorCodesI.C2C_ENQ_INVALID_USER_TYPE);
                    String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_ENQ_INVALID_USER_TYPE, null);
                    masterError.setErrorMsg(msg);
                    masterErrorLists.add(masterError);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Invalid Search by, allowd options are: " + PretupsI.SEARCH_BY_TRANSACTIONID + ","
                        + PretupsI.SEARCH_BY_MSISDN + "," + PretupsI.SEARCH_BY_ADVANCE);
            }
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_ENQUIRY_TYPE);
        }

        errorMap.setMasterErrorList(masterErrorLists);

        if (errorMap.getMasterErrorList().size() >= 1) {

            response.setStatus(Integer.toString(HttpStatus.SC_BAD_REQUEST));
            response.setErrorMap(errorMap);
            response.setMessageCode(PretupsErrorCodesI.MULTI_VALIDATION_ERROR);
            String message = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MULTI_VALIDATION_ERROR,
                    new String[] { errorMap.getMasterErrorList().get(0).getErrorMsg() });
            response.setMessage(errorMap.getMasterErrorList().get(0).getErrorMsg());
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);

            if (log.isDebugEnabled()) {
                log.error(methodName, "MULTI_VALIDATION_ERROR " + errorMap);

            }
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadClosingBalanceData(Connection con, UserVO sessionUser, ClosingBalanceEnquiryRequestVO requestVO,
                                       ClosingBalanceEnquiryResponseVO response, HttpServletResponse response1, Locale locale)
            throws BTSLBaseException {
        String methodName = "loadClosingBalanceData";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        UsersReportForm thisForm = new UsersReportForm();
        String tempCatCode = null;
        ListValueVO listValueVO = null;
        ArrayList<ChannelUserTransferVO> userList;
        ArrayList<UserClosingBalanceVO> userVOList;
        UserVO userVO = null;
        boolean intermediateError = false;
        String parentCategoryCode = null;

        userVO = sessionUser;
        tempCatCode = requestVO.getCatCode();

        // setting zone list
        if (TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
            thisForm.setZoneList(
                    new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID()));
        else
            thisForm.setZoneList(userVO.getGeographicalAreaList());

        if (requestVO.getZoneCode().equals(TypesI.ALL)) {
            thisForm.setZoneName(TypesI.ALL);
        } else {
            String zone = null;
            for (int i = 0; i < thisForm.getZoneList().size(); i++) {
                UserGeographiesVO obj = (UserGeographiesVO) thisForm.getZoneList().get(i);
                if (obj.getGraphDomainCode().equals(thisForm.getZoneCode())) {
                    zone = obj.getGraphDomainName();
                    break;
                }
            }
            thisForm.setZoneName(zone);
        }

        // setting domain list
        thisForm.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
        final ArrayList<ListValueVO> loggedInUserDomainList = new ArrayList<>();
        commonUsersList(thisForm, loggedInUserDomainList, userVO);

        listValueVO = BTSLUtil.getOptionDesc(requestVO.getDomainCode(), thisForm.getDomainList());
        thisForm.setDomainName(listValueVO.getLabel());

        // set parent category list
        final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
        if ((PretupsI.OPERATOR_USER_TYPE).equals(userVO.getUserType())) {
            thisForm.setParentCategoryList(categoryWebDAO.loadCategoryReportList(con));
        } else if ((PretupsI.CHANNEL_USER_TYPE).equals(userVO.getUserType())) {
            final int loginSeqNo = userVO.getCategoryVO().getSequenceNumber();
            thisForm.setParentCategoryList(categoryWebDAO.loadCategoryReporSeqtList(con, loginSeqNo));
        }

        final ChannelUserReportDAO channelUserReportDAO = new ChannelUserReportDAO();
        parentCategoryCode = requestVO.getCatCode();
        thisForm.setParentCategoryCode(parentCategoryCode);
        if (parentCategoryCode.equals(TypesI.ALL)) {
            thisForm.setCategoryName(TypesI.ALL);
            userList = channelUserReportDAO.loadUserListBasisOfZoneDomainCategory(con, PretupsI.ALL,
                    requestVO.getDomainCode(), requestVO.getZoneCode(), requestVO.getUserName(), userVO.getUserID());
        } else {
            listValueVO = BTSLUtil.getOptionDesc(parentCategoryCode, thisForm.getParentCategoryList());
            thisForm.setCategoryName(listValueVO.getLabel());
            userList = channelUserReportDAO.loadUserListBasisOfZoneDomainCategory(con, requestVO.getCatCode(),
                    requestVO.getDomainCode(), requestVO.getZoneCode(), requestVO.getUserName(), userVO.getUserID());
        }

        if (requestVO.getUserName().equalsIgnoreCase(TypesI.ALL)) {
            thisForm.setUserID(PretupsI.ALL);
        } else if (userList == null || userList.isEmpty()) {
            intermediateError = true;
            thisForm.setParentCategoryCode(tempCatCode);
        } else if (userList.size() == 1) {
            final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList.get(0);
            thisForm.setUserName(channelUserTransferVO.getUserName());
            thisForm.setUserID(channelUserTransferVO.getUserID());
        } else if (userList.size() > 1) {
            boolean flag = true;
            for (int i = 0, j = userList.size(); i < j; i++) {
                final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList.get(i);
                if (requestVO.getUserName().equalsIgnoreCase(channelUserTransferVO.getUserName())) {// see if required
                    // username is in
                    // list
                    thisForm.setUserName(channelUserTransferVO.getUserName());
                    thisForm.setUserID(channelUserTransferVO.getUserID());
                    flag = false;
                    break;
                }
            }
            if (flag) {
                intermediateError = true;
                thisForm.setParentCategoryCode(tempCatCode);
                throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                        PretupsErrorCodesI.EXT_USRADD_USERNAME_INCORRECT);
            }
        }

        java.sql.Date fromDate = null;
        java.sql.Date toDate = null;
        String strToDate = null;
        String fromAmount = "";
        String toAmount = "";

        try {
            fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(requestVO.getFromDate()));
            toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(requestVO.getToDate()));
        } catch (Exception e) {
            throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                    PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Date" });
        }

        if (BTSLUtil.isNullString(requestVO.getFromAmount())) {
            fromAmount = "0";
        } else {
            fromAmount = requestVO.getFromAmount();
        }
        if (BTSLUtil.isNullString(requestVO.getToAmount())) {
            toAmount = "999999";
        } else {
            toAmount = requestVO.getToAmount();
        }

        if (!intermediateError) {
            userVOList = new UserReportDAO().loadUserClosingBalance(con, userVO.getNetworkID(), requestVO.getZoneCode(),
                    requestVO.getDomainCode(), requestVO.getCatCode(), thisForm.getUserID(), userVO.getUserID(),
                    fromDate, toDate, fromAmount, toAmount, userVO.getUserType());

            O2CServiceImpl o2cImpl = new O2CServiceImpl();
            O2CProductsResponseVO temp_response = new O2CProductsResponseVO();
            HashMap<String, String> dict = new HashMap<>();
            o2cImpl.processO2CProductDownlaod(con, (ChannelUserVO) sessionUser, temp_response);
            for (O2CProductResponseData data : temp_response.getProductsList()) {
                dict.put(data.getCode(), data.getName());
            }

            modifyBalanceData(response, dict, userVOList, requestVO.getFromDate(), requestVO.getToDate());
            response.setBalanceList(userVOList);

            if (response.getModifiedData() != null && requestVO.getFileType() != null) {
                ArrayList<ArrayList<String>> filterData = new ArrayList<>();
                UserClosingBalanceVO filterVO = response.getBalanceList().get(0);
                filterData.add(
                        Stream.of(PretupsI.DOMAIN, sessionUser.getDomainName()).collect(toCollection(ArrayList::new)));
                filterData.add(
                        Stream.of(PretupsI.CATEGORY, filterVO.getUserCategory()).collect(toCollection(ArrayList::new)));
                filterData.add(
                        Stream.of(PretupsI.GEOGRAPHY, sessionUser.getGeographicalAreaList().get(0).getGraphDomainName())
                                .collect(toCollection(ArrayList::new)));
                filterData.add(
                        Stream.of(PretupsI.USER_NAME, requestVO.getUserName()).collect(toCollection(ArrayList::new)));
                filterData.add(Stream.of(PretupsI.DATE_RANGE, requestVO.getFromDate() + " - " + requestVO.getToDate())
                        .collect(toCollection(ArrayList::new)));
                filterData.add(Stream.of(PretupsI.FROM_AMOUNT, requestVO.getFromAmount())
                        .collect(toCollection(ArrayList::new)));
                filterData.add(
                        Stream.of(PretupsI.TO_AMOUNT, requestVO.getToAmount()).collect(toCollection(ArrayList::new)));
                createDownloadReportForUserClosingBalance(requestVO, response, filterData);
            }

        }
        if (response.getModifiedData() == null) {
            throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                    PretupsErrorCodesI.NO_RECORD_AVAILABLE);
        }
        response.setStatus(Integer.toString(PretupsI.RESPONSE_SUCCESS));

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
    }

    private void createDownloadReportForUserClosingBalance(ClosingBalanceEnquiryRequestVO requestVO,
                                                           ClosingBalanceEnquiryResponseVO response, ArrayList<ArrayList<String>> filterData)
            throws BTSLBaseException {
        String methodName = "createDownloadReportForUserClosingBalance";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        String fileName = "viewUserClosingBalanceEnquiry" + (System.currentTimeMillis());
        String fileType1 = requestVO.getFileType().toUpperCase();
        String fileData = null;

        try {
            List<HeaderColumn> headerColumns1 = new ArrayList<HeaderColumn>();
            headerColumns1.add(new HeaderColumn(PretupsI.PRODUCT_NAME, PretupsI.PRODUCT_NAME));
            headerColumns1.add(new HeaderColumn(PretupsI.USER_NAME, PretupsI.USER_NAME));
            headerColumns1.add(new HeaderColumn(PretupsI.USER_MOBILE_NUMBER, PretupsI.USER_MOBILE_NUMBER));
            headerColumns1.add(new HeaderColumn(PretupsI.CATEGORY, PretupsI.CATEGORY));
            headerColumns1.add(new HeaderColumn(PretupsI.GEOGRAPHY, PretupsI.GEOGRAPHY));
            headerColumns1.add(new HeaderColumn(PretupsI.USER_PARENTS_NAME, PretupsI.USER_PARENTS_NAME));
            headerColumns1.add(new HeaderColumn(PretupsI.PARENT_MOBILE_NUMBER, PretupsI.PARENT_MOBILE_NUMBER));
            headerColumns1.add(new HeaderColumn(PretupsI.OWNER_NAME, PretupsI.OWNER_NAME));
            headerColumns1.add(new HeaderColumn(PretupsI.OWNER_MOBILE_NUMBER, PretupsI.OWNER_MOBILE_NUMBER));

            // setting filter data

            for (String dateLabel : response.getDateColumnLabels())
                headerColumns1.add(new HeaderColumn(dateLabel, dateLabel));

            if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(fileType1)) {
                fileData = createExcelXFile(fileName, response.getModifiedData(), headerColumns1, filterData);
            } else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(fileType1)) {
                fileData = createCSVFile(response.getModifiedData(), headerColumns1, filterData);
            } else
                fileData = createExcelFile(fileName, response.getModifiedData(), headerColumns1, filterData);

            response.setFileName(fileName + "." + fileType1.toLowerCase());
            response.setFileType(fileType1.toLowerCase());
            response.setFileAttachment(fileData);

        } catch (Exception e) {
            throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                    PretupsErrorCodesI.FILE_WRITE_ERROR);
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
    }

    public String createExcelFile(String fileName, ArrayList<ArrayList<String>> dataList,
                                  List<HeaderColumn> editColumns, ArrayList<ArrayList<String>> filterData) throws Exception {
        String fileDat = null;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet(fileName);

            // processing filter data
            Font headerFontd1 = workbook.createFont();
            headerFontd1.setColor(Font.COLOR_NORMAL);
            headerFontd1.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
            CellStyle headerCellStyle1 = workbook.createCellStyle();
            headerCellStyle1.setFont(headerFontd1);
            int rowInd = 0;
            for (ArrayList<String> fData : filterData) {
                Row filterRow = sheet.createRow(rowInd++);
                Cell cell = filterRow.createCell(0);
                cell.setCellValue(fData.get(0));
                cell.setCellStyle(headerCellStyle1);

                cell = filterRow.createCell(1);
                cell.setCellValue(fData.get(1));
            }

            // Processing file header data
            List<String> displayNamList = editColumns.stream().map(HeaderColumn::getDisplayName)
                    .collect(Collectors.toList());
            int displayNameListSize = displayNamList.size();
            Font headerFontd = workbook.createFont();
            headerFontd.setColor(Font.COLOR_NORMAL);
            headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFontd);
            Row headerRow = sheet.createRow(rowInd++);
            for (int col = 0; col < displayNameListSize; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(displayNamList.get(col));
                cell.setCellStyle(headerCellStyle);
                try {
                    sheet.autoSizeColumn(col);
                }catch (Exception e) {
                    log.error("", "Error occurred while autosizing columns");
                    e.printStackTrace();
                }
            }

            // Processing file body data
            List<String> columnNameLis = editColumns.stream().map(HeaderColumn::getColumnName)
                    .collect(Collectors.toList());
            int columnNameListSize = columnNameLis.size();
            int voucherAvailListSize = dataList.size();
            for (int i = 0; i < voucherAvailListSize; i++) {
                ArrayList<String> record = dataList.get(i);
                Row dataRow = sheet.createRow(rowInd++);

                for (int col = 0; col < columnNameListSize; col++) {
                    dataRow.createCell(col).setCellValue(record.get(col));
                }
            }

            workbook.write(outputStream);
            fileDat = new String(Base64.getEncoder().encode(outputStream.toByteArray()));
        } catch (IOException e) {
            log.error("Error occurred while generating excel file report.", e);
            throw new Exception("423423");
        }

        return fileDat;
    }

    public String createCSVFile(ArrayList<ArrayList<String>> dataList, List<HeaderColumn> editColumns,
                                ArrayList<ArrayList<String>> filterData) throws Exception {
        String fileData = null;
        try (StringWriter writer = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

            // processing filter data
            for (ArrayList<String> fData : filterData) {
                csvWriter.writeNext(fData.stream().toArray(String[]::new));
            }

            // Processing file header data
            List<String> displayNameList = editColumns.stream().map(HeaderColumn::getDisplayName)
                    .collect(Collectors.toList());
            csvWriter.writeNext(displayNameList.stream().toArray(String[]::new));

            // Processing file body data
            List<String> columnNameList = editColumns.stream().map(HeaderColumn::getColumnName)
                    .collect(Collectors.toList());
            int columnNameListSize = columnNameList.size();
            for (ArrayList<String> record : dataList) {
                String[] dataRow = new String[columnNameListSize];

                for (int col = 0; col < columnNameListSize; col++) {
                    dataRow[col] = record.get(col);
                }

                csvWriter.writeNext(dataRow);
            }

            String output = writer.toString();
            fileData = new String(Base64.getEncoder().encode(output.getBytes()));
        } catch (IOException e) {
            log.error("Error occurred while generating csv file report.", e);
            throw new Exception("423423");
        }

        return fileData;
    }

    public String createExcelXFile(String fileName, ArrayList<ArrayList<String>> data, List<HeaderColumn> editColumns,
                                   ArrayList<ArrayList<String>> filterData) throws Exception {
        String fileDat = null;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(fileName);

            // processing filter data
            Font headerFontd1 = workbook.createFont();
            headerFontd1.setColor(Font.COLOR_NORMAL);
            headerFontd1.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
            CellStyle headerCellStyle1 = workbook.createCellStyle();
            headerCellStyle1.setFont(headerFontd1);
            int rowInd = 0;
            for (ArrayList<String> fData : filterData) {
                Row filterRow = sheet.createRow(rowInd++);
                Cell cell = filterRow.createCell(0);
                cell.setCellValue(fData.get(0));
                cell.setCellStyle(headerCellStyle1);

                cell = filterRow.createCell(1);
                cell.setCellValue(fData.get(1));
            }

            // Processing file header data
            List<String> displayNamList = editColumns.stream().map(HeaderColumn::getDisplayName)
                    .collect(Collectors.toList());
            int displayNameListSize = displayNamList.size();
            Font headerFontd = workbook.createFont();
            headerFontd.setColor(Font.COLOR_NORMAL);
            headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFontd);
            Row headerRow = sheet.createRow(rowInd++);
            for (int col = 0; col < displayNameListSize; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(displayNamList.get(col));
                cell.setCellStyle(headerCellStyle);
                try {
                    sheet.autoSizeColumn(col);
                }catch (Exception e) {
                    log.error("", "Error occurred while autosizing columns");
                    e.printStackTrace();
                }
            }

            // Processing file body data
            List<String> columnNameLis = editColumns.stream().map(HeaderColumn::getColumnName)
                    .collect(Collectors.toList());
            int columnNameListSize = columnNameLis.size();
            int voucherAvailListSize = data.size();// no of rows
            for (int i = 0; i < voucherAvailListSize; i++) {
                ArrayList<String> record = data.get(i);
                Row dataRow = sheet.createRow(rowInd++);
                for (int col = 0; col < columnNameListSize; col++) {
                    dataRow.createCell(col).setCellValue(record.get(col));
                }
            }

            workbook.write(outputStream);
            fileDat = new String(Base64.getEncoder().encode(outputStream.toByteArray()));
        } catch (IOException e) {
            log.error("Error occurred while generating excel file report.", e);
            throw new Exception("423423");
        }

        return fileDat;
    }

    private void modifyBalanceData(ClosingBalanceEnquiryResponseVO response, HashMap<String, String> prodMap,
                                   ArrayList<UserClosingBalanceVO> userVOList, String fromDateString, String toDateString)
            throws BTSLBaseException {

        String methodName = "modifyBalanceData";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        int noOfDays = -1;
        ArrayList<String> dateArr = new ArrayList<>();
        Date fromDate;
        Date toDate;
        ArrayList<ArrayList<String>> modifiedData = new ArrayList<>();
        try {
            noOfDays = BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getDateFromDateString(fromDateString),
                    BTSLUtil.getDateFromDateString(toDateString));

            fromDate = BTSLUtil.getDateFromDateString(fromDateString);
            toDate = BTSLUtil.getDateFromDateString(toDateString);
            if (fromDate.compareTo(toDate) != 0) {
                Date tempDate = fromDate;
                for (int i = 0; i <= noOfDays; i++) {

                    if (!tempDate.after(toDate)) {
                        dateArr.add(BTSLUtil.getDateStringFromDate(tempDate));
                    }
                    tempDate = BTSLUtil.addDaysInUtilDate(tempDate, 1);
                }
            } else {
                dateArr.add(BTSLUtil.getDateStringFromDate(fromDate));
            }
            response.setDateColumnLabels(dateArr);

        } catch (Exception e) {
            throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                    PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Date" });
        }

        for (UserClosingBalanceVO data : userVOList) {
            if (data.getBalanceString() == null)
                continue;
            // parse all data in dateString
            HashSet<String> st = new HashSet<>();
            String[] list = data.getBalanceString().split(",");
            for (String temp : list)
                st.add(temp.split("::")[0]);
            ArrayList<String> prodCodeList = new ArrayList<>(st);
            HashMap<String, ArrayList<String>> dict = new HashMap<>();
            // seprate data for each product
            for (String pCode : prodCodeList) {
                ArrayList<String> tempData = new ArrayList<>();
                for (String temp : list)
                    if (temp.split("::")[0].equals(pCode))
                        tempData.add(temp);
                dict.put(pCode, tempData);
            }
            Collections.sort(prodCodeList);// sorting products
            // get data for each date for specific product
            for (String pCode : prodCodeList) {
                ArrayList<String> dateData = dict.get(pCode);
                int prevDayCount = 0;
                ArrayList<String> res = new ArrayList<>();
                for (String dData : dateData) {
                    int dayDiff = getDayDiffFromUserClosingBalance(fromDate, dData);
                    while (prevDayCount < dayDiff) {
                        res.add("NA");
                        prevDayCount++;
                    }
//					res.add(dData.split("::")[1]);// main data
                    res.add(Long.toString(BTSLUtil.parseDoubleToLong( BTSLUtil.getDisplayAmount(Long.parseLong(dData.split("::")[2])))));
                    if (prevDayCount == dayDiff)
                        prevDayCount++;
                }
                int dayDiff = BTSLUtil.getDifferenceInUtilDates(fromDate, toDate);
                while (prevDayCount <= dayDiff) {
                    res.add("NA");
                    prevDayCount++;
                }
                dict.put(pCode, res);
            }

            // creating mainData
            for (String pCode : prodCodeList) {
                ArrayList<String> mainData = new ArrayList<String>();
                mainData.add(prodMap.get(pCode));
                mainData.add(data.getUserName());
                mainData.add(data.getUserMSISDN());
                mainData.add(data.getUserCategory());
                mainData.add(data.getUserGeography());
                mainData.add(data.getParentUserName());
                mainData.add(data.getParentUserMSISDN());
                mainData.add(data.getOwnerUserName());
                mainData.add(data.getOwnerUserMSISDN());

                for (String dateWiseData : dict.get(pCode))
                    mainData.add(dateWiseData);
                modifiedData.add(mainData);
            }

        }

        if (modifiedData.size() == 0)
            modifiedData = null;
        response.setModifiedData(modifiedData);

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
    }

    public int getDayDiffFromUserClosingBalance(Date fromDate, String thisData) throws BTSLBaseException {// if thisData
        // has
        // fromDate
        // then this
        // func will
        // return 0
        String methodName = "getDayDiffFromUserClosingBalance";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        int daysFromStartDate = -1;
        try {
            if (QueryConstants.DB_POSTGRESQL.equals(Constants.getProperty(QueryConstants.PRETUPS_DB))) {
                daysFromStartDate = BTSLUtil.getDifferenceInUtilDates(fromDate,
                        BTSLUtil.getDateFromDateString(thisData.split("\\s+")[0], "yyyy-MM-dd"));
            } else {
                daysFromStartDate = BTSLUtil.getDifferenceInUtilDates(fromDate,
                        BTSLUtil.getDateFromDateString(thisData.split("::")[1], "dd-MMM-yy"));
            }
        } catch (Exception e) {
            throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                    PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Date" });
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
        return daysFromStartDate;
    }

    private void commonUsersList(UsersReportForm thisForm, ArrayList loggedInUserDomainList, UserVO userVO) {
        if (loggedInUserDomainList == null) {
            loggedInUserDomainList = new ArrayList();
        }
        if (thisForm.getDomainList().size() == 0) {
            loggedInUserDomainList.add(new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
            thisForm.setDomainList(loggedInUserDomainList);
            thisForm.setDomainCode(userVO.getDomainID());
            thisForm.setDomainName(userVO.getDomainName());
        } else if (thisForm.getDomainListSize() == 1) {
            ListValueVO listvo = (ListValueVO) thisForm.getDomainList().get(0);
            thisForm.setDomainCode(listvo.getValue());
            thisForm.setDomainName(listvo.getLabel());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void getAlertCounterSummaryData(Connection con, UserVO userVO, AlertCounterSummaryRequestVO requestVO,
                                           AlertCounterSummaryResponseVO response, HttpServletResponse response1, Locale locale)
            throws BTSLBaseException, ParseException {

        String methodName = "getAlertCounterSummaryData";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        Date tDate = null, frDate = null;
        String todate = null, fromdate = null;
        String fromMonth = null;
        UsersReportModel usersReportModel = new UsersReportModel();
        final UsersReportForm thisForm = new UsersReportForm();
        UserWebDAO userWebDAO = new UserWebDAO();
        ChannelUserVO userVO1 = (ChannelUserVO) userVO;
        userWebDAO.loadMisExecutedDates(con, userVO1);

        if (!BTSLUtil.isNullString(requestVO.getReqMonth())) {// setting month accordingly
            HashMap<String, String> mp = new HashMap<String, String>();
            for (String d : SystemPreferences.FORMAT_MONTH_YEAR.split("/")) {
                if (d.equals("mm"))
                    mp.put("m", requestVO.getReqMonth().split("/")[1]);
                else
                    mp.put("y", requestVO.getReqMonth().split("/")[0]);
            }
            requestVO.setReqMonth("01" + "/" + mp.get("m") + "/" + mp.get("y"));
            fromMonth = requestVO.getReqMonth().split("/")[1] + "/" + requestVO.getReqMonth().split("/")[2];
        }
        // handling reqDate
        if (!BTSLUtil.isNullString(requestVO.getReqDate())) {
            tDate = BTSLUtil.getDateFromDateString(requestVO.getReqDate());

            if (tDate != null && !(BTSLUtil.checkDateFromMisDates(tDate, tDate, userVO1, ProcessI.C2SMIS))) {
                final String arr[] = {
                        BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(userVO1.getC2sMisFromDate())),
                        BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(userVO1.getC2sMisToDate())) };
                throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                        PretupsErrorCodesI.DATE_RANGE_ERROR_ARG, arr);// have to add date not in range error
            }
        }

        if (tDate != null) {
            todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tDate));
            thisForm.setRptcurrentDate(BTSLUtil.reportDateFormat(todate));
        }

        // handling reqMonth

        if (!BTSLUtil.isNullString(requestVO.getReqMonth())) {
            frDate = BTSLUtil.getDateFromDateString(requestVO.getReqMonth(), PretupsI.DATE_FORMAT);
        }
        if (frDate != null) {
            fromdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(frDate));
            thisForm.setRptfromDate(BTSLUtil.reportDateFormat(fromdate)); // report
            // format date
            String toMonth = BTSLUtil.getDateWithDayOfMonth(BTSLUtil.getNoOfDaysInMonth(fromdate, PretupsI.DATE_FORMAT),
                    fromMonth);
            toMonth = BTSLUtil.sqlDateToDateYYYYString(
                    BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(toMonth, PretupsI.DATE_FORMAT)));
            thisForm.setRpttoDate(BTSLUtil.reportDateFormat(toMonth));
            if (frDate != null && !(BTSLUtil.checkDateFromMisDates(frDate, frDate, userVO1, ProcessI.C2SMIS))) {
                final String arr[] = {
                        BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(userVO1.getC2sMisFromDate())),
                        BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(userVO1.getC2sMisToDate())) };
                throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                        PretupsErrorCodesI.DATE_RANGE_ERROR_ARG, arr);// have to add date not in range error
            }
        }

        thisForm.setNetworkCode(userVO.getNetworkID());
        thisForm.setNetworkName(userVO.getNetworkName());
        thisForm.setReportHeaderName(userVO.getReportHeaderName());

        ListValueVO listValueVO = null;
        // date check that current or previous
        Date currentDate = new Date();
        Date tempdate = null;
        // below if else block sets from and to date
        if (!BTSLUtil.isNullString(requestVO.getReqMonth())) {
            tempdate = BTSLUtil.getDateFromDateString(requestVO.getReqMonth(), PretupsI.DATE_FORMAT);
            if (tempdate.getYear() + 1900 <= currentDate.getYear() + 1900) {
                thisForm.setRptfromDate(BTSLUtil
                        .reportDateFormat(BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tempdate))));
                // current month
                if (currentDate.getMonth() == tempdate.getMonth()
                        && tempdate.getYear() + 1900 == currentDate.getYear() + 1900) {
                    thisForm.setDateType(PretupsI.DATE_CHECK_CURRENT);
                    todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(currentDate));
                    thisForm.setRpttoDate(BTSLUtil.reportDateFormat(
                            BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(currentDate))));
                }
                // previous month
                else if ((currentDate.getMonth() > tempdate.getMonth()
                        && tempdate.getYear() + 1900 == currentDate.getYear() + 1900)
                        || currentDate.getYear() + 1900 > tempdate.getMonth() + 1900) {
                    thisForm.setDateType(PretupsI.DATE_CHECK_PREVIOUS);
                    final Calendar tempcal = BTSLDateUtil.getInstance();
                    tempcal.setTime(tempdate);
                    final int lastDate = tempcal.getActualMaximum(Calendar.DATE);
                    tempcal.add(Calendar.DATE, lastDate - 1);
                    todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tempcal.getTime()));
                    thisForm.setRpttoDate(BTSLUtil.reportDateFormat(
                            BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tempcal.getTime()))));
                }
            }
        } else if (!BTSLUtil.isNullString(requestVO.getReqDate())) {
            tempdate = BTSLUtil.getDateFromDateString(requestVO.getReqDate());
            currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
            if (currentDate.equals(tempdate)) {
                thisForm.setDateType(PretupsI.DATE_CHECK_CURRENT);
            } else if (currentDate.after(tempdate)) {
                thisForm.setDateType(PretupsI.DATE_CHECK_PREVIOUS);
            }
            fromdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tempdate));
            thisForm.setRptfromDate(BTSLUtil
                    .reportDateFormat(BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tempdate))));
            thisForm.setRpttoDate(BTSLUtil
                    .reportDateFormat(BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tempdate))));
        }

        SimpleDateFormat timeStampFormat;
        Timestamp fromDateTimeValue = null;
        Timestamp toDateTimeValue = null;
        Date fromDateParse;
        Date toDateParse;
        Date fromDate = null;
        Date toDate = null;

        UserZeroBalanceCounterSummaryDAO userZeroBalanceCounterSummaryDAO = new UserZeroBalanceCounterSummaryDAO();

        if (!BTSLUtil.isNullString(fromdate)) {
            fromDate = BTSLUtil.getDateFromDateString(fromdate + " 00:00:00", PretupsI.TIMESTAMP_DATESPACEHHMMSS);
            timeStampFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
            fromDateParse = timeStampFormat.parse(BTSLUtil.getDateTimeStringFromDate(fromDate));
            fromDateTimeValue = new Timestamp(fromDateParse.getTime());
        }

        if (!BTSLUtil.isNullString(todate)) {
            toDate = BTSLUtil.getDateFromDateString(todate + " 23:59:00", PretupsI.TIMESTAMP_DATESPACEHHMMSS);
            timeStampFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
            toDateParse = timeStampFormat.parse(BTSLUtil.getDateTimeStringFromDate(toDate));
            toDateTimeValue = new Timestamp(toDateParse.getTime());
        }

        // setting user report model
        usersReportModel.setNetworkCode(userVO.getNetworkID());
        usersReportModel.setThresholdType(requestVO.getThresholdType());
        usersReportModel.setParentCategoryCode(requestVO.getCatCode());
        usersReportModel.setParentCategoryCode(requestVO.getCatCode());
        usersReportModel.setDomainCode(requestVO.getDomainCode());
        usersReportModel.setZoneCode(requestVO.getGeoCode());
        usersReportModel.setZoneCode(requestVO.getGeoCode());
        usersReportModel.setLoginUserID(userVO.getUserID());
        try {
            if (userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
                ArrayList<UserZeroBalanceCounterSummaryVO> userZeroBalanceCounterSummaryList = userZeroBalanceCounterSummaryDAO
                        .loadUserBalanceReport(con, usersReportModel, fromDateTimeValue, toDateTimeValue);
                usersReportModel.setUserZeroBalanceCounterSummaryList(userZeroBalanceCounterSummaryList);
                response.setAlertList(userZeroBalanceCounterSummaryList);
            } else {
                ArrayList<UserZeroBalanceCounterSummaryVO> userZeroBalanceCounterSummaryListOne = userZeroBalanceCounterSummaryDAO
                        .loadzeroBalSummChannelUserReport(con, usersReportModel, fromDateTimeValue, toDateTimeValue);
                usersReportModel.setUserZeroBalanceCounterSummaryListOne(userZeroBalanceCounterSummaryListOne);
                response.setAlertList(userZeroBalanceCounterSummaryListOne);
            }

        } catch (Exception e) {
            throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                    PretupsErrorCodesI.SQL_ERROR_EXCEPTION);// throw sql exception
        }

        if (response.getAlertList() == null || response.getAlertList().size() == 0) {
            throw new BTSLBaseException(ChannelEnquiryServiceImpl.class.getName(), methodName,
                    PretupsErrorCodesI.NO_RECORD_AVAILABLE);
        }

        response.setStatus(Integer.toString(PretupsI.RESPONSE_SUCCESS));

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public void getBatchC2cTransferdetails(BatchC2cTransferRequestVO batchC2cTransferRequestVO,
                                           BatchC2cTransferResponseVO response, HttpServletResponse responseSwag, OAuthUser oAuthUserData, Locale locale, String searchBy)
            throws BTSLBaseException, Exception
    {
        final String methodName = "getBatchC2cTransferdetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserVO loginUservo=null;
        final String searchByBatchNo="BATCHNO";
        final String searchByAdvance="ADVANCE";
        List transferList=null;
        final UserDAO userDao=new UserDAO();
        final C2CBatchTransferWebDAO c2cBatchTransferWebDAO = new C2CBatchTransferWebDAO();
        String batchId=batchC2cTransferRequestVO.getBatchId().trim();
        String domainCode=batchC2cTransferRequestVO.getDomainCode().trim();
        String categoryCode=batchC2cTransferRequestVO.getCategoryCode().trim();
        String geographyCode=batchC2cTransferRequestVO.getGeographyCode().trim();
        String userId=batchC2cTransferRequestVO.getUserId().trim();
        String productCode=batchC2cTransferRequestVO.getProductCode().trim();
        Date fromDate=null;
        Date toDate=null;
        final String format = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            loginUservo=userDao.loadAllUserDetailsByLoginID(con, oAuthUserData.getData().getLoginid());
            if(searchBy.equalsIgnoreCase(searchByBatchNo)) {
                if(BTSLUtil.isNullorEmpty(batchId)) {
                    response.setMessage("Batch Id cannot be empty");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BAD_REQUEST);
                }
                transferList= c2cBatchTransferWebDAO.loadBatchDetailsListByBatchId(con, batchId);
            }else if(searchBy.equalsIgnoreCase(searchByAdvance)) {

                if(BTSLUtil.isNullorEmpty(domainCode)||BTSLUtil.isNullorEmpty(categoryCode)
                        ||BTSLUtil.isNullorEmpty(geographyCode)||BTSLUtil.isNullorEmpty(userId)
                        ||BTSLUtil.isNullorEmpty(productCode))
                {
                    response.setMessage("Domain or category or geography or userid or product  cannot be empty");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BAD_REQUEST);
                }

                try {
                    if (format.length() == batchC2cTransferRequestVO.getFromDate().length()
                            || format.length() == batchC2cTransferRequestVO.getToDate().length()) {
                        fromDate = sdf.parse(batchC2cTransferRequestVO.getFromDate());
                        toDate = sdf.parse(batchC2cTransferRequestVO.getToDate());
                    }
                    else {
                        response.setMessage("Invalid dates format or can't be empty");
                        throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.BAD_REQUEST);
                    }
                }
                catch (BTSLBaseException e) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BAD_REQUEST);
                }
                //fetching data
                transferList = c2cBatchTransferWebDAO.loadBatchDetailsListByAdvance(con, fromDate, toDate,domainCode,userId,categoryCode,loginUservo.getUserID());
                List<C2CBatchMasterVO> list1=transferList;
                if(!productCode.equalsIgnoreCase("all")) {
                    transferList=list1.stream().filter(p->p.getProductCode().equalsIgnoreCase(productCode)).collect(Collectors.toList());
                }
                else {
                    transferList=list1;
                }
            }
            else {
                response.setMessage("Invalid searchBy type");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.BAD_REQUEST);
            }

            if (transferList==null||transferList.size()==0) {
                String message = RestAPIStringParser.getMessage(locale,
                        "channeltransfer.enquirytransferlist.label.nodata", null);
                response.setMessage(message);
            } else {
                response.setMessage(PretupsI.SUCCESS);
            }
            response.setTransferList(transferList);
            response.setStatus(Integer.toString(HttpStatus.SC_OK));
            responseSwag.setStatus(HttpStatus.SC_OK);

        }catch (Exception  e) {
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }
        finally {
            try {
                if (mcomCon != null) {
                    mcomCon.close(this.classname +" # " + methodName);
                    mcomCon = null;
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited");
            }
        }
    }


    @SuppressWarnings("unchecked")
    public void loadC2STransferEnquiryListStaff(Connection con, UserVO userVO, C2SEnquiryRequestVO c2sEnquiryRequestVO,
                                                C2SEnquiryResponseVO response, HttpServletResponse response1, Locale locale)
    {

        final String methodName = "loadTransferEnquiryList";
        LogFactory.printLog(methodName, " Entered", log);

        C2STransferWebDAO c2STransferwebDAO = null;
        NetworkPrefixVO networkPrefixVO = null;
        String msisdnPrefix = null;
        String networkCode = null;
        Date fromDate = null, toDate = null;
        String receiverMsisdn = null;
        ArrayList userList = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        try {
            channelUserWebDAO = new ChannelUserWebDAO();
            final UserWebDAO userwebDAO = new UserWebDAO();
            final String showErrorTo = "entermsisdn";
            fromDate = BTSLUtil.getDateFromDateString(c2sEnquiryRequestVO.getFromDate());
            toDate = BTSLUtil.getDateFromDateString(c2sEnquiryRequestVO.getToDate());
            networkCode = userVO.getNetworkID();
            String service = c2sEnquiryRequestVO.getService();
            String transferID = null;
            String UserId = userVO.getUserID();
            String category_code = userVO.getCategoryCode();
            if (!BTSLUtil.isNullString(transferID = c2sEnquiryRequestVO.getTransferID())) {
                service = PretupsI.ALL;
                transferID = c2sEnquiryRequestVO.getTransferID().trim();
            }
            if (!BTSLUtil.isNullString(c2sEnquiryRequestVO.getReceiverMsisdn())) {
                // Change ID=ACCOUNTID
                // FilteredMSISDN is replaced by getFilteredIdentificationNumber
                // This is done because this field can contains msisdn or
                // account id
                receiverMsisdn = PretupsBL.getFilteredIdentificationNumber(c2sEnquiryRequestVO.getReceiverMsisdn());
                msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverMsisdn);
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    throw new BTSLBaseException(this, methodName, "c2senquiry.viewc2stransfers.msg.notsupportnetwork",
                            0, new String[] { c2sEnquiryRequestVO.getReceiverMsisdn() }, showErrorTo);
                }

            }

            // for c2s enquiry

            UserId = PretupsI.OPERATOR_USER_TYPE;
            String userName = null;
            /*
             * if (!(theForm.getUserType().equals(PretupsI.CUSTOMER_CARE)) &&
             * BTSLUtil.isNullString(receiverMsisdn) && BTSLUtil.isNullString(transferID)) {
             * if (((theForm.getLoginId().equals(PretupsI.ALL)) ||
             * theForm.getLoginId().equalsIgnoreCase("%")) &&
             * BTSLUtil.isNullString(theForm.getParentUserName())) { throw new
             * BTSLBaseException(this, methodName,
             * "c2s.reports.web.userreportform.error.msg.required.value", showErrorTo); } }
             */
            if ((c2sEnquiryRequestVO.getStaffUserID().equals(PretupsI.ALL))
                    || (BTSLUtil.isNullString(c2sEnquiryRequestVO.getStaffUserID()))) {
                userName = "%%%"; // for selecting all the child users
            } else {
                userName = c2sEnquiryRequestVO.getStaffUserID();
            }
            if (PretupsI.USER_TYPE_CHANNEL.equals(userVO.getUserType())
                    || PretupsI.STAFF_USER_TYPE.equals(userVO.getUserType())) {
                UserId = userVO.getUserID();
            } /*
             * else if (((!BTSLUtil.isNullString(receiverMsisdn) ||
             * (!BTSLUtil.isNullString(transferID) || !BTSLUtil.isNullString(userName)))) &&
             * (userVO.getCategoryCode().equals(PretupsI.BCU_USER))) { UserId =
             * PretupsI.OPERATOR_USER_TYPE; } else { if
             * (((BTSLUtil.isNullString(theForm.getZoneCode()) ||
             * BTSLUtil.isNullString(theForm.getDomainCode())) ||
             * BTSLUtil.isNullString(theForm.getParentCategoryCode())) &&
             * BTSLUtil.isNullString(receiverMsisdn) && BTSLUtil.isNullString(transferID)) {
             * throw new BTSLBaseException(this, methodName,
             * "c2s.reports.web.userreportform.error.msg.required.value", showErrorTo); } if
             * (BTSLUtil.isNullString(theForm.getParentUserID())) { UserId =
             * userVO.getUserID(); } else { UserId = c2sEnquiryRequestVO.getParentUserID();
             * } category_code = theForm.getParentCategoryCode().split("\\|")[1]; }
             */
            /*
             * if (UserId.equalsIgnoreCase(PretupsI.OPERATOR_USER_TYPE)) { if
             * (!BTSLUtil.isNullString(theForm.getParentUserID()) &&
             * theForm.getLoginId().equals(PretupsI.ALL)) { UserId =
             * theForm.getParentUserID(); userName = "%%%"; category_code =
             * theForm.getParentCategoryCode().split("\\|")[1]; } else if
             * (!BTSLUtil.isNullString(theForm.getParentUserID()) &&
             * !theForm.getLoginId().equals(PretupsI.ALL)) { UserId =
             * theForm.getParentUserID(); category_code =
             * theForm.getParentCategoryCode().split("\\|")[1]; } }
             */
            userList = userwebDAO.loadUserListByLogin(con, UserId, PretupsI.STAFF_USER_TYPE, userName);

            if (userList != null && userList.isEmpty() && !PretupsI.OPERATOR_USER_TYPE.equals(UserId)) {
                throw new BTSLBaseException(this, methodName, "c2senquiry.viewc2stransfers.msg.nohierarchy",
                        showErrorTo);
            }
            // removing all the user not requird for enquiry.
            ArrayList domainList = null;
            if (UserId.equalsIgnoreCase(PretupsI.OPERATOR_USER_TYPE)
                    && !(userVO.getCategoryCode().equalsIgnoreCase(PretupsI.CUSTOMER_CARE))) {
                final ArrayList domainList1 = BTSLUtil.displayDomainList(userVO.getDomainList());
                domainList = new ArrayList();
                int domainLists1 = domainList1.size();
                for (int i = 0; i < domainLists1; i++) {
                    final ListValueVO domain = (ListValueVO) domainList1.get(i);
                    domainList.add(domain.getValue());
                }
            }

            if (!c2sEnquiryRequestVO.getStaffUserID().equals(PretupsI.ALL)
                    && !BTSLUtil.isNullString(c2sEnquiryRequestVO.getStaffUserID())) {
                for (int i = 0; i < userList.size(); i++) {
                    final ListValueVO user = (ListValueVO) userList.get(i);
                    if (!userName.equals(user.getLabel())) {
                        userList.remove(i);
                    }
                }
            }
            if ((UserId.equalsIgnoreCase(PretupsI.OPERATOR_USER_TYPE) && domainList != null) && (userList != null
                    && !userList.isEmpty() && !(userVO.getCategoryCode().equalsIgnoreCase(PretupsI.CUSTOMER_CARE)))) {
                for (int i = 0; i < userList.size(); i++) {
                    final ListValueVO user = (ListValueVO) userList.get(i);
                    if (!domainList.contains(user.getIDValue())) {
                        userList.remove(i);
                    }
                }
            }
            if ((userList.isEmpty() || c2sEnquiryRequestVO.getStaffUserID().equals("%"))
                    && !PretupsI.OPERATOR_USER_TYPE.equals(UserId)) {
                throw new BTSLBaseException(this, methodName, "staffc2senquiry.viewc2stransfers.error.loginidnotexist",
                        showErrorTo);
            }
            final ArrayList c2sTransferVOList = new ArrayList();

            // now load the transaction list
            c2STransferwebDAO = new C2STransferWebDAO();
            if (true) {
                response.setC2sEnquiryDetails(c2STransferwebDAO.loadC2STransferVOList(con, networkCode, fromDate,
                        toDate, userList, receiverMsisdn, transferID, service, category_code));
                for (int i = 0; i < response.getC2sEnquiryDetails().size(); i++) {
                    C2STransferVO c2sTransferVO = new C2STransferVO();
                    c2sTransferVO = (C2STransferVO) response.getC2sEnquiryDetails().get(i);
                    c2sTransferVO.setActiveUserName(c2sTransferVO.getSenderName());
                    for (int k = 0; k < userList.size(); k++) {
                        final ListValueVO user = (ListValueVO) userList.get(k);
                        if (c2sTransferVO.getActiveUserId().equals(user.getValue())) {
                            c2sTransferVO.setActiveUserName(user.getOtherInfo());
                            break;
                        }
                    }
                    c2sTransferVOList.add(c2sTransferVO);
                }
                response.setC2sEnquiryDetails(c2sTransferVOList);
            }

        } catch (Exception e) {
            log.error(methodName, "Exception: e=" + e);
            log.errorTrace(methodName, e);
        } finally {

        }

    }
}
