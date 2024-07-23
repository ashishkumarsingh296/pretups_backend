package com.restapi.superadmin.serviceI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.restapi.superadmin.requestVO.BatchOperatorUserInitiateRequestVO;
import com.restapi.superadmin.responseVO.BatchOperatorUserInitiateResponseVO;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public interface BatchOperatorUserInitiateServiceI {
	
	public BatchOperatorUserInitiateResponseVO downloadFileTemplate(Connection con, MComConnectionI mcomCon, Locale locale, String categoryType, ChannelUserVO userVO, BatchOperatorUserInitiateResponseVO response, HttpServletResponse responseSwag) throws BTSLBaseException, SQLException, ParseException, RowsExceededException, WriteException, IOException;
	public ArrayList<MasterErrorList> basicFileValidations(BatchOperatorUserInitiateRequestVO request, BatchOperatorUserInitiateResponseVO response, String categoryType, Locale locale, ArrayList<MasterErrorList> inputValidations) throws BTSLBaseException,SQLException;
	public boolean uploadAndValidateFile(Connection con,MComConnectionI mcomCon, ChannelUserVO userVO, BatchOperatorUserInitiateRequestVO request, BatchOperatorUserInitiateResponseVO response) throws BTSLBaseException, SQLException;
	public void processUploadedFile(Connection con,MComConnectionI mcomCon, ChannelUserVO userVO, String categoryType, BatchOperatorUserInitiateRequestVO request, BatchOperatorUserInitiateResponseVO response, HttpServletResponse responseSwag) throws BTSLBaseException, SQLException, FileNotFoundException, IOException, BiffException;
}
