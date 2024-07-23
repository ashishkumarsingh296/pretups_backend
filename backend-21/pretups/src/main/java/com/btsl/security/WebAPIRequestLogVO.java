package com.btsl.security;

public class WebAPIRequestLogVO {
    private String type;
    private String method;
    private String path;
    private String headers;
    private String body;


    private String requestNo;
    private String IP;
    private long totaltime;


    public WebAPIRequestLogVO() {}

    public WebAPIRequestLogVO(String type, String method, String path, String headers, String body, String requestNo, String IP, long totaltime) {
        this.type = type;
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.requestNo = requestNo;
        this.IP = IP;
        this.totaltime=totaltime;

    }


    public long getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(long totaltime) {
        this.totaltime = totaltime;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String iP) {
        IP = iP;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub

        if(type.equalsIgnoreCase("ReqIN"))
        {
            return "[RequestType: "+type+"] [ requestNo: "+requestNo+"][ IP: "+IP+"][Method: "+method+"] [ path: "+path+"][headers: "+headers+"] [ Request: "+body+"]";
        }else{
            return "[RequestType: "+type+"] [ requestNo: "+requestNo+"][ IP: "+IP+"][Method: "+method+"] [ path: "+path+"][headers: "+headers+"] [ Response: "+body+"] [TT: "+totaltime+" ms]";
        }
    }
}
