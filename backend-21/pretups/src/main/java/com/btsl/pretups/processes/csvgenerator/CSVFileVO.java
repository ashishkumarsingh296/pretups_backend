package com.btsl.pretups.processes.csvgenerator;

/**
 * @(#)NodeVO
 *            * Copyright(c) 2010, Comviva technologies LTD.
 *            All Rights Reserved
 *            ------------------------------------------------------------------
 *            -------------------------------
 *            Author Date History
 *            ------------------------------------------------------------------
 *            -------------------------------
 *            Shishupal Singh 22-Jun-2011 Initial Creation
 *            ------------------------------------------------------------------
 *            ------------------------------
 * 
 */
public class CSVFileVO {

    private String _processId = null;
    private String _queryName = null;
    private String _dirName = null;
    private String _extName = null;
    private String _headerName = null;
    private String _prefixName = null;
    private String _tempTable = null;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(", _processId=" + _processId);
        sb.append(", _queryName=" + _queryName);
        sb.append(", _dirName=" + _dirName);
        sb.append(", _extName=" + _extName);
        sb.append(", _headerName=" + _headerName);
        sb.append(", _prefixName=" + _prefixName);
        return sb.toString();
    }

    public String getProcessId() {
        return _processId;
    }

    public void setProcessId(String _processId) {
        this._processId = _processId;
    }

    public String getQueryName() {
        return _queryName;
    }

    public void setQueryName(String _queryName) {
        this._queryName = _queryName;
    }

    public String getDirName() {
        return _dirName;
    }

    public void setDirName(String _dirName) {
        this._dirName = _dirName;
    }

    public String getHeaderName() {
        return _headerName;
    }

    public void setHeaderName(String _headerName) {
        this._headerName = _headerName;
    }

    public String getPrefixName() {
        return _prefixName;
    }

    public void setPrefixName(String _prefixName) {
        this._prefixName = _prefixName;
    }

    public String getExtName() {
        return _extName;
    }

    public void setExtName(String _extName) {
        this._extName = _extName;
    }

    public String getTempTable() {
        return _tempTable;
    }

    public void setTempTable(String tempTable) {
        this._tempTable = tempTable;
    }
}