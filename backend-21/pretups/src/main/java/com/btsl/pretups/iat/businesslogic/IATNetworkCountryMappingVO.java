package com.btsl.pretups.iat.businesslogic;

public class IATNetworkCountryMappingVO {

    private String _recCountryShortName;
    private String _recNetworkCode;
    private String _recNetworkName;
    private String _recNetworkPrefix;
    private String _status;
    private String _serialID;
    private String _networkCountryID;

    public String toString() {
        StringBuffer sbf = new StringBuffer("");
        sbf.append("[Receiver country short name :" + _recCountryShortName + "]");
        sbf.append("[_recNetworkCode :" + _recNetworkCode + "]");
        sbf.append("[_recNetworkName:" + _recNetworkName + "]");
        sbf.append("[_recNetworkPrefix :" + _recNetworkPrefix + "]");
        sbf.append("[_status :" + _status + "]");
        sbf.append("[_serialID :" + _serialID + "]");
        sbf.append("[_networkCountryID :" + _networkCountryID + "]");
        return sbf.toString();
    }

    /**
     * @return Returns the serialID.
     */
    public String getSerialID() {
        return _serialID;
    }

    /**
     * @param serialID
     *            The serialID to set.
     */
    public void setSerialID(String serialID) {
        _serialID = serialID;
    }

    public String getRecCountryShortName() {
        return _recCountryShortName;
    }

    public void setRecCountryShortName(String recCountryShortName) {
        _recCountryShortName = recCountryShortName;
    }

    public String getRecNetworkCode() {
        return _recNetworkCode;
    }

    public void setRecNetworkCode(String recNetworkCode) {
        _recNetworkCode = recNetworkCode;
    }

    public String getRecNetworkName() {
        return _recNetworkName;
    }

    public void setRecNetworkName(String recNetworkName) {
        _recNetworkName = recNetworkName;
    }

    public String getRecNetworkPrefix() {
        return _recNetworkPrefix;
    }

    public void setRecNetworkPrefix(String recNetworkPrefix) {
        _recNetworkPrefix = recNetworkPrefix;
    }

    /**
     * @return Returns the networkCountryID.
     */
    public String getNetworkCountryID() {
        return _networkCountryID;
    }

    /**
     * @param networkCountryID
     *            The networkCountryID to set.
     */
    public void setNetworkCountryID(String networkCountryID) {
        _networkCountryID = networkCountryID;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }
    
    public static IATNetworkCountryMappingVO getInstance(){
    	return new IATNetworkCountryMappingVO();
    }
}
