package com.btsl.voms.vomsprocesses.util;

/*
 * @(#)VoucherFileChecksI.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Sidhartha 4/07/06 Initial Creation
 * Gurjeet Singh Bedi 21/07/2006 Modified (Restructured the code)
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Interface class for voucher file processing
 */

import java.sql.Connection;

import com.btsl.common.BTSLBaseException;
import com.btsl.voms.voucher.businesslogic.VomsSerialUploadCheckVO;

public interface VoucherFileChecksI {
    public void loadConstantValues() throws BTSLBaseException;

    public VoucherUploadVO validateVoucherFile() throws BTSLBaseException, Exception;

    public void getFileLength(VoucherUploadVO p_voucherUploadVO) throws BTSLBaseException, Exception;

    public VomsSerialUploadCheckVO populateVomsSerialUploadCheckVO(Connection p_con) throws BTSLBaseException, Exception;
}
