package com.selftopup.menu;

/*
 * MenuTEI.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

/**
 * This class is used for storing extra info about the menu tag
 * Creation date: (05-Apr-02 11:18:33 AM)
 * 
 * @author: Suraj Chawla
 */
public class MenuTEI extends jakarta.servlet.jsp.tagext.TagExtraInfo {
    /**
     * default MenuTagEntraInfo constructor
     */
    public MenuTEI() {
        super();
    }

    /**
     * This method sets the variable info
     * Creation date: (05-Apr-02 12:18:11 PM)
     * 
     * @return jakarta.servlet.jsp.tagext.VariableInfo[]
     * @param param
     *            jakarta.servlet.jsp.tagext.TagData
     */
    public jakarta.servlet.jsp.tagext.VariableInfo[] getVariableInfo(jakarta.servlet.jsp.tagext.TagData data) {

        jakarta.servlet.jsp.tagext.VariableInfo menuName = new jakarta.servlet.jsp.tagext.VariableInfo("menuName", "String", true, jakarta.servlet.jsp.tagext.VariableInfo.NESTED);
        jakarta.servlet.jsp.tagext.VariableInfo menuUrl = new jakarta.servlet.jsp.tagext.VariableInfo("menuUrl", "String", true, jakarta.servlet.jsp.tagext.VariableInfo.NESTED);
        jakarta.servlet.jsp.tagext.VariableInfo parentCode = new jakarta.servlet.jsp.tagext.VariableInfo("moduleCode", "String", true, jakarta.servlet.jsp.tagext.VariableInfo.NESTED);
        jakarta.servlet.jsp.tagext.VariableInfo menuObj = new jakarta.servlet.jsp.tagext.VariableInfo("menuObj", "com.selftopup.menu.MenuItem", true, jakarta.servlet.jsp.tagext.VariableInfo.NESTED);
        jakarta.servlet.jsp.tagext.VariableInfo[] info = { menuName, menuUrl, parentCode, menuObj };
        return info;

    }
}
