package com.btsl.user.businesslogic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class RuleRoleCode {
    private String module;
    private List<RuleRole> ruleRole = new ArrayList<RuleRole>();
    public String getModule() {
        return module;
    }
    public void setModule(String module) {
        this.module = module;
    }
    public List<RuleRole> getRuleRole() {
        return ruleRole;
    }
    public void setRuleRole(List<RuleRole> ruleRole) {
        this.ruleRole = ruleRole;
    }
}
