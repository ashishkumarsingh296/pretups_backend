package com.restapi.user.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;

public interface DownloadTemplateService {
	public void downloadC2CBatchTemplate(FileDownloadResponse fileDownloadResponse)throws BTSLBaseException, RowsExceededException, WriteException, IOException;
	public void downloadGiftTemplate(FileDownloadResponse fileDownloadResponse)throws BTSLBaseException, RowsExceededException, WriteException, IOException;
	public void downloadRechargeTemplate(FileDownloadResponse fileDownloadResponse)throws BTSLBaseException, RowsExceededException, WriteException, IOException;
	public void downloadFixedTemplate(FileDownloadResponse fileDownloadResponse)throws BTSLBaseException, RowsExceededException, WriteException, IOException;
	public void downloadInternetTemplate(FileDownloadResponse fileDownloadResponse)throws BTSLBaseException, RowsExceededException, WriteException, IOException;
	public void downloadDvdMasterSheet(Connection con, FileDownloadResponse fileDownloadResponse, String userID, String networkID)throws BTSLBaseException, RowsExceededException, WriteException, IOException;
	public void downloadDvdTemplate(FileDownloadResponse fileDownloadResponse)throws BTSLBaseException, RowsExceededException, WriteException, IOException;
	public void downloadO2CWithdrawTemplate(FileDownloadResponse fileDownloadResponse)throws BTSLBaseException, RowsExceededException, WriteException, IOException;
	public void downloadO2CPurchaseTemplate(FileDownloadResponse fileDownloadResponse)throws BTSLBaseException, RowsExceededException, WriteException, IOException;
	public void downloadBulkUserTemplate(Connection con, UserVO userVO, String domainCode, String geographyCode, FileDownloadResponse fileDownloadResponse, HttpServletResponse responseswag) throws BTSLBaseException, SQLException, ParseException, IOException;
}
