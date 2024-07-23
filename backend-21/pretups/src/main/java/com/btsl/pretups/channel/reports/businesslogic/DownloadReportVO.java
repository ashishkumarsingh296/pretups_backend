/*
 * #DownloadReportVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * oct 10, 2008 Rajdeep Deb Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.channel.reports.businesslogic;

import java.io.Serializable;

/**
 * @author rajdeep.deb
 */
public class DownloadReportVO implements Serializable {
    private String _reportCode;
    private String _reportName;
    private String _type;
    private String _status;
    private String _pathKey;
    private String _prefix;
    private String _dateFormat;
    private String _path; // Directory name of each report
    private String[] _statusFlag;
    private String _module;

    /**
     * @return Returns the module.
     */
    public String getModule() {
        return _module;
    }

    /**
     * @param module
     *            The module to set.
     */
    public void setModule(String module) {
        _module = module;
    }

    /**
     * @return Returns the prefix.
     */
    public String getPrefix() {
        return _prefix;
    }

    /**
     * @param prefix
     *            The _prefix to set.
     */
    public void setPrefix(String prefix) {
        _prefix = prefix;
    }

    /**
     * @return Returns the prefix.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The _status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the reportCode.
     */
    public String getReportCode() {
        return _reportCode;
    }

    /**
     * @param reportCode
     *            The reportCode to set.
     */
    public void setReportCode(String reportCode) {
        _reportCode = reportCode;
    }

    /**
     * @return Returns the reportName.
     */
    public String getReportName() {
        return _reportName;
    }

    /**
     * @param reportName
     *            The reportName to set.
     */
    public void setReportName(String reportName) {
        _reportName = reportName;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * @return Returns the pathKey.
     */
    public String getPathKey() {
        return _pathKey;
    }

    /**
     * @param pathKey
     *            The _pathKey to set.
     */
    public void setPathKey(String pathKey) {
        _pathKey = pathKey;
    }

    /**
     * @return Returns the _statusCheckbox.
     */
    public String[] getStatusFlag() {
        return _statusFlag;
    }

    /**
     * @param path
     *            The _statusCheckbox to set.
     */
    public void setStatusCheckbox(String[] statusFlag) {
        _statusFlag = statusFlag;
    }

    /**
     * @return Returns the path.
     */
    public String getPath() {
        return _path;
    }

    /**
     * @param path
     *            The _path to set.
     */
    public void setPath(String path) {
        _path = path;
    }

    /**
     * @return Returns the dateFormat.
     */
    public String getDateFormat() {
        return _dateFormat;
    }

    /**
     * @param dateFormat
     *            The _dateFormat to set.
     */
    public void setDateFormat(String dateFormat) {
        _dateFormat = dateFormat;
    }

}
