package com.btsl.pretups.interfaces.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;

public class InterfaceNodeDetailsVO implements Serializable {

    private ArrayList _nodeStatusList;
    private String _port = null;
    private String _uri = null;
    private String _nodeStatus;
    private int rowIndex;
    private String _ip = null;
    private String _nodeName;
    private static final long serialVersionUID = 1L;
    public InterfaceNodeDetailsVO(InterfaceNodeDetailsVO interfaceNodeDetailsVO) {
        this._port = interfaceNodeDetailsVO._port;
        this._uri = interfaceNodeDetailsVO._uri;
        this._ip = interfaceNodeDetailsVO._ip;
        this._nodeStatus = interfaceNodeDetailsVO._nodeStatus;
        this.rowIndex = interfaceNodeDetailsVO.rowIndex;
    }

    public String getNodeName() {
        return _nodeName;
    }

    public void setNodeName(String _nodeName) {
        this._nodeName = _nodeName;
    }

    public InterfaceNodeDetailsVO() {

    }

    private String _uriReq;

    public ArrayList<String> getNodeStatusList() {
        return _nodeStatusList;
    }

    public void setNodeStatusList(ArrayList<String> _nodeStatusList) {
        this._nodeStatusList = _nodeStatusList;
    }

    public String getIp() {
        return _ip;
    }

    public String getUriReq() {
        return _uriReq;
    }

    public void setUriReq(String _uriReq) {
        this._uriReq = _uriReq;
    }

    public void setIp(String _ip) {
        this._ip = _ip;
    }

    public String getPort() {
        return _port;
    }

    public void setPort(String _port) {
        this._port = _port;
    }

    public String getUri() {
        return _uri;
    }

    public void setUri(String _uri) {
        this._uri = _uri;
    }

    public String getNodeStatus() {
        return _nodeStatus;
    }

    public void setNodeStatus(String _nodeStatus) {
        this._nodeStatus = _nodeStatus;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public String toString() {
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("\n IP Id=" + _ip);
        strBuff.append("\n  Port Number=" + _port);
        strBuff.append("\n _uri=" + _uri);
        strBuff.append("\n Node status=" + _nodeStatus);
        return strBuff.toString();
    }

}
