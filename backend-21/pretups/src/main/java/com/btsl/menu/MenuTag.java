package com.btsl.menu;

/*
 * MenuTag.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.util.ArrayList;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class MenuTag extends jakarta.servlet.jsp.tagext.BodyTagSupport {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private java.util.ArrayList _menuList = null;// ArrayList for storing menu
                                                 // objects
    private int _posCurrentElement = 0;// int value to store current position in
                                       // the collection from where the next
                                       // element will be retrived
    private int _testCounter = 0;// for testing
    private int _sizeOfArray = 0;// int value to store the size of the array
    private boolean _checkCondition = false;// boolean variable to find out if
                                            // the checkCondition nethod should
                                            // be called or not
    private java.lang.String _level = null;// String variable to store the level
                                           // of the menu we want to retrieve
    private java.lang.String _parentLevel = null;// String variable to store the
                                                 // parentlevel of the menu we
                                                 // want to retrieve
    private java.lang.String _moduleCode = null;// String variable to store the
                                                // moduleCode of the menu we
                                                // want to retrieve
    private String _stringForOut = null;// String variable to store value to be
                                        // written back to the user screen
    private boolean _writeOut = false;// boolean variable to store the current
                                      // value to be written to the usre screen
                                      // or not
    private ArrayList _tempList = null;

    /**
     * Default MenuTag constructor .
     */
    public MenuTag() {
        super();
        // System.out.println("MenuTag constructor _posCurrentElement="+_posCurrentElement);
        _posCurrentElement = 0;
    }

    /**
     * This method checks if the next element can be retrived from the
     * collection or not
     * Creation date: (04-Apr-02 1:30:22 PM)
     * 
     * @return boolean
     */
    private boolean checkCondition() {
        // System.out.println("MenuTag checkCondition() Entered _posCurrentElement="+_posCurrentElement+"       _sizeOfArray="+_sizeOfArray);
        return _posCurrentElement < _sizeOfArray ? true : false;
    }

    /**
     * This method is called after every body of the tag
     * returns EVEL_BODY_TAG if the collection is not null else
     * returns SKIP_BODY
     * Creation date: (04-Apr-02 12:21:26 PM)
     */
    public int doAfterBody() throws JspException { // System.out.println("MenuTag doAfterBody() entered");
        if (checkCondition()) {
            if (findNextElement()) {
                return EVAL_BODY_TAG;
            } else {
                return SKIP_BODY;
            }
        }
        return SKIP_BODY;
    }

    /**
     * This method is called once for the tag at end of the tag
     * returns EVEL_BODY_TAG if the collection is not null else
     * return SKIP_BODY and writes the contents of the body
     */
    public int doEndTag() throws JspException {
        final String METHOD_NAME = "doEndTag";
        try {
            _tempList = null;
            if (bodyContent != null) {
                if (_writeOut) {
                    if (bodyContent.getString() != null)
                        _stringForOut += bodyContent.getString().trim();
                }
                bodyContent.clearBody();
                bodyContent.write(_stringForOut);
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
                _stringForOut = null;
            }
            // System.out.println("MenuTag doEndTag()  _stringForOut="+_stringForOut);
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            System.err.println("Exception in MenuTag-->doEndTag   " + ex);
        }

        if (checkCondition()) {
            return EVAL_BODY_TAG;
        }
        return SKIP_BODY;
    }

    /**
     * This method is called once for the tag at start of the tag
     * returns EVEL_BODY_TAG if the collection is not null else
     * returns SKIP_BODY
     */
    public int doStartTag() throws JspException {
        // System.out.println("MenuTag doStartTag() entered");
        final String METHOD_NAME = "doStartTag";
        _posCurrentElement = 0;
        _testCounter = 0;
        _stringForOut = null;
        _writeOut = false;
        _tempList = new ArrayList();
        try {
            // System.out.println("MenuTag doStartTag() entered _menuList="+_menuList);
            if (_menuList != null) {
                _stringForOut = "";
                _sizeOfArray = _menuList.size();
                // System.out.println("MenuTag doStartTag() calling findNextElement_menuList size="+_menuList.size());
                findNextElement();
                return EVAL_BODY_TAG;
            } else {
                return SKIP_BODY;
            }
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            return SKIP_BODY;
        }
    }

    /**
     * This method returns true if next matching element can be found from the
     * collection
     * else returns false
     * Creation date: (09-Apr-02 2:46:16 PM)
     */
    public boolean findNextElement() {
        // System.out.println("MenuTag findNextElement() entered");
        boolean getNext = true;
        MenuItem menu = null;
        // System.out.println("MenuTag findNextElement() _writeOut="+_writeOut+"  bodyContent="+bodyContent);
        if (_writeOut && (bodyContent != null)) {
            if (bodyContent.getString() != null) {
                _stringForOut += (bodyContent.getString()).trim();
                bodyContent.clearBody();
            }
        }
        // System.out.println("MenuTag findNextElement() getNext="+getNext+"   checkCondition()="+checkCondition());

        while ((getNext) && (checkCondition())) {
            menu = (MenuItem) _menuList.get(_posCurrentElement);
            // System.out.println("MenuTag findNextElement() _level="+_level+"      menu="+menu+"  _parentLevel="+_parentLevel+"      _moduleCode="+_moduleCode+" _tempList size="+_tempList.size());
            if (_moduleCode == null)
                _moduleCode = "";
            if (_parentLevel == null)
                _parentLevel = "";
            if (_level == null) {
                _writeOut = false;
                _posCurrentElement++;
            } else if ((menu != null && menu.isMenuItem()) && (_level.equals("1")) && _level.equals(menu.getLevel()) && !_tempList.contains(menu.getModuleCode().trim())) {
                getNext = false;
                _writeOut = true;
                // System.out.println("MenuTag findNextElement()  setting pageContext menuName="+menu.getMenuName()+"  menuUrl="+menu.getUrl()+"   moduleCode="+menu.getModuleCode()+"         PageContext.PAGE_SCOPE="+PageContext.PAGE_SCOPE);
                _tempList.add(menu.getModuleCode().trim());
                pageContext.setAttribute("menuName", menu.getMenuName(), PageContext.PAGE_SCOPE);
                pageContext.setAttribute("menuUrl", menu.getUrl(), PageContext.PAGE_SCOPE);
                pageContext.setAttribute("moduleCode", menu.getModuleCode(), PageContext.PAGE_SCOPE);
                pageContext.setAttribute("menuObj", menu, PageContext.PAGE_SCOPE);
                _posCurrentElement++;
                return true;
            } else if (menu != null && menu.isMenuItem() && (_level.equals(menu.getLevel())) && (_parentLevel.equals(menu.getParentLevel())) && (_moduleCode.equals(menu.getModuleCode())) && ((_level.equals(menu.getLevel())))) {
                getNext = false;
                _writeOut = true;
                // System.out.println("MenuTag findNextElement()  setting pageContext menuName="+menu.getMenuName()+"  menuUrl="+menu.getUrl()+"   moduleCode="+menu.getModuleCode()+"       PageContext.PAGE_SCOPE="+PageContext.PAGE_SCOPE);
                pageContext.setAttribute("menuName", menu.getMenuName(), PageContext.PAGE_SCOPE);
                pageContext.setAttribute("menuUrl", menu.getUrl(), PageContext.PAGE_SCOPE);
                pageContext.setAttribute("menuObj", menu, PageContext.PAGE_SCOPE);
                if (menu.getModuleCode() != null)
                    pageContext.setAttribute("moduleCode", menu.getModuleCode(), PageContext.PAGE_SCOPE);
                _posCurrentElement++;
                // System.out.println("_testCounter	="+(++_testCounter));
                return true;
            } else {
                _writeOut = false;
                _posCurrentElement++;
            }
        }
        return false;
    }

    /**
     * This method returns the level of the menu
     * Creation date: (04-Apr-02 5:14:31 PM)
     * 
     * @return java.lang.String
     */
    public java.lang.String getLevel() {
        return _level;
    }

    /**
     * This method returns the collection of the menu objects
     * Creation date: (04-Apr-02 12:03:04 PM)
     * 
     * @return java.util.ArrayList
     */
    public java.util.ArrayList getMenuList() {
        return  _menuList;
    }

    /**
     * This method returns the parent level of the menu
     * Creation date: (04-Apr-02 5:15:00 PM)
     * 
     * @return java.lang.String
     */
    public java.lang.String getParentLevel() {
        return _parentLevel;
    }

    /**
     * This method returns the current position from the collection of the menu
     * objects
     * Creation date: (04-Apr-02 12:58:18 PM)
     * 
     * @return int
     */
    public int getPosCurrentElement() {
        return _posCurrentElement;
    }

    /**
     * This method returns the size of the menu array
     * Creation date: (04-Apr-02 12:58:18 PM)
     * 
     * @return int
     */
    public int getSizeOfArray() {
        return _sizeOfArray;
    }

    /**
     * This method set the check condition i.e the condition is to be checked or
     * not
     * Creation date: (04-Apr-02 1:30:22 PM)
     * 
     * @param newCheckCondition
     *            boolean
     */
    private void setCheckCondition(boolean newCheckCondition) {
        _checkCondition = newCheckCondition;
    }

    /**
     * This method set the level of the menu
     * Creation date: (04-Apr-02 5:14:31 PM)
     * 
     * @param newFirstLevel
     *            java.lang.String
     */
    public void setLevel(java.lang.String newLevel) {
        _level = newLevel;
    }

    /**
     * This method set the menu Collection
     * Creation date: (04-Apr-02 12:03:04 PM)
     * 
     * @param newMenuList
     *            java.util.ArrayList
     */
    public void setMenuList(java.util.ArrayList newMenuList) {
        this._menuList = newMenuList;
    }

    /**
     * This method set the parent level of the menu
     * Creation date: (04-Apr-02 5:15:00 PM)
     * 
     * @param newModuleCode
     *            java.lang.String
     */
    public void setParentLevel(java.lang.String newParentLevel) {
        _parentLevel = newParentLevel;
    }

    /**
     * This method set the current position in the Collection
     * Creation date: (04-Apr-02 12:58:18 PM)
     * 
     * @param newPosCurrentElement
     *            int
     */
    public void setPosCurrentElement(int newPosCurrentElement) {
        _posCurrentElement = newPosCurrentElement;
    }

    /**
     * This method set the size of the menu Collection
     * Creation date: (04-Apr-02 12:58:18 PM)
     * 
     * @param newSizeOfArray
     *            int
     */
    public void setSizeOfArray(int newSizeOfArray) {
        _sizeOfArray = newSizeOfArray;
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
}
