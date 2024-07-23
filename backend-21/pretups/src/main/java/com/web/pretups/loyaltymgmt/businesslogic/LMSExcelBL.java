package com.web.pretups.loyaltymgmt.businesslogic;

import java.util.ArrayList;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelRW;

import jxl.write.WritableWorkbook;

public class LMSExcelBL {

	private final Log _log = LogFactory.getLog(this.getClass().getName());

	public void createWorkbook(ArrayList dataList, int SheetNo,
			WritableWorkbook workbook, MessageResources p_messages,
			Locale p_locale) {
		final String METHOD_NAME = "createWorkbook";
		if (_log.isDebugEnabled()) {
			_log.debug("createWorkbook", "Entered");
		}
		String fileArr[][] = null;
		String controlGroup = "Y";
		try {
			controlGroup = Constants.getProperty("LMS_CONTROL_GROUP_REQUIRED");
		} catch (RuntimeException e1) {
			controlGroup = "Y";
		}
		int cols = 3;
		if (PretupsI.YES.equals(controlGroup)) {
			cols = 4;
		}
		int rows = dataList.size() + 1;
		fileArr = new String[rows][cols]; // ROW-COL
		fileArr[0][0] = "lmsprofile.xlsheading.label.msisdn";
		fileArr[0][1] = "lmsprofile.xlsheading.label.associate.currently";
		fileArr[0][2] = "lmsprofile.xlsheading.label.associate.required";
		if (PretupsI.YES.equals(controlGroup)) {
			fileArr[0][3] = "lmsprofile.xlsheading.label.controlgroup.required";
		}
		try {
			fileArr = this.convertTo2dArray(fileArr, dataList, rows,
					controlGroup);
			ExcelRW excelRW = new ExcelRW();
			// excelRW.writeExcel(ExcelFileIDI.BATCH_FOC_INITIATE,fileArr,((MessageResources)
			// request.getAttribute(Globals.MESSAGES_KEY)),BTSLUtil.getBTSLLocale(request),filePath+""+fileName);
			excelRW.writeMultipleExcelNew(workbook,
					ExcelFileIDI.BATCH_FOC_INITIATE, fileArr, p_messages,
					p_locale, SheetNo);
		} catch (Exception e) {
			_log.error("createWorkbook", "Exceptin:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("createWorkbook", "Exited:");
			}
		}

	}

	private String[][] convertTo2dArray(String[][] p_fileArr,
			ArrayList dataList, int p_rows, String p_controlGroup)
			throws BTSLBaseException {
		final String METHOD_NAME = "convertTo2dArray";
		if (_log.isDebugEnabled()) {
			_log.debug("convertTo2dArray", "Entered p_fileArr=" + p_fileArr
					+ "dataList=" + dataList);
		}
		try {
			// first row is already generated,and the number of cols are fixed
			// to eight
			// Iterator iterator=p_hashMap.keySet().iterator();
			String key = null;
			ChannelUserVO channelUserVO = null;
			int rows = 0;
			int cols;
			int dataListSize = dataList.size();
			for (int i = 0; i < dataListSize; i++) {
				key = (String.valueOf(i + 1));
				channelUserVO = (ChannelUserVO) dataList.get(i);
				// Only those records are written into the xls file for which
				// status='Y' and insuspend='N'

				{
					rows++;
					if (rows >= p_rows) {
						break;
					}
					cols = 0;
					// p_fileArr[rows][cols++]=key;
					p_fileArr[rows][cols++] = channelUserVO.getMsisdn();

					p_fileArr[rows][cols++] = channelUserVO.getUserProfileID();// Y
																				// or
																				// N
																				// (for
																				// profile
																				// to
																				// be
																				// associated
																				// or
																				// not)
					p_fileArr[rows][cols++] = "";// Y or N (for profile to be
													// associated or not)
					if (PretupsI.YES.equals(p_controlGroup)) {
						if (PretupsI.YES.equalsIgnoreCase(channelUserVO
								.getControlGroup())) {
							p_fileArr[rows][cols++] = channelUserVO
									.getControlGroup();// Y or N (for control
														// group to be
														// associated or not)
						} else {
							p_fileArr[rows][cols++] = "";
						}
					}
				}

			}
		} catch (Exception e) {
			_log.error("convertTo2dArray", "Exceptin:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException("LMSExcelBL", METHOD_NAME, "");
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("convertTo2dArray", "Exited p_fileArr=" + p_fileArr);
			}
		}
		return p_fileArr;
	}
}
