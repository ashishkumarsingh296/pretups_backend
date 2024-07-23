package com.btsl.pretups.processes;



import java.sql.Connection;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.LoanDataVO;


public interface UserLoanFileChecksI {

    public void loadConstantValues() throws BTSLBaseException;

    public LoanDataVO validateLoanDataFile(Connection p_con) throws BTSLBaseException, Exception;

    public void getFileLength(LoanDataVO p_userLoanVO) throws BTSLBaseException, Exception;



}
