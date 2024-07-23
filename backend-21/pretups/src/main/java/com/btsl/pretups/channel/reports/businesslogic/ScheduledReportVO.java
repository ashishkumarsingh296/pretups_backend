package com.btsl.pretups.channel.reports.businesslogic;

/*
 * @# ScheduledReportVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * mar 27, 2005 Ved.sharma Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
public class ScheduledReportVO {
    private String _reportCode;
    private String _reportName;
    private String _module;
    private String _rptName;
    private String _url;
    private String _generatedFileName;

    /**
     * @return Returns the generatedFileName.
     */
    public String getGeneratedFileName() {
        return _generatedFileName;
    }

    /**
     * @param generatedFileName
     *            The generatedFileName to set.
     */
    public void setGeneratedFileName(String generatedFileName) {
        _generatedFileName = generatedFileName;
    }

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
     * @return Returns the rptName.
     */
    public String getRptName() {
        return _rptName;
    }

    /**
     * @param rptName
     *            The rptName to set.
     */
    public void setRptName(String rptName) {
        _rptName = rptName;
    }

    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return _url;
    }

    /**
     * @param url
     *            The url to set.
     */
    public void setUrl(String url) {
        _url = url;
    }
}
