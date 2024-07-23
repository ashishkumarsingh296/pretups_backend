package com.btsl.event;

public class EventVO {
    private int _eventID = 0;
    private String _componentName = null;
    private String _eventStatus = null;
    private int _level = 0;
    private String _processName = null;
    private String _requestID = null;
    private String _msisdn = null;
    private String _networkCode = null;
    private String _message = null;
    private long _generatedOn = 0;
    private int _alarmCount = 0;

    public EventVO(int p_eventID, String p_componentName, String p_eventStatus, int p_level, String p_processName, String p_requestID, String p_msisdn, String p_networkCode, String p_message) {
        _eventID = p_eventID;
        _componentName = p_componentName;
        _eventStatus = p_eventStatus;
        _level = p_level;
        _processName = p_processName;
        _requestID = p_requestID;
        _msisdn = p_msisdn;
        _networkCode = p_networkCode;
        _message = p_message;
        _generatedOn = System.currentTimeMillis();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
    	StringBuilder sbf = new StringBuilder();
    	sbf.append(_eventID).append("|");
    	sbf.append(_componentName).append("|");
    	sbf.append(_eventStatus).append("|");
    	sbf.append(_level).append("|");
    	sbf.append(_processName).append("|");
    	sbf.append(_requestID).append("|");
    	sbf.append(_msisdn ).append("|");
    	sbf.append(_networkCode).append("|");
    	sbf.append(_message).append("|");
    	sbf.append(_generatedOn);
    	
    	
    	
    	return sbf.toString();
    }

    public int getAlarmCount() {
        return _alarmCount;
    }

    public void setAlarmCount(int alarmCount) {
        _alarmCount = alarmCount;
    }

    public String getComponentName() {
        return _componentName;
    }

    public void setComponentName(String componentName) {
        _componentName = componentName;
    }

    public int getEventID() {
        return _eventID;
    }

    public void setEventID(int eventID) {
        _eventID = eventID;
    }

    public String getEventStatus() {
        return _eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        _eventStatus = eventStatus;
    }

    public long getGeneratedOn() {
        return _generatedOn;
    }

    public void setGeneratedOn(long generatedOn) {
        _generatedOn = generatedOn;
    }

    public int getLevel() {
        return _level;
    }

    public void setLevel(int level) {
        _level = level;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getProcessName() {
        return _processName;
    }

    public void setProcessName(String processName) {
        _processName = processName;
    }

    public String getRequestID() {
        return _requestID;
    }

    public void setRequestID(String requestID) {
        _requestID = requestID;
    }

}
