package com.btsl.user.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page {
    private String id;
    private String ngIf;
    private String name;
    private String path;
    private String activeClass;
    private String image;
    private String _class;
    private List<SubPage> subPages = new ArrayList<SubPage>();
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNgIf() {
        return ngIf;
    }
    public void setNgIf(String ngIf) {
        this.ngIf = ngIf;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getActiveClass() {
        return activeClass;
    }
    public void setActiveClass(String activeClass) {
        this.activeClass = activeClass;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getClass_() {
        return _class;
    }
    public void setClass_(String _class) {
        this._class = _class;
    }
    public List<SubPage> getSubPages() {
        return subPages;
    }
    public void setSubPages(List<SubPage> subPages) {
        this.subPages = subPages;
    }
}