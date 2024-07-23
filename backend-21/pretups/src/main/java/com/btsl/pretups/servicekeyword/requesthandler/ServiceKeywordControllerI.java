package com.btsl.pretups.servicekeyword.requesthandler;

import com.btsl.pretups.receiver.RequestVO;

public interface ServiceKeywordControllerI {
    /**
     * Process
     * 
     * @param p_con
     * @param p_requestVO
     * @return void
     */
    public void process(RequestVO p_requestVO);
}
