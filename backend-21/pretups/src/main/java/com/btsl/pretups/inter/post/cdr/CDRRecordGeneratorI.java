/*
 * CDRRecordGeneratorI.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Ruwali 24/05/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * This Interface consists of common methods signature of CDR record generation
 */
package com.btsl.pretups.inter.post.cdr;

import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;

public interface CDRRecordGeneratorI {
    public String[] generateCDRRecords(ArrayList p_queueVOList, String p_interfaceID) throws BTSLBaseException;

    public String generateHeaderRecord(String p_headerName, long p_numOfRecords) throws BTSLBaseException;

    public String generateTrailerRecord(String p_fileName, long p_numOfRecords) throws BTSLBaseException;

    public void loadConstants(String interfaceID);
}
