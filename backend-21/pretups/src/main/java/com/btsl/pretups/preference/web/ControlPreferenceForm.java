package com.btsl.pretups.preference.web;

import jakarta.servlet.http.HttpServletRequest;

/*import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;*/

/*
 * ControlPreferenceForm.java
 * Name Date History
 * ------------------------------------------------------------------------
 * ved.sharma Feb 14, 2007 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */
public class ControlPreferenceForm extends PreferenceForm {
    private String _preferenceDescription;

    public int getControlListSize() {
        if (this.getControlList() != null) {
            return this.getControlList().size();
        } else {
            return 0;
        }
    }

    public int getModuleListSize() {
        if (this.getModuleList() != null) {
            return this.getModuleList().size();
        } else {
            return 0;
        }
    }

    /**
     * Method flush.
     * This method is used to reset all the fiels of the formBean.
     */
    public void flush() {
        super.flush();
        _preferenceDescription = null;

    }

    public String getPreferenceDescription() {
        return _preferenceDescription;
    }

    public void setPreferenceDescription(String preferenceDescription) {
        _preferenceDescription = preferenceDescription;
    }

    public int getPreferenceTypeListSize() {
        if (super.getPreferenceTypeList() != null) {
            return super.getPreferenceTypeList().size();
        }
        return 0;
    }

}
