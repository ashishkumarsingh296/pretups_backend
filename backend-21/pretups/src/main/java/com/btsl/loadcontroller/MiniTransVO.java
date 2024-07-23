package com.btsl.loadcontroller;

import java.io.Serializable;

/*
 * MiniTransVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 18/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to store details of a transaction in memory
 */

public class MiniTransVO implements Serializable {

    public MiniTransVO() {
    }

    public MiniTransVO(String p_senderInterfaceService, long p_createdTime, boolean p_isOverflow, String p_senderOriginalService) {
        _senderInterfaceService = p_senderInterfaceService;
        _creationTime = p_createdTime;
        _senderOriginalService = p_senderOriginalService;
        _isOverflow = p_isOverflow;
    }

    private String _reciverInterfaceService = null;
    private String _senderInterfaceService = null;
    private String _senderOriginalService = null;
    private String _reciverOriginalService = null;
    private boolean _isOverflow = false;
    private boolean _isRecieverOverflow = false;
    private long _creationTime = 0;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Sender Interface Service=" + _senderInterfaceService + ",");
        sb.append("Sender using Overflow=" + _isOverflow + ",");
        sb.append("Sender Original Service=" + _senderOriginalService + ",");
        sb.append("Reciver Interface Service=" + _reciverInterfaceService + ",");
        sb.append("Reciever using Overflow=" + _isRecieverOverflow + ",");
        sb.append("Reciver Original Service=" + _reciverOriginalService + ",");
        sb.append("Creation Time=" + _creationTime);
        return sb.toString();
    }

    public long getCreationTime() {
        return _creationTime;
    }

    public void setCreationTime(long creationTime) {
        _creationTime = creationTime;
    }

    public String getReciverInterfaceService() {
        return _reciverInterfaceService;
    }

    public void setReciverInterfaceService(String reciverInterfaceService) {
        _reciverInterfaceService = reciverInterfaceService;
    }

    public String getSenderInterfaceService() {
        return _senderInterfaceService;
    }

    public void setSenderInterfaceService(String senderInterfaceService) {
        _senderInterfaceService = senderInterfaceService;
    }

    public boolean isOverflow() {
        return _isOverflow;
    }

    public void setOverflow(boolean isOverflow) {
        _isOverflow = isOverflow;
    }

    public boolean isRecieverOverflow() {
        return _isRecieverOverflow;
    }

    public void setRecieverOverflow(boolean isRecieverOverflow) {
        _isRecieverOverflow = isRecieverOverflow;
    }

    public String getReciverOriginalService() {
        return _reciverOriginalService;
    }

    public void setReciverOriginalService(String reciverOriginalService) {
        _reciverOriginalService = reciverOriginalService;
    }

    public String getSenderOriginalService() {
        return _senderOriginalService;
    }

    public void setSenderOriginalService(String senderOriginalService) {
        _senderOriginalService = senderOriginalService;
    }

}
