package com.restapi.channeluser.service;
import java.util.List;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserAssociateProfileResponseVO extends BaseResponse {
	

	 @JsonProperty("gradeList")
	    private List<GradeList> gradeList = null;
	    @JsonProperty("transferProfileList")
	    private List<TransferProfileList> transferProfileList = null;
	    
	    @JsonInclude(Include.NON_NULL)
	    @JsonProperty("transferRuleTypeList")
	    private List<TransferRuleTypeList> transferRuleTypeList = null;
	    
	    @JsonInclude(Include.NON_NULL)
	    @JsonProperty("lmsList")
	    private List<LMSList> lmsList = null;
	    

	    @JsonProperty("gradeList")
	    public List<GradeList> getGradeList() {
	        return gradeList;
	    }

	    @JsonProperty("gradeList")
	    public void setGradeList(List<GradeList> gradeList) {
	        this.gradeList = gradeList;
	    }

	    @JsonProperty("transferProfileList")
	    public List<TransferProfileList> getTransferProfileList() {
	        return transferProfileList;
	    }

	    @JsonProperty("transferProfileList")
	    public void setTransferProfileList(List<TransferProfileList> transferProfileList) {
	        this.transferProfileList = transferProfileList;
	    }

	    @JsonProperty("transferRuleTypeList")
	    public List<TransferRuleTypeList> getTransferRuleTypeList() {
	        return transferRuleTypeList;
	    }

	    @JsonProperty("transferRuleTypeList")
	    public void setTransferRuleTypeList(List<TransferRuleTypeList> transferRuleTypeList) {
	        this.transferRuleTypeList = transferRuleTypeList;
	    }
	    
	    @JsonProperty("lmsList")
	    public List<LMSList> getLmsList() {
	        return lmsList;
	    }

	    @JsonProperty("lmsList")
	    public void setLmsList(List<LMSList> lmsList) {
	        this.lmsList = lmsList;
	    }

}

class GradeList {

    @JsonProperty("gradecode")
    private String gradecode;
    @JsonProperty("gradename")
    private String gradename;
    @JsonProperty("commisionProfileList")
    private List<CommisionProfileList> commisionProfileList = null;
    

    @JsonProperty("gradecode")
    public String getGradecode() {
        return gradecode;
    }

    @JsonProperty("gradecode")
    public void setGradecode(String gradecode) {
        this.gradecode = gradecode;
    }

    @JsonProperty("gradename")
    public String getGradename() {
        return gradename;
    }

    @JsonProperty("gradename")
    public void setGradeName(String gradename) {
        this.gradename = gradename;
    }

    @JsonProperty("commisionProfileList")
    public List<CommisionProfileList> getCommisionProfileList() {
        return commisionProfileList;
    }

    @JsonProperty("commisionProfileList")
    public void setCommisionProfileList(List<CommisionProfileList> commisionProfileList) {
        this.commisionProfileList = commisionProfileList;
    }

    

}
class CommisionProfileList {

    @JsonProperty("commprofilecode")
    private String commprofilecode;
    @JsonProperty("commprofilename")
    private String commprofilename;
    
    @JsonProperty("commprofilecode")
    public String getCommprofileCode() {
        return commprofilecode;
    }

    @JsonProperty("commprofilecode")
    public void setCommprofileCode(String commprofilecode) {
        this.commprofilecode = commprofilecode;
    }

    @JsonProperty("commprofilename")
    public String getCommprofileName() {
        return commprofilename;
    }

    @JsonProperty("commprofilename")
    public void setCommprofileName(String commprofilename) {
        this.commprofilename = commprofilename;
    }

    

}
class TransferProfileList {

    @JsonProperty("transferprofilecode")
    private String transferprofilecode;
    @JsonProperty("transferprofilename")
    private String transferprofilename;
  

    @JsonProperty("transferprofilecode")
    public String getTransferprofileCode() {
        return transferprofilecode;
    }

    @JsonProperty("transferprofilecode")
    public void setTransferprofileCode(String transferprofilecode) {
        this.transferprofilecode = transferprofilecode;
    }

    @JsonProperty("transferprofilename")
    public String getTransferprofileName() {
        return transferprofilename;
    }

    @JsonProperty("transferprofilename")
    public void setTransferprofileName(String transferprofilename) {
        this.transferprofilename = transferprofilename;
    }

    

}
class TransferRuleTypeList {

    @JsonProperty("transferrulecode")
    private String transferrulecode;
    @JsonProperty("transferrulename")
    private String transferrulename;
 

    @JsonProperty("transferrulecode")
    public String getTransferruleCode() {
        return transferrulecode;
    }

    @JsonProperty("transferrulecode")
    public void setTransferruleCode(String transferrulecode) {
        this.transferrulecode = transferrulecode;
    }

    @JsonProperty("transferrulename")
    public String getTransferruleName() {
        return transferrulename;
    }

    @JsonProperty("transferrulename")
    public void setTransferruleName(String transferrulename) {
        this.transferrulename = transferrulename;
    }

    
}

class LMSList{


    @JsonProperty("lmsprofilecode")
    private String lmsprofilecode;
    @JsonProperty("lmsprofilename")
    private String lmsprofilename;
 

    @JsonProperty("lmsprofilecode")
    public String getLmsprofileCode() {
        return lmsprofilecode;
    }

    @JsonProperty("lmsprofilecode")
    public void setLmsprofileCode(String lmsprofilecode) {
        this.lmsprofilecode = lmsprofilecode;
    }

    @JsonProperty("lmsprofilename")
    public String getLmsprofileName() {
        return lmsprofilename;
    }

    @JsonProperty("lmsprofilename")
    public void setLmsprofileName(String lmsprofilename) {
        this.lmsprofilename = lmsprofilename;
    }

    

}
