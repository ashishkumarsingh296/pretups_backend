package com.inter.righttel.zte;

import java.net.InetAddress;


public class ZTEINSocket {


	String _destinationIP=null;
	String _destinationPort=null;

	ZTEINSocketWrapper _zteINSocketWrapper=null;
	
	String _sessionId=null;
	
	
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
	
	public ZTEINSocketWrapper getZteINSocketWrapper() {
		return _zteINSocketWrapper;
	}
	public void setZteINSocketWrapper(ZTEINSocketWrapper socketWrapper) {
		_zteINSocketWrapper = socketWrapper;
	}

	public String toString() {
		if(_zteINSocketWrapper==null){
			return "DestinationIP="+_destinationIP+" ,DestinationPort="+_destinationPort+"  ,_zteINSocketWrapper=null ,SessionId="+ _sessionId;	
		}else{
			return "DestinationIP"+_destinationIP+" ,DestinationPort"+_destinationPort+" , ,_zteINSocketWrapper"+_zteINSocketWrapper+" ,SessionId="+ _sessionId;
		}
	}

	public String getSessionId() {
		return _sessionId;
	}
	public void setSessionId(String id) {
		_sessionId = id;
	}

}
