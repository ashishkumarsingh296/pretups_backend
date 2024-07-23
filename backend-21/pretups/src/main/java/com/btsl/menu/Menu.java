package com.btsl.menu;

import java.io.Serializable;

public class Menu implements Serializable {

    public Menu() {
        _menuItem = false;
        _menuName = null;
        _url = null;
        _parentLevel = null;
        _level = null;
        _accessType = null;
        _code = null;
        _parentCode = null;
        _type = null;
    }

    public String getAccessType() {
        return _accessType;
    }

    public String getCode() {
        return _code;
    }

    public String getLevel() {
        return _level;
    }

    public String getMenuName() {
        return _menuName;
    }

    public String getParentCode() {
        return _parentCode;
    }

    public String getParentLevel() {
        return _parentLevel;
    }

    public String getType() {
        return _type;
    }

    public String getUrl() {
        return _url;
    }

    public boolean isMenuItem() {
        return _menuItem;
    }

    public void setAccessType(String newAccessType) {
        _accessType = newAccessType;
    }

    public void setCode(String newCode) {
        _code = newCode;
    }

    public void setLevel(String newLevel) {
        _level = newLevel;
    }

    public void setMenuItem(boolean newMenuItem) {
        _menuItem = newMenuItem;
    }

    public void setMenuName(String newMenuName) {
        _menuName = newMenuName;
    }

    public void setParentCode(String newParentCode) {
        _parentCode = newParentCode;
    }

    public void setParentLevel(String newParentLevel) {
        _parentLevel = newParentLevel;
    }

    public void setType(String new_type) {
        _type = new_type;
    }

    public void setUrl(String newUrl) {
        _url = newUrl;
    }

    public Menu(boolean p_menuItem, String p_menuName, String p_url, String p_parentLevel, String p_level, String p_accessType, String p_code, String p_parentCode, String p_type) {
        _menuItem = false;
        _menuName = null;
        _url = null;
        _parentLevel = null;
        _level = null;
        _accessType = null;
        _code = null;
        _parentCode = null;
        _type = null;
        _menuItem = p_menuItem;
        _menuName = p_menuName;
        _url = p_url;
        _parentLevel = p_parentLevel;
        _level = p_level;
        _accessType = p_accessType;
        _code = p_code;
        _parentCode = p_parentCode;
        _type = p_type;
    }

    private boolean _menuItem;
    private String _menuName;
    private String _url;
    private String _parentLevel;
    private String _level;
    private String _accessType;
    private String _code;
    private String _parentCode;
    private String _type;
}
