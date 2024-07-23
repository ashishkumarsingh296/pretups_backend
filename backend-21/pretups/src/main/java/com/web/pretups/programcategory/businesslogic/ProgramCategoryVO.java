package com.web.pretups.programcategory.businesslogic;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.btsl.common.ListValueVO;

@Component
@Scope(value = "request")
// @Scope("session")
public class ProgramCategoryVO {
    private String programType;
    private List<ListValueVO> programList;
    private List<ListValueVO> programEarningTypeList;
    private String minPeriod;
    private String maxPeriod;
    private boolean pointsForParent = false;
    private String programEarningType;
    private boolean autoRedempAll = false;
    private List<ListValueVO> rewardTypeList;
    private String rewardType;
    private List<ListValueVO> redempFreqList;
    private String redempFrequency;
    private List<String> redempWalletList;
    private String redempWalletType;
    private String ruleListExist;
    private String propertyName;
    private String propertyMessage;

    private List<String> programEarningSelList;
    private List<String> rewardTypeSelList;
    private List<String> redempFreqSelList;
    private List<String> redempWalletSelList;

    public List<String> getProgramEarningSelList() {
        return programEarningSelList;
    }

    public void setProgramEarningSelList(List<String> programEarningSelList) {
        this.programEarningSelList = programEarningSelList;
    }

    public List<String> getRewardTypeSelList() {
        return rewardTypeSelList;
    }

    public void setRewardTypeSelList(List<String> rewardTypeSelList) {
        this.rewardTypeSelList = rewardTypeSelList;
    }

    public List<String> getRedempFreqSelList() {
        return redempFreqSelList;
    }

    public void setRedempFreqSelList(List<String> redempFreqSelList) {
        this.redempFreqSelList = redempFreqSelList;
    }

    public List<String> getRedempWalletSelList() {
        return redempWalletSelList;
    }

    public void setRedempWalletSelList(List<String> redempWalletSelList) {
        this.redempWalletSelList = redempWalletSelList;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyMessage() {
        return propertyMessage;
    }

    public void setPropertyMessage(String propertyMessage) {
        this.propertyMessage = propertyMessage;
    }

    public String getRuleListExist() {
        return ruleListExist;
    }

    public void setRuleListExist(String ruleListExist) {
        this.ruleListExist = ruleListExist;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public List<ListValueVO> getProgramList() {
        return programList;
    }

    public void setProgramList(List<ListValueVO> programList) {
        this.programList = programList;
    }

    public List<ListValueVO> getProgramEarningTypeList() {
        return programEarningTypeList;
    }

    public void setProgramEarningTypeList(List<ListValueVO> programEarningTypeList) {
        this.programEarningTypeList = programEarningTypeList;
    }

    public String getMinPeriod() {
        return minPeriod;
    }

    public void setMinPeriod(String minPeriod) {
        this.minPeriod = minPeriod;
    }

    public String getMaxPeriod() {
        return maxPeriod;
    }

    public void setMaxPeriod(String maxPeriod) {
        this.maxPeriod = maxPeriod;
    }

    public boolean isPointsForParent() {
        return pointsForParent;
    }

    public void setPointsForParent(boolean pointsForParent) {
        this.pointsForParent = pointsForParent;
    }

    public String getProgramEarningType() {
        return programEarningType;
    }

    public void setProgramEarningType(String programEarningType) {
        this.programEarningType = programEarningType;
    }

    public boolean isAutoRedempAll() {
        return autoRedempAll;
    }

    public void setAutoRedempAll(boolean autoRedempAll) {
        this.autoRedempAll = autoRedempAll;
    }

    public List<ListValueVO> getRewardTypeList() {
        return rewardTypeList;
    }

    public void setRewardTypeList(List<ListValueVO> rewardTypeList) {
        this.rewardTypeList = rewardTypeList;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public List<ListValueVO> getRedempFreqList() {
        return redempFreqList;
    }

    public void setRedempFreqList(List<ListValueVO> redempFreqList) {
        this.redempFreqList = redempFreqList;
    }

    public String getRedempFrequency() {
        return redempFrequency;
    }

    public void setRedempFrequency(String redempFrequency) {
        this.redempFrequency = redempFrequency;
    }

    public List<String> getRedempWalletList() {
        return redempWalletList;
    }

    public void setRedempWalletList(List<String> redempWalletList) {
        this.redempWalletList = redempWalletList;
    }

    public String getRedempWalletType() {
        return redempWalletType;
    }

    public void setRedempWalletType(String redempWalletType) {
        this.redempWalletType = redempWalletType;
    }

}
