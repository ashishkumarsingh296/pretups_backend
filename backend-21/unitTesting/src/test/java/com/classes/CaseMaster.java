package com.classes;

public class CaseMaster {

    private int dataRow;
    private String moduleCode;
    private String description;
    private String errorCode;
    private String testCaseCode;
    private String testCategory;
    private String jiraID;

    public String getJiraID() {
        return jiraID;
    }

    public void setJiraID(String jiraID) {
        this.jiraID = jiraID;
    }

    public int getDataRow() {
        return dataRow;
    }

    public void setDataRow(int dataRow) {
        this.dataRow = dataRow;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getTestCaseCode() {
        return testCaseCode;
    }

    public void setTestCaseCode(String testCaseCode) {
        this.testCaseCode = testCaseCode;
    }

    public String getExtentCase() {
        return testCaseCode + ": " + description;
    }

    public String getTestCategory() {
        return testCategory;
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }

}
