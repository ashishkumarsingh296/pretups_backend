/*
 * Created on Aug 29, 2005
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.p2p.query.businesslogic;

import java.io.Serializable;

import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;

/**
 * @author ved.sharma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class TransferDetailsVO implements Serializable {
    private TransferVO transferVO = null;
    private TransferItemVO transferItemVO = null;

    /**
     * @return Returns the transferItemVO.
     */
    public TransferItemVO getTransferItemVO() {
        return transferItemVO;
    }

    /**
     * @param transferItemVO
     *            The transferItemVO to set.
     */
    public void setTransferItemVO(TransferItemVO transferItemVO) {
        this.transferItemVO = transferItemVO;
    }

    /**
     * @return Returns the transferVO.
     */
    public TransferVO getTransferVO() {
        return transferVO;
    }

    /**
     * @param transferVO
     *            The transferVO to set.
     */
    public void setTransferVO(TransferVO transferVO) {
        this.transferVO = transferVO;
    }
}
