package com.btsl.user.businesslogic;

import java.io.Serializable;

public class RoleHitTimeVO implements Serializable {

    private String _roleCode;
    private long _totalHits = 0;
    private long _totalUnderProcess = 0;
    private long _maxUnderProcess = 0;
    private long _totalTime = 0;
    private long _lastAccessTime = 0;
@Override
    public String toString() {
    	StringBuilder sbf = new StringBuilder();
    	sbf.append("_roleCode:").append(_roleCode);
    	sbf.append(" _totalHits:").append(_totalHits);
    	sbf.append(" _totalUnderProcess:").append(_totalUnderProcess);
    	sbf.append(" _totalTime:").append(_totalTime);
    	sbf.append(" _lastAccessTime:").append(_lastAccessTime);
    	
        return sbf.toString();
    }

    public RoleHitTimeVO(String p_roleCode, long p_totalHits, long p_totalUnderProcess) {
        super();

        _roleCode = p_roleCode;
        _totalHits = p_totalHits;
        if (_maxUnderProcess < _totalUnderProcess) {
            _maxUnderProcess = _totalUnderProcess;
        }
        _totalUnderProcess = p_totalUnderProcess;
    }

    public RoleHitTimeVO(String p_roleCode, long p_totalHits, long p_totalTime, long p_lastAccessTime) {
        super();
     
        _roleCode = p_roleCode;
        _totalHits = p_totalHits;
        _totalTime = p_totalTime;
        _lastAccessTime = p_lastAccessTime;
    }

    public long getLastAccessTime() {
        return _lastAccessTime;
    }

    public void setLastAccessTime(long _lastAccessTime) {
        this._lastAccessTime = _lastAccessTime;
    }

    public String getRoleCode() {
        return _roleCode;
    }

    public void setRoleCode(String _roleCode) {
        this._roleCode = _roleCode;
    }

    public long getTotalHits() {
        return _totalHits;
    }

    public void setTotalHits(long _totalHits) {
        this._totalHits = _totalHits;
    }

    public long getTotalTime() {
        return _totalTime;
    }

    public void setTotalTime(long _totalTime) {
        this._totalTime = _totalTime;
    }

    public long getTotalUnderProcess() {
        return _totalUnderProcess;
    }

    public void setTotalUnderProcess(long totalUnderProcess) {
        _totalUnderProcess = totalUnderProcess;
        if (_maxUnderProcess < _totalUnderProcess) {
            _maxUnderProcess = _totalUnderProcess;
        }
    }

    public long getMaxUnderProcess() {
        return _maxUnderProcess;
    }

    public void setMaxUnderProcess(long maxUnderProcess) {
        _maxUnderProcess = maxUnderProcess;
    }

}
