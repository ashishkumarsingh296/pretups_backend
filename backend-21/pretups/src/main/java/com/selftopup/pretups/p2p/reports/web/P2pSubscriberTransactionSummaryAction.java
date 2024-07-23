package com.selftopup.pretups.p2p.reports.web;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import jakarta.servlet.http.HttpSession;
import com.selftopup.common.BTSLActionSupport;
import com.selftopup.common.ListValueVO;
import com.selftopup.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.master.businesslogic.LookupsCache;
import com.selftopup.pretups.p2p.reports.businesslogic.P2pSubscriberTransactionVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;
import com.opensymphony.xwork2.interceptor.ScopedModelDriven;

public class P2pSubscriberTransactionSummaryAction extends BTSLActionSupport implements ScopedModelDriven<P2pSubscriberTransactionVO> {

    private static final long serialVersionUID = 1L;
    public Log _log = LogFactory.getLog(this.getClass().getName());

    private P2pSubscriberTransactionVO p2pSubscriberVO;
    private String MODEL_SESSION_KEY;

    public P2pSubscriberTransactionVO getModel() {
        // TODO Auto-generated method stub
        return p2pSubscriberVO;
    }

    public String getScopeKey() {
        // TODO Auto-generated method stub
        return MODEL_SESSION_KEY;
    }

    public void setModel(P2pSubscriberTransactionVO arg0) {
        // TODO Auto-generated method stub
        this.p2pSubscriberVO = (P2pSubscriberTransactionVO) arg0;
    }

    public void setScopeKey(String arg0) {
        // TODO Auto-generated method stub
        MODEL_SESSION_KEY = arg0;
    }

    public P2pSubscriberTransactionVO getP2pSubscriberVO() {
        return p2pSubscriberVO;
    }

    public void setP2pSubscriberVO(P2pSubscriberTransactionVO p2pSubscriberVO) {
        this.p2pSubscriberVO = p2pSubscriberVO;
    }

