package com.btsl.pretups.receiver;

import java.io.Serializable;

public class FixedInformationVO implements Serializable {

    private String _mcc = null; //
    private String _mnc = null; //
    private String _lac = null; //
    private String _cid = null; //
    // all the above will be in the first 7 bytes

    private String _language = null;
    private String _serviceId = null;
    private String _serviceVersion = "0.0";
    private String _position = null;
    private String _applicationVersion = null;
    private static final long serialVersionUID = 1L;
    public String getApplicationVersion() {
        return _applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        _applicationVersion = applicationVersion;
    }

    public String getCid() {
        return _cid;
    }

    public void setCid(String cid) {
        _cid = cid;
    }

    public String getLac() {
        return _lac;
    }

    public void setLac(String lac) {
        _lac = lac;
    }

    public String getLanguage() {
        return _language;
    }

    public void setLanguage(String language) {
        _language = language;
    }

    public String getMcc() {
        return _mcc;
    }

    public void setMcc(String mcc) {
        _mcc = mcc;
    }

    public String getMnc() {
        return _mnc;
    }

    public void setMnc(String mnc) {
        _mnc = mnc;
    }

    public String getPosition() {
        return _position;
    }

    public void setPosition(String position) {
        _position = position;
    }

    public String getServiceId() {
        return _serviceId;
    }

    public void setServiceId(String serviceId) {
        _serviceId = serviceId;
    }

    public String getServiceVersion() {
        return _serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        _serviceVersion = serviceVersion;
    }

}
