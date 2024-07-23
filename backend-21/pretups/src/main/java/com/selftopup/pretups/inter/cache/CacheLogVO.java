/**
 * @(#)CacheLogVO.java
 *                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 * 
 *                     <description>
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     avinash.kamthan June 27, 2005 Initital Creation
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 * 
 */

package com.selftopup.pretups.inter.cache;

import java.io.Serializable;

/**
 * @author avinash.kamthan
 * 
 */
public class CacheLogVO implements Serializable {
    private String cacheID;
    private String type;
    private String oldValue;
    private String newValue;
    private String actionType;

    private String dispalyValue;

    public String getDispalyValue() {
        return dispalyValue;
    }

    public void setDispalyValue(String dispalyValue) {
        this.dispalyValue = dispalyValue;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getCacheID() {
        return cacheID;
    }

    public void setCacheID(String interfaceID) {
        this.cacheID = interfaceID;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String propertyKey) {
        this.type = propertyKey;
    }

}