    public static OperatorUtilI calculatorI = null;
    static {
        String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyMgtAction", "", "", "", "Exception while loading the operator util class at the addBuddyInfo:" + e.getMessage());
        }
    }

    public String loadDetails() {

        if (_log.isDebugEnabled())
            _log.debug("loadDetails", "Entered");

        String returnStr = null;
        ServicesTypeDAO servicesTypeDAO = null;
        Connection con = null;
        ArrayList list = new ArrayList();
        HttpSession session = _request.getSession();
        P2pSubscriberTransactionVO p2pSubscriberVO = this.p2pSubscriberVO;
        ListValueVO vo = null;

        try {
            p2pSubscriberVO.flush();
            con = OracleUtil.getConnection();
            p2pSubscriberVO.setSubstypeList(LookupsCache.loadLookupDropDown(PretupsI.SUBSCRIBER_TYPE, true));

            servicesTypeDAO = new ServicesTypeDAO();
            p2pSubscriberVO.setServiceTypeList(servicesTypeDAO.loadServicesListForReconciliation(con, PretupsI.P2P_MODULE));
            if (p2pSubscriberVO.getSizeOfServiceTypeList() == 1) {
                vo = (ListValueVO) p2pSubscriberVO.getServiceTypeList().get(0);
                p2pSubscriberVO.setServiceType(vo.getValue());
                p2pSubscriberVO.setServiceTypeName(vo.getLabel());
            }

            String status = "'" + SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS + "','" + SelfTopUpErrorCodesI.TXN_STATUS_FAIL + "','" + SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS + "'";

            list.add(new ListValueVO(this.getText("list.all"), PretupsI.ALL));

            list.addAll(new ChannelUserReportDAO().loadKeyValuesList(con, false, PretupsI.KEY_VALUE_P2P_STATUS, status));
            p2pSubscriberVO.setTransferStatusList(list);

            returnStr = "loadDetails";
        } catch (Exception e) {
            _log.error("loadDetails", "Exception e=" + e);
            e.printStackTrace();

        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.error("loadDetails", "Exiting : forward" + returnStr);
        }

        return returnStr;
    }

    public String p2pSubscriberTransactionSummary() {

        if (_log.isDebugEnabled())
            _log.debug("p2pSubscriberTransactionSummary", "Entered");

        String returnStr = null;
        P2pSubscriberTransactionVO p2pSubscriberVO = this.p2pSubscriberVO;
        HttpSession session = _request.getSession();

        try {

            p2pSubscriberVO.setSubstypeList(LookupsCache.loadLookupDropDown(PretupsI.SUBSCRIBER_TYPE, true));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            Date fromDate = formatter.parse((p2pSubscriberVO.getFromdate().split("T"))[0]);
            Date toDate = formatter.parse((p2pSubscriberVO.getTodate().split("T"))[0]);

            String fromDateStr = BTSLUtil.getDateStringFromDate(fromDate);
            String toDateStr = BTSLUtil.getDateStringFromDate(toDate);

            fromDate = BTSLUtil.getDateFromDateString(fromDateStr);
            toDate = BTSLUtil.getDateFromDateString(toDateStr);

            String fromdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(fromDate));
            String todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(toDate));

            p2pSubscriberVO.setRptfromdate(BTSLUtil.reportDateFormat(fromdate));
            System.out.println(BTSLUtil.reportDateFormat(fromdate));
            p2pSubscriberVO.setRpttodate(BTSLUtil.reportDateFormat(todate));

            CP2PSubscriberVO subsVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
            p2pSubscriberVO.setNetworkCode(subsVO.getNetworkCode());
            p2pSubscriberVO.setNetworkName(subsVO.getNetworkName());
            p2pSubscriberVO.setReportHeaderName(subsVO.getReportHeaderName());

            ListValueVO listValueVO = null;
            if (!BTSLUtil.isNullString(p2pSubscriberVO.getServiceType()) && p2pSubscriberVO.getServiceType().equals(PretupsI.ALL))
                p2pSubscriberVO.setServiceTypeName(this.getText("list.all"));
            else if (!BTSLUtil.isNullString(p2pSubscriberVO.getServiceType())) {
                listValueVO = (ListValueVO) BTSLUtil.getOptionDesc(p2pSubscriberVO.getServiceType(), p2pSubscriberVO.getServiceTypeList());
                p2pSubscriberVO.setServiceTypeName(listValueVO.getLabel());
            }
            if (!BTSLUtil.isNullString(p2pSubscriberVO.getTransferStatus()) && p2pSubscriberVO.getTransferStatus().equals(PretupsI.ALL))
                p2pSubscriberVO.setTransferStatusName(this.getText("list.all"));
            else if (!BTSLUtil.isNullString(p2pSubscriberVO.getTransferStatus())) {
                listValueVO = BTSLUtil.getOptionDesc(p2pSubscriberVO.getTransferStatus(), p2pSubscriberVO.getTransferStatusList());
                p2pSubscriberVO.setTransferStatusName(listValueVO.getLabel());
            }
            session.setAttribute("p2pSubscriberVO", p2pSubscriberVO);
            returnStr = SUCCESS;
        } catch (Exception e) {
            _log.error("p2pSubscriberTransactionSummary", "Exception e=" + e);
            e.printStackTrace();

        }
        if (_log.isDebugEnabled())
            _log.debug("p2pSubscriberTransactionSummary", "Exiting:forward=" + returnStr);

        return returnStr;
    }

    public void validate() {

        P2pSubscriberTransactionVO p2pSubscriberVO = this.p2pSubscriberVO;

        if (_request.getServletPath().equals("/p2preports/p2preports_p2pSubscriberTransactionSummary.action")) {

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                Date fromDate = formatter.parse((p2pSubscriberVO.getFromdate().split("T"))[0]);
                Date toDate = formatter.parse((p2pSubscriberVO.getTodate().split("T"))[0]);

                String fromDateStr = BTSLUtil.getDateStringFromDate(fromDate);
                String toDateStr = BTSLUtil.getDateStringFromDate(toDate);

                fromDate = BTSLUtil.getDateFromDateString(fromDateStr);
                toDate = BTSLUtil.getDateFromDateString(toDateStr);

                Date currentDate = Calendar.getInstance().getTime();

                if ((currentDate.compareTo(toDate)) < 0 || (currentDate.compareTo(fromDate) < 0)) {
                    this.addActionError(this.getText("p2p.transaction.report.past.date"));
                    return;

                }

                if (BTSLUtil.getDifferenceInUtilDates(fromDate, toDate) > 30) {
                    this.addActionError(this.getText("p2p.transaction.report.days"));
                }
            } catch (ParseException e) {

                e.printStackTrace();
            }

        }
    }

    public boolean isValidTodate() {

        boolean flag = false;
        P2pSubscriberTransactionVO p2pSubscriberVO = this.p2pSubscriberVO;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            Date fromDate = formatter.parse((p2pSubscriberVO.getFromdate().split("T"))[0]);
            Date toDate = formatter.parse((p2pSubscriberVO.getTodate().split("T"))[0]);

            String fromDateStr = BTSLUtil.getDateStringFromDate(fromDate);
            String toDateStr = BTSLUtil.getDateStringFromDate(toDate);

            fromDate = BTSLUtil.getDateFromDateString(fromDateStr);
            toDate = BTSLUtil.getDateFromDateString(toDateStr);

            Date currentDate = Calendar.getInstance().getTime();

            if ((currentDate.compareTo(toDate)) < 0 || (currentDate.compareTo(fromDate) < 0)) {

                flag = true;
            }

            System.out.println(currentDate);
            System.out.println(toDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flag;
    }

}
