/*
 * @# MessageGatewayVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 7, 2005 Sandeep Goel Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.common;

import java.io.Serializable;

public class CacheVO implements Serializable {

    private String _cacheCode;
    private String _cacheName;
    private String _cacheKey;
    private String _status;
    public CacheVO() {
    }
    public String getCacheCode() {
        return _cacheCode;
    }
    public void setCacheCode(String cacheCode) {
        _cacheCode = cacheCode;
    }
    public String getCacheName() {
        return _cacheName;
    }
    public void setCacheName(String cacheName) {
        _cacheName = cacheName;
    }
    public String getCacheKey() {
        return _cacheKey;
    }
    public void setCacheKey(String cacheKey) {
        _cacheKey = cacheKey;
    }
    public String getStatus() {
        return _status;
    }
    public void setStatus(String status) {
        _status = status;
    }
    @Override
    public String toString() {
        StringBuilder sbf = new StringBuilder();
        sbf.append("_cacheCode=" + _cacheCode);
        sbf.append(",_cacheName=" + _cacheName);
        sbf.append(",_cacheKey=" + _cacheKey);
        return sbf.toString();
    }
}
