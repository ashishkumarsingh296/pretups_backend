package com.reporting.extent.core;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.commons.MasterI;
import com.utils._masterVO;

public class ExtentManager {

    public static ExtentReports getInstance(String extentReportPath) {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(extentReportPath);
        htmlReporter.config().setDocumentTitle("[PreTUPS]" + _masterVO.getMasterValue(MasterI.CLIENT_NAME) + "_" + _masterVO.getMasterValue(MasterI.APPLICATION_VERSION) + " Automation Report");
        htmlReporter.config().setReportName("[PreTUPS]" + _masterVO.getMasterValue(MasterI.CLIENT_NAME) + "_" + _masterVO.getMasterValue(MasterI.APPLICATION_VERSION) + " Automation Report");
        htmlReporter.config().setChartVisibilityOnOpen(false);
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("Host Name", _masterVO.getMasterValue(MasterI.WEB_URL));
        extent.setSystemInfo("Client", _masterVO.getMasterValue(MasterI.CLIENT_NAME));
        extent.setSystemInfo("Application Version", _masterVO.getMasterValue(MasterI.APPLICATION_VERSION));

        return extent;
    }
}
