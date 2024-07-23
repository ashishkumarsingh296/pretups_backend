package com.btsl.ota.services.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * 
 * @(#)ServicesVO.java Copyright(c) 2003, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 *                     --------------------------------------------------------
 *                     ----------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     ----------
 *                     Gaurav Garg 19/12/03 Initial Creation
 *                     Gurjeet Singh Bedi 31/12/03 Modified
 *                     --------------------------------------------------------
 *                     ----------
 */

public class SimVO implements Serializable {
    protected java.lang.String _decodedData;
    protected int _lockTime;
    protected java.lang.String _locationCode;
    protected java.lang.String _transactionID;
    protected java.lang.String _userType;
    protected java.lang.String _userProfile;
    protected java.lang.String _userMsisdn;
    protected java.lang.String _createdBy;
    protected java.lang.String _modifiedBy;
    protected java.util.Date _createdOn;
    protected java.util.Date _modifedOn;
    protected java.lang.String _service1;
    protected java.lang.String _service2;
    protected java.lang.String _service3;
    protected java.lang.String _service4;
    protected java.lang.String _service5;
    protected java.lang.String _service6;
    protected java.lang.String _service7;
    protected java.lang.String _service8;
    protected java.lang.String _service9;
    protected java.lang.String _service10;
    protected java.lang.String _service11;
    protected java.lang.String _service12;
    protected java.lang.String _service13;
    protected java.lang.String _service14;
    protected java.lang.String _service15;
    protected java.lang.String _service16;
    protected java.lang.String _service17;
    protected java.lang.String _service18;
    protected java.lang.String _service19;
    protected java.lang.String _service20;
    protected java.lang.String _param1;
    protected java.lang.String _param2;
    protected java.lang.String _param3;
    protected java.lang.String _param4;
    protected java.lang.String _param5;
    protected java.lang.String _param6;
    protected java.lang.String _param7;
    protected java.lang.String _param8;
    protected java.lang.String _param9;
    protected java.lang.String _param10;
    protected java.lang.String _smsRef;
    protected java.lang.String _langRef;
    protected java.lang.String _simEnquiryRes;
    protected java.lang.String _response;
    protected java.lang.String _status;
    protected java.lang.String _operation;
    protected ArrayList _serviceList;

    /**
     * @return
     */
    public java.lang.String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @return
     */
    public java.util.Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @return
     */
    public java.lang.String getLocationCode() {
        return _locationCode;
    }

    /**
     * @return
     */
    public java.util.Date getModifedOn() {
        return _modifedOn;
    }

    /**
     * @return
     */
    public java.lang.String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @return
     */
    public java.lang.String getUserMsisdn() {
        return _userMsisdn;
    }

    /**
     * @return
     */
    public java.lang.String getUserProfile() {
        return _userProfile;
    }

    /**
     * @return
     */
    public java.lang.String getUserType() {
        return _userType;
    }

    /**
     * @param string
     */
    public void setCreatedBy(java.lang.String string) {
        _createdBy = string;
    }

    /**
     * @param date
     */
    public void setCreatedOn(java.util.Date date) {
        _createdOn = date;
    }

    /**
     * @param string
     */
    public void setLocationCode(java.lang.String string) {
        _locationCode = string;
    }

    /**
     * @param date
     */
    public void setModifedOn(java.util.Date date) {
        _modifedOn = date;
    }

    /**
     * @param string
     */
    public void setModifiedBy(java.lang.String string) {
        _modifiedBy = string;
    }

    /**
     * @param string
     */
    public void setUserMsisdn(java.lang.String string) {
        _userMsisdn = string;
    }

    /**
     * @param string
     */
    public void setUserProfile(java.lang.String string) {
        _userProfile = string;
    }

    /**
     * @param string
     */
    public void setUserType(java.lang.String string) {
        _userType = string;
    }

    /**
     * @return
     */
    public java.lang.String getLangRef() {
        return _langRef;
    }

    /**
     * @return
     */
    public java.lang.String getParam1() {
        return _param1;
    }

    public java.lang.String getParam1AsString() {
        if (_param1 == null) {
            return "";
        } else {
            return _param1;
        }
    }

    /**
     * @return
     */
    public java.lang.String getParam10() {
        return _param10;
    }

    public java.lang.String getParam10AsString() {
        if (_param10 == null) {
            return "";
        } else {
            return _param10;
        }
    }

    /**
     * @return
     */
    public java.lang.String getParam2() {
        return _param2;
    }

    public java.lang.String getParam2AsString() {
        if (_param2 == null) {
            return "";
        } else {
            return _param2;
        }
    }

    /**
     * @return
     */
    public java.lang.String getParam3() {
        return _param3;
    }

    public java.lang.String getParam3AsString() {
        if (_param3 == null) {
            return "";
        }
        return _param3;
    }

    /**
     * @return
     */
    public java.lang.String getParam4() {
        return _param4;
    }

    public java.lang.String getParam4AsString() {
        if (_param4 == null) {
            return "";
        }
        return _param4;
    }

    /**
     * @return
     */
    public java.lang.String getParam5() {
        return _param5;
    }

    public java.lang.String getParam5AsString() {
        if (_param5 == null) {
            return "";
        }
        return _param5;
    }

    /**
     * @return
     */
    public java.lang.String getParam6() {
        return _param6;
    }

    public java.lang.String getParam6AsString() {
        if (_param6 == null) {
            return "";
        }
        return _param6;
    }

    /**
     * @return
     */
    public java.lang.String getParam7() {
        return _param7;
    }

    public java.lang.String getParam7AsString() {
        if (_param7 == null) {
            return "";
        }
        return _param7;
    }

    /**
     * @return
     */
    public java.lang.String getParam8() {
        return _param8;
    }

    public java.lang.String getParam8AsString() {
        if (_param8 == null) {
            return "";
        }
        return _param8;
    }

    /**
     * @return
     */
    public java.lang.String getParam9() {
        return _param9;
    }

    public java.lang.String getParam9AsString() {
        if (_param9 == null) {
            return "";
        }
        return _param9;
    }

    /**
     * @return
     */
    public java.lang.String getService1() {
        return _service1;
    }

    /**
     * @return
     */
    public java.lang.String getService10() {
        return _service10;
    }

    /**
     * @return
     */
    public java.lang.String getService11() {
        return _service11;
    }

    /**
     * @return
     */
    public java.lang.String getService12() {
        return _service12;
    }

    /**
     * @return
     */
    public java.lang.String getService13() {
        return _service13;
    }

    /**
     * @return
     */
    public java.lang.String getService14() {
        return _service14;
    }

    /**
     * @return
     */
    public java.lang.String getService15() {
        return _service15;
    }

    /**
     * @return
     */
    public java.lang.String getService16() {
        return _service16;
    }

    /**
     * @return
     */
    public java.lang.String getService17() {
        return _service17;
    }

    /**
     * @return
     */
    public java.lang.String getService18() {
        return _service18;
    }

    /**
     * @return
     */
    public java.lang.String getService19() {
        return _service19;
    }

    /**
     * @return
     */
    public java.lang.String getService2() {
        return _service2;
    }

    /**
     * @return
     */
    public java.lang.String getService20() {
        return _service20;
    }

    /**
     * @return
     */
    public java.lang.String getService3() {
        return _service3;
    }

    /**
     * @return
     */
    public java.lang.String getService4() {
        return _service4;
    }

    /**
     * @return
     */
    public java.lang.String getService5() {
        return _service5;
    }

    /**
     * @return
     */
    public java.lang.String getService6() {
        return _service6;
    }

    /**
     * @return
     */
    public java.lang.String getService7() {
        return _service7;
    }

    /**
     * @return
     */
    public java.lang.String getService8() {
        return _service8;
    }

    /**
     * @return
     */
    public java.lang.String getService9() {
        return _service9;
    }

    /**
     * @return
     */
    public java.lang.String getSimEnquiryRes() {
        return _simEnquiryRes;
    }

    /**
     * @return
     */
    public java.lang.String getSmsRef() {
        return _smsRef;
    }

    /**
     * @return
     */
    public java.lang.String getTransactionID() {
        return _transactionID;
    }

    /**
     * @param string
     */
    public void setLangRef(java.lang.String string) {
        _langRef = string;
    }

    /**
     * @param string
     */
    public void setParam1(java.lang.String string) {
        _param1 = string;
    }

    /**
     * @param string
     */
    public void setParam10(java.lang.String string) {
        _param10 = string;
    }

    /**
     * @param string
     */
    public void setParam2(java.lang.String string) {
        _param2 = string;
    }

    /**
     * @param string
     */
    public void setParam3(java.lang.String string) {
        _param3 = string;
    }

    /**
     * @param string
     */
    public void setParam4(java.lang.String string) {
        _param4 = string;
    }

    /**
     * @param string
     */
    public void setParam5(java.lang.String string) {
        _param5 = string;
    }

    /**
     * @param string
     */
    public void setParam6(java.lang.String string) {
        _param6 = string;
    }

    /**
     * @param string
     */
    public void setParam7(java.lang.String string) {
        _param7 = string;
    }

    /**
     * @param string
     */
    public void setParam8(java.lang.String string) {
        _param8 = string;
    }

    /**
     * @param string
     */
    public void setParam9(java.lang.String string) {
        _param9 = string;
    }

    /**
     * @param string
     */
    public void setService1(java.lang.String string) {
        _service1 = string;
    }

    /**
     * @param string
     */
    public void setService10(java.lang.String string) {
        _service10 = string;
    }

    /**
     * @param string
     */
    public void setService11(java.lang.String string) {
        _service11 = string;
    }

    /**
     * @param string
     */
    public void setService12(java.lang.String string) {
        _service12 = string;
    }

    /**
     * @param string
     */
    public void setService13(java.lang.String string) {
        _service13 = string;
    }

    /**
     * @param string
     */
    public void setService14(java.lang.String string) {
        _service14 = string;
    }

    /**
     * @param string
     */
    public void setService15(java.lang.String string) {
        _service15 = string;
    }

    /**
     * @param string
     */
    public void setService16(java.lang.String string) {
        _service16 = string;
    }

    /**
     * @param string
     */
    public void setService17(java.lang.String string) {
        _service17 = string;
    }

    /**
     * @param string
     */
    public void setService18(java.lang.String string) {
        _service18 = string;
    }

    /**
     * @param string
     */
    public void setService19(java.lang.String string) {
        _service19 = string;
    }

    /**
     * @param string
     */
    public void setService2(java.lang.String string) {
        _service2 = string;
    }

    /**
     * @param string
     */
    public void setService20(java.lang.String string) {
        _service20 = string;
    }

    /**
     * @param string
     */
    public void setService3(java.lang.String string) {
        _service3 = string;
    }

    /**
     * @param string
     */
    public void setService4(java.lang.String string) {
        _service4 = string;
    }

    /**
     * @param string
     */
    public void setService5(java.lang.String string) {
        _service5 = string;
    }

    /**
     * @param string
     */
    public void setService6(java.lang.String string) {
        _service6 = string;
    }

    /**
     * @param string
     */
    public void setService7(java.lang.String string) {
        _service7 = string;
    }

    /**
     * @param string
     */
    public void setService8(java.lang.String string) {
        _service8 = string;
    }

    /**
     * @param string
     */
    public void setService9(java.lang.String string) {
        _service9 = string;
    }

    /**
     * @param string
     */
    public void setSimEnquiryRes(java.lang.String string) {
        _simEnquiryRes = string;
    }

    /**
     * @param string
     */
    public void setSmsRef(java.lang.String string) {
        _smsRef = string;
    }

    /**
     * @param string
     */
    public void setTransactionID(java.lang.String string) {
        _transactionID = string;
    }

    /**
     * @return
     */
    public java.lang.String getResponse() {
        return _response;
    }

    /**
     * @param string
     */
    public void setResponse(java.lang.String string) {
        _response = string;
    }

    /**
     * @return
     */
    public java.lang.String getStatus() {
        return _status;
    }

    /**
     * @param string
     */
    public void setStatus(java.lang.String string) {
        _status = string;
    }

    /**
     * @return
     */
    public java.lang.String getDecodedData() {
        return _decodedData;
    }

    /**
     * @param string
     */
    public void setDecodedData(java.lang.String string) {
        _decodedData = string;
    }

    /**
     * @return
     */
    public java.lang.String getOperation() {
        return _operation;
    }

    /**
     * @param string
     */
    public void setOperation(java.lang.String string) {
        _operation = string;
    }

    /**
     * @return
     */
    public int getLockTime() {
        return _lockTime;
    }

    /**
     * @param string
     */
    public void setLockTime(int string) {
        _lockTime = string;
    }

    /**
     * @return
     */
    public ArrayList getServiceList() {
        return _serviceList;
    }

    /**
     * @param list
     */
    public void setServiceList(ArrayList list) {
        _serviceList = list;
    }

}
