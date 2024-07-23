package com.inter.vodafoneghana.voucherconsumptiongh;

import netscape.ldap.util.ConnectionPool;

public class VoucherConsumptionGHSocket {

    String _destinationIP = null;
    int _destinationPort = 0;
    String _authId = null;
    int _version = 2;
    String _authPassword = null;
    int _minPoolSize = 0;
    int _maxPoolSize = 0;
    int _connectTimeOut = 0;
    ConnectionPool _connectionPool = null;

    public String getAuthId() {
        return _authId;
    }

    public void setAuthId(String id) {
        _authId = id;
    }

    public String getAuthPassword() {
        return _authPassword;
    }

    public void setAuthPassword(String password) {
        _authPassword = password;
    }

    public int getConnectTimeOut() {
        return _connectTimeOut;
    }

    public void setConnectTimeOut(int timeOut) {
        _connectTimeOut = timeOut;
    }

    public int getMaxPoolSize() {
        return _maxPoolSize;
    }

    public void setMaxPoolSize(int poolSize) {
        _maxPoolSize = poolSize;
    }

    public int getMinPoolSize() {
        return _minPoolSize;
    }

    public void setMinPoolSize(int poolSize) {
        _minPoolSize = poolSize;
    }

    public String getDestinationIP() {
        return _destinationIP;
    }

    public void setDestinationIP(String _destinationip) {
        _destinationIP = _destinationip;
    }

    public int getDestinationPort() {
        return _destinationPort;
    }

    public void setDestinationPort(int port) {
        _destinationPort = port;
    }

    public int getVersion() {
        return _version;
    }

    public void setVersion(int _version) {
        this._version = _version;
    }

    public ConnectionPool getConnectionPool() {
        return _connectionPool;
    }

    public void setConnectionPool(ConnectionPool pool) {
        _connectionPool = pool;
    }

}
