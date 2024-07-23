package com.btsl.pretups.iat.enquiry.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

/*import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorActionForm;*/

import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

public class IATTransferEnquiryForm/* extends ValidatorActionForm*/ {

    private String _fromDate;
    private String _toDate;
    private String _transferID;
    private String _senderMsisdn;
    private String _serviceType;
    private ArrayList _c2sTransferVOList = null;
    private ArrayList _c2sTransferItemsVOList = null;
    private ArrayList _serviceTypeList = null;
    private String _tmpTransferID = null;
    private C2STransferVO transferVO = null;
    private String _currentDateFlag = null;
    private int _radioCheckStatus = 0;
    private int _disableRadioButton = 0;
    private ArrayList _transferStatusList = null;

    public IATTransferEnquiryForm() {
        super();
    }

    public int getTransferVOListSize() {
        if (_c2sTransferVOList != null) {
            return _c2sTransferVOList.size();
        }
        return 0;
    }

    public int getTransferItemVOListSize() {
        if (_c2sTransferItemsVOList != null) {
            return _c2sTransferItemsVOList.size();
        }
        return 0;
    }

    public void flush() {
        _fromDate = null;
        _toDate = null;
        _transferID = null;
        _senderMsisdn = null;
        _serviceType = null;
        _c2sTransferVOList = null;
        _c2sTransferItemsVOList = null;
        _serviceTypeList = null;
        _tmpTransferID = null;
        transferVO = null;
        _currentDateFlag = null;
        _radioCheckStatus = 0;
        _disableRadioButton = 0;

    }

    public ArrayList getC2sTransferItemsVOList() {
        return _c2sTransferItemsVOList;
    }

    public ArrayList getC2sTransferVOList() {
        return _c2sTransferVOList;
    }

    public String getCurrentDateFlag() {
        return _currentDateFlag;
    }

    public String getFromDate() {
        return _fromDate;
    }

    public String getSenderMsisdn() {
        return _senderMsisdn;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public ArrayList getServiceTypeList() {
        return _serviceTypeList;
    }

    public String getTmpTransferID() {
        return _tmpTransferID;
    }

    public String getToDate() {
        return _toDate;
    }

    public String getTransferID() {
        return _transferID;
    }

    public C2STransferVO getTransferVO() {
        return transferVO;
    }

    public void setC2sTransferItemsVOList(ArrayList transferItemsVOList) {
        _c2sTransferItemsVOList = transferItemsVOList;
    }

    public void setC2sTransferVOList(ArrayList transferVOList) {
        _c2sTransferVOList = transferVOList;
    }

    public void setCurrentDateFlag(String currentDateFlag) {
        _currentDateFlag = currentDateFlag;
    }

    public void setFromDate(String fromDate) {
        _fromDate = fromDate;
    }

    public void setSenderMsisdn(String senderMsisdn) {
        _senderMsisdn = senderMsisdn;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public void setServiceTypeList(ArrayList serviceTypeList) {
        _serviceTypeList = serviceTypeList;
    }

    public void setTmpTransferID(String tmpTransferID) {
        _tmpTransferID = tmpTransferID;
    }

    public void setToDate(String toDate) {
        _toDate = toDate;
    }

    public void setTransferID(String transferID) {
        _transferID = transferID;
    }

    public void setTransferVO(C2STransferVO transferVO) {
        this.transferVO = transferVO;
    }

    public int getRadioCheckStatus() {
        return _radioCheckStatus;
    }

    public void setRadioCheckStatus(int radioCheckStatus) {
        _radioCheckStatus = radioCheckStatus;
    }

    /*public void reset(ActionMapping mapping, HttpServletRequest request) {
        if (request.getParameter("submit") != null) {
            _currentDateFlag = PretupsI.RESET_CHECKBOX;

        }

        if (request.getParameter("btnBack") != null) {
            _radioCheckStatus = 0;
            _disableRadioButton = 0;
        }
    }*/

    public int getDisableRadioButton() {
        return _disableRadioButton;
    }

    public void setDisableRadioButton(int disableRadioButton) {
        _disableRadioButton = disableRadioButton;
    }

    public ArrayList getTransferStatusList() {
        return _transferStatusList;
    }

    public void setTransferStatusList(ArrayList transferStatusList) {
        _transferStatusList = transferStatusList;
    }

}
