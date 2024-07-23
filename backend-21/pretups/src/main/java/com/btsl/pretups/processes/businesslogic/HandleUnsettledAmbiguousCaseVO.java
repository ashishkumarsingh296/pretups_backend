package com.btsl.pretups.processes.businesslogic;

public class HandleUnsettledAmbiguousCaseVO {

    private String _transctionID;
    private String _status;
    private String _recordCount;
    private String _otherInfo;

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("transctionID  =" + _transctionID);
        sbf.append(",_status =" + _status);
        sbf.append(",_recordCount =" + _recordCount);
        sbf.append(",_otherInfo =" + _otherInfo);
        return sbf.toString();
    }

    public String get_transctionID() {
        return _transctionID;
    }

    public void setTransctionID(String _transctionid) {
        _transctionID = _transctionid;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String _status) {
        this._status = _status;
    }

    public String getRecordCount() {
        return _recordCount;
    }

    public void setRecordCount(String count) {
        _recordCount = count;
    }

    public String getOtherInfo() {
        return _otherInfo;
    }

    public void setOtherInfo(String info) {
        _otherInfo = info;
    }

}
