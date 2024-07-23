package com.btsl.user.businesslogic;

import java.util.HashMap;
import java.util.Map;
public class SubPage {
    private String id;
    private String ngIf;
    private String name;
    private String routerLink;
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
    public String getRouterLink() {
        return routerLink;
    }
    public void setRouterLink(String routerLink) {
        this.routerLink = routerLink;
    }
}