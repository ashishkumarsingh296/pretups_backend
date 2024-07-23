package com.btsl.pretups.user.businesslogic;

/**
 * @(#)UserMessageVO.java
 *                        Copyright(c) 2010, Comviva Technologies Ltd.
 *                        All Rights Reserved
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Mahindra Comviva 10 OCT'10 Intial Creation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 */
public class UserMessageVO {
    private int _lineNo;
    private String _msg1;
    private String _msg2;
    private String _reason;

    public UserMessageVO(
                    String p_msg1, String p_msg2) {
        this._msg1 = p_msg1;
        this._msg2 = p_msg2;
    }

    public UserMessageVO(
                    int p_lineNo, String p_msg1, String p_msg2, String p_reason) {
        this._lineNo = p_lineNo;
        this._msg1 = p_msg1;
        this._msg2 = p_msg2;
        this._reason = p_reason;
    }

    /**
     * @return the lineNo
     */
    public int getLineNo() {
        return _lineNo;
    }

    /**
     * @return the _msg1
     */
    public String getMsg1() {
        return _msg1;
    }

    /**
     * @return the toGeoDomCode
     */
    public String getMsg2() {
        return _msg2;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return _reason;
    }
}
