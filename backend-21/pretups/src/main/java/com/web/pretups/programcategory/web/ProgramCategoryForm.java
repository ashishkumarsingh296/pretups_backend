package com.web.pretups.programcategory.web;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.btsl.common.ListValueVO;

@Component
@Scope(value = "session")
public class ProgramCategoryForm {

    private String programType;
    private List<ListValueVO> programList;
    private List<ListValueVO> programEarningTypeList;
    private String minPeriod = null;
    private String maxPeriod = null;
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

    public String getRuleListExist() {
        return ruleListExist;
    }

    public void setRuleListExist(String ruleListExist) {
        this.ruleListExist = ruleListExist;
    }

    public String getRedempWalletType() {
        return redempWalletType;
    }

    public void setRedempWalletType(String redempWalletType) {
        this.redempWalletType = redempWalletType;
    }

    public List<String> getRedempWalletList() {
        return redempWalletList;
    }

    public void setRedempWalletList(List<String> redempWalletList) {
        this.redempWalletList = redempWalletList;
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

    public boolean isPointsForParent() {
        return pointsForParent;
    }

    public void setPointsForParent(boolean pointsForParent) {
        this.pointsForParent = pointsForParent;
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

    public String getProgramEarningType() {
        return programEarningType;
    }

    public void setProgramEarningType(String programEarningType) {
        this.programEarningType = programEarningType;
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

    public List<ListValueVO> getProgramList() {
        return programList;
    }

    public void setProgramList(List<ListValueVO> programList) {
        this.programList = programList;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public List<ListValueVO> getProgramEarningTypeList() {
        return programEarningTypeList;
    }

    public void setProgramEarningTypeList(List<ListValueVO> programEarningTypeList) {
        this.programEarningTypeList = programEarningTypeList;
    }

}
