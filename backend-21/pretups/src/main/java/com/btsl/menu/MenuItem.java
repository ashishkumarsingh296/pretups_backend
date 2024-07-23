package com.btsl.menu;

/*
 * Menu.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
public class MenuItem implements java.io.Serializable {
    private boolean _menuItem = false; // boolean variable to store if menu
                                       // object is menu item is not
    private java.lang.String _menuName = null;// String value to store menu name
    private java.lang.String _url = null;// String value to store url
    private java.lang.String _parentLevel = null;// String value to store parent
                                                 // level
    private java.lang.String _level = null;// String value to store level
    private java.lang.String _pageCode = null;
    private java.lang.String _moduleCode = null;
    private java.lang.String _type = null;
    private String _fromTimeStr;
    private String _toTimeStr;
    private String _roleCode;

    /**
     * default Menu constructor
     */
    public MenuItem() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param p_menuItem
     *            boolean
     * @param p_menuName
     *            java.lang.String
     * @param p_url
     *            java.lang.String
     * @param p_parentLevel
     *            java.lang.String
     * @param p_level
     *            java.lang.String
     * @param p_accessType
     *            java.lang.String
     * @param p_code
     *            java.lang.String
     * @param p_parentCode
     *            java.lang.String
     * @param p_type
     *            java.lang.String
     */
    public MenuItem(boolean p_menuItem, String p_menuName, String p_url, String p_parentLevel, String p_level, String p_pageCode, String p_moduleCode, String p_parentCode, String p_type) {
        _menuItem = p_menuItem;
        _menuName = p_menuName;
        _url = p_url;
        _parentLevel = p_parentLevel;
        _level = p_level;
        _pageCode = p_pageCode;
        _moduleCode = p_moduleCode;
        _type = p_type;
    }

    /**
     * @return Returns the fromTimeStr.
     */
    public String getFromTimeStr() {
        return _fromTimeStr;
    }

    /**
     * @param fromTimeStr
     *            The fromTimeStr to set.
     */
    public void setFromTimeStr(String fromTimeStr) {
        _fromTimeStr = fromTimeStr;
    }

    /**
     * @return Returns the level.
     */
    public java.lang.String getLevel() {
        return _level;
    }

    /**
     * @param level
     *            The level to set.
     */
    public void setLevel(java.lang.String level) {
        _level = level;
    }

    /**
     * @return Returns the menuItem.
     */
    public boolean isMenuItem() {
        return _menuItem;
    }

    /**
     * @param menuItem
     *            The menuItem to set.
     */
    public void setMenuItem(boolean menuItem) {
        _menuItem = menuItem;
    }

    /**
     * @return Returns the menuName.
     */
    public java.lang.String getMenuName() {
        return _menuName;
    }

    /**
     * @param menuName
     *            The menuName to set.
     */
    public void setMenuName(java.lang.String menuName) {
        _menuName = menuName;
    }

    /**
     * @return Returns the parentLevel.
     */
    public java.lang.String getParentLevel() {
        return _parentLevel;
    }

    /**
     * @param parentLevel
     *            The parentLevel to set.
     */
    public void setParentLevel(java.lang.String parentLevel) {
        _parentLevel = parentLevel;
    }

    /**
     * @return Returns the toTimeStr.
     */
    public String getToTimeStr() {
        return _toTimeStr;
    }

    /**
     * @param toTimeStr
     *            The toTimeStr to set.
     */
    public void setToTimeStr(String toTimeStr) {
        _toTimeStr = toTimeStr;
    }

    /**
     * @return Returns the type.
     */
    public java.lang.String getType() {
        return _type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(java.lang.String type) {
        _type = type;
    }

    /**
     * @return Returns the url.
     */
    public java.lang.String getUrl() {
        return _url;
    }

    /**
     * @param url
     *            The url to set.
     */
    public void setUrl(java.lang.String url) {
        _url = url;
    }

    /**
     * @return Returns the pageCode.
     */
    public java.lang.String getPageCode() {
        return _pageCode;
    }

    /**
     * @param pageCode
     *            The pageCode to set.
     */
    public void setPageCode(java.lang.String pageCode) {
        _pageCode = pageCode;
    }

    /**
     * @return Returns the moduleCode.
     */
    public java.lang.String getModuleCode() {
        return _moduleCode;
    }

    /**
     * @param moduleCode
     *            The moduleCode to set.
     */
    public void setModuleCode(java.lang.String moduleCode) {
        _moduleCode = moduleCode;
    }

    public String getRoleCode() {
        return _roleCode;
    }

    public void setRoleCode(String roleCode) {
        _roleCode = roleCode;
    }
}
