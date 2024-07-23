package com.inter.zteethopia;

import java.net.InetAddress;

public class ZTEINSocket {

    String _destinationIP = null;
    String _destinationPort = null;
    InetAddress _virtualIP = null;
    ZTEINSocketWrapper _zteINSocketWrapper = null;

    public String getDestinationIP() {
        return _destinationIP;
    }

    public void setDestinationIP(String _destinationip) {
        _destinationIP = _destinationip;
    }

    public String getDestinationPort() {
        return _destinationPort;
    }

    public void setDestinationPort(String port) {
        _destinationPort = port;
    }

    public InetAddress getVirtualIP() {
        return _virtualIP;
    }

    public void setVirtualIP(InetAddress _virtualip) {
        _virtualIP = _virtualip;
    }

    public ZTEINSocketWrapper getZteINSocketWrapper() {
        return _zteINSocketWrapper;
    }

    public void setZteINSocketWrapper(ZTEINSocketWrapper socketWrapper) {
        _zteINSocketWrapper = socketWrapper;
    }

    public String toString() {
        if (_zteINSocketWrapper == null) {
            return "DestinationIP=" + _destinationIP + " ,DestinationPort=" + _destinationPort + " ,VirtualIP" + _virtualIP + " ,_zteINSocketWrapper=null";
        } else {
            return "DestinationIP" + _destinationIP + " ,DestinationPort" + _destinationPort + " ,VirtualIP" + _virtualIP + " ,_zteINSocketWrapper" + _zteINSocketWrapper;
        }
    }

}// End of PAMIErrorStatus class
