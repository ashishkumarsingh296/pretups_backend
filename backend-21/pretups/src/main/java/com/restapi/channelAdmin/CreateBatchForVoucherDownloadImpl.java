package com.restapi.channelAdmin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.IDGenerator;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;

@Service("CreateBatchForVoucherDownloadI")
public class CreateBatchForVoucherDownloadImpl implements CreateBatchForVoucherDownloadI {

	public static final Log LOG = LogFactory.getLog(CreateBatchForVoucherDownloadImpl.class.getName());
	public static final String classname = "CreateBatchForVoucherDownloadImpl";

	@Override
	public DenominationResponse getMrpList(Connection con, String loginId, String voucherType, String voucherSegment,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "getMrpList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		DenominationResponse response = new DenominationResponse();
		ArrayList categoryList = null;
		VomsCategoryWebDAO vomsCategorywebDAO = new VomsCategoryWebDAO();
		VomsCategoryVO vomsCategoryVO = null;
		String genratedmrpstr = null;
		String mrp = null;
		String vType = null;
		ArrayList<String> mrplist;
		ArrayList<VomsCategoryVO> voucherTypeList;
		UserDAO userDAO = new UserDAO();
		UserVO user = new UserVO();
		try {
			mrplist = new ArrayList<String>();
			voucherTypeList = new ArrayList<VomsCategoryVO>();
			user = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
				voucherTypeList = vomsCategorywebDAO.loadUserCategoryList(con,user.getUserID());
			else
				voucherTypeList = vomsCategorywebDAO.loadCategoryList(con);
			if (voucherTypeList.isEmpty()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(METHOD_NAME, "For adding a new subcategory, no parent category found");
				}
				throw new BTSLBaseException(this, METHOD_NAME, "vmcategory.addsubcategoryforvoms.err.msg.noparentcatfound");
			}
			String[] allowedVoucherType = VomsUtil.getAllowedVoucherTypesForScreen(PretupsI.SCREEN_VOUCHER_DOWNLOAD);
			ArrayList<VomsCategoryVO> filteredVoucherList = VomsUtil.getAllowedVoucherType(allowedVoucherType, voucherTypeList);

			VomsCategoryVO vomsCategory = filteredVoucherList.stream()
					.filter(element -> element.getType().equals(voucherType))
					.findFirst()
					.orElse(null);

			if (vomsCategory != null) {
				vType = vomsCategory.getVoucherType();
			}

			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DOWNLD_BATCH_BY_BATCHID)))
					.booleanValue()) {
				vomsCategorywebDAO = new VomsCategoryWebDAO();
				categoryList = vomsCategorywebDAO.loadCategoryListForEnable(con, VOMSI.VOMS_STATUS_ACTIVE,
						VOMSI.EVD_CATEGORY_TYPE_FIXED, vType, voucherSegment, user.getNetworkID());
			}

			if (categoryList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_DENOM, 0, null);
			} else {

				for (int i = 0; i < categoryList.size(); i++) {
					vomsCategoryVO = (VomsCategoryVO) categoryList.get(i);
					if (BTSLUtil.isNullString(genratedmrpstr)) {
						genratedmrpstr = Double.toString(vomsCategoryVO.getMrp());
						mrp = Double.toString(vomsCategoryVO.getMrp());
						mrplist.add(mrp);
					} else {
						genratedmrpstr = genratedmrpstr + "," + vomsCategoryVO.getMrp();
						mrp = Double.toString(vomsCategoryVO.getMrp());
						mrplist.add(mrp);
					}
				}
			}
			response.setMrpList(mrplist);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DENOM_LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DENOM_LIST_FOUND);

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.NO_DENOM, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.NO_DENOM);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public BatchListResponseVO getBatchIdDetails(Connection con, String loginId, String denomination,
			String voucherType, String voucherSegment, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "getBatchIdDetails";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		BatchListResponseVO response = new BatchListResponseVO();
		VomsCategoryWebDAO vomsCategorywebDAO = null;
		ArrayList<VomsVoucherVO> batchIdList;
		UserDAO userDAO = new UserDAO();
		UserVO user = new UserVO();
		VomsVoucherVO vomsVoucherVO = new VomsVoucherVO();
		String vType = null;
		ArrayList<VomsCategoryVO> voucherTypeList;

		try {

			batchIdList = new ArrayList<VomsVoucherVO>();
			vomsCategorywebDAO = new VomsCategoryWebDAO();
			voucherTypeList = new ArrayList<VomsCategoryVO>();
			user = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
				voucherTypeList = vomsCategorywebDAO.loadUserCategoryList(con,user.getUserID());
			else
				voucherTypeList = vomsCategorywebDAO.loadCategoryList(con);
			if (voucherTypeList.isEmpty()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(METHOD_NAME, "For adding a new subcategory, no parent category found");
				}
				throw new BTSLBaseException(this, METHOD_NAME, "vmcategory.addsubcategoryforvoms.err.msg.noparentcatfound");
			}
			String[] allowedVoucherType = VomsUtil.getAllowedVoucherTypesForScreen(PretupsI.SCREEN_VOUCHER_DOWNLOAD);
			ArrayList<VomsCategoryVO> filteredVoucherList = VomsUtil.getAllowedVoucherType(allowedVoucherType, voucherTypeList);

			VomsCategoryVO vomsCategory = filteredVoucherList.stream()
					.filter(element -> element.getType().equals(voucherType))
					.findFirst()
					.orElse(null);

			if (vomsCategory != null) {
				vType = vomsCategory.getVoucherType();
			}

			if (!BTSLUtil.isNullString(denomination)) {
				batchIdList = vomsCategorywebDAO.loadBatchID(con, denomination, vType, voucherSegment,
						user.getNetworkID());
			}
			if (BTSLUtil.isNullorEmpty(batchIdList)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BATCH_NOT_FOUND, 0, null);
			}

			else {

				response.setBatchIdList(batchIdList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_ID_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.BATCH_ID_FOUND);
			}
		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.BATCH_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.BATCH_NOT_FOUND);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public BaseResponse createBatchIdbyAdmin(Connection con, String loginId, String fromSerialNo, String toSerialNo,
			String voucherType, String downloadType, String productId, String quantity,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "createBatchIdbyAdmin";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		BaseResponse response = new BaseResponse();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String batchNo = null;
		String voucherUpdateStatus = null;
		long min_no;
		VomsBatchVO vomsBatchVO = null;
		VomsCategoryWebDAO vomsCategorywebDAO = new VomsCategoryWebDAO();
		int updatecount = 0;
		String serialnSequence[] = new String[2];
		String seqid = null;
		String End_range = null;
		long total_down_vouchers;
		String[] arr = new String[2];
		int c = 0;
		UserDAO userDAO = new UserDAO();
		UserVO user = new UserVO();
		ArrayList<VomsCategoryVO> voucherTypeList = null;
		VomsCategoryWebDAO categorywebDAO = null;
		String vType = null;

		try {
			categorywebDAO = new VomsCategoryWebDAO();
			vomsBatchVO = new VomsBatchVO();
			long noOfVoucher = Long.parseLong(quantity);
			min_no = vomsCategorywebDAO.loadSerialno(con, productId, voucherType);
			user = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED)))
					.booleanValue())
				voucherTypeList = vomsCategorywebDAO.loadUserCategoryList(con, user.getUserID());
			else
				voucherTypeList = categorywebDAO.loadCategoryList(con);
			String[] allowedVoucherType = VomsUtil.getAllowedVoucherTypesForScreen(PretupsI.SCREEN_VOUCHER_DOWNLOAD);
			ArrayList<VomsCategoryVO> filteredVoucherList = VomsUtil.getAllowedVoucherType(allowedVoucherType,
					voucherTypeList);
			VomsCategoryVO vomsCategory = filteredVoucherList.stream()
					.filter(element -> element.getType().equals(voucherType))
					.findFirst()
					.orElse(null);

			if (vomsCategory != null) {
				vType = vomsCategory.getVoucherType();
			}

			if (min_no >= 0) {
				String Start_range;
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DOWNLD_BATCH_BY_BATCHID)))
						.booleanValue()) {
					Start_range = fromSerialNo;
					Start_range = BTSLUtil
							.padZeroesToLeft(Start_range,
									((Integer) (PreferenceCache
											.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH)))
													.intValue());
					serialnSequence = vomsCategorywebDAO.lastSerialNumber(con, Start_range);
					End_range = toSerialNo;
					seqid = serialnSequence[1];
				}

				else {
					noOfVoucher = noOfVoucher + min_no;
					Start_range = Long.toString(min_no);

					Start_range = BTSLUtil
							.padZeroesToLeft(Start_range,
									((Integer) (PreferenceCache
											.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH)))
													.intValue());
					serialnSequence = vomsCategorywebDAO.lastSerialNumber(con, Start_range);
					End_range = serialnSequence[0];
					seqid = serialnSequence[1];
				}
				Date currentDate = new Date();
				total_down_vouchers = (Long.parseLong(End_range) - Long.parseLong(Start_range)) + 1;
				vomsBatchVO.setCreatedDate(currentDate);
				vomsBatchVO.setModifiedDate(currentDate);
				vomsBatchVO.setCreatedOn(currentDate);
				vomsBatchVO.setProductid(productId);
				vomsBatchVO.setBatchType(VOMSI.VOMS_PRINT_BATCH);
				batchNo = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE,
						String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));

				vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batchNo));
				if (Long.parseLong(quantity) < total_down_vouchers) {
					End_range = Long.toString(noOfVoucher - 1);
					End_range = BTSLUtil
							.padZeroesToLeft(End_range,
									((Integer) (PreferenceCache
											.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH)))
													.intValue());
					arr[c++] = "" + vomsBatchVO.getQuantity();
				} else {
					arr[c++] = "" + total_down_vouchers;
				}

				voucherUpdateStatus = VomsUtil.getNextVoucherLifeStatus(VOMSI.VOUCHER_NEW,
						BTSLUtil.getVoucherTypeCode(filteredVoucherList, vType));
				if ("printing".equals(downloadType)) {
					updatecount = vomsCategorywebDAO.updateStatus(con, voucherUpdateStatus, productId, Start_range,
							End_range, vomsBatchVO.getBatchNo(), vType, seqid);
				}

				if ("thirdparty".equals(downloadType)) {

					updatecount = vomsCategorywebDAO.updateStatus(con,
							((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_THIRDPARTY_STATUS)),
							productId, Start_range, End_range, vomsBatchVO.getBatchNo(), vType, seqid);
				}

				if (updatecount <= 0 && "printing".equals(downloadType)) {
					con.rollback();
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BATCH_CREATION_FAIL, 0,
							null);
				} else {

					vomsBatchVO.setFromSerialNo(Start_range);
					vomsBatchVO.setToSerialNo(End_range);
					if (Long.parseLong(quantity) < total_down_vouchers) {
						vomsBatchVO.setNoOfVoucher(Long.parseLong(vomsBatchVO.getQuantity()));
					} else {
						vomsBatchVO.setNoOfVoucher(total_down_vouchers);
					}

					int insert_count = vomsCategorywebDAO.insertBatchPrintDetails(con, vomsBatchVO);
					if ("thirdparty".equals(downloadType)) {

						updatecount = vomsCategorywebDAO.updateStatus(con,
								((String) PreferenceCache
										.getSystemPreferenceValue(PreferenceI.VOUCHER_THIRDPARTY_STATUS)),
								productId, Start_range, End_range, vomsBatchVO.getBatchNo(),
								vType, seqid);

						// Insert in voms_batches.
						if (updatecount > 0) {
							vomsBatchVO.set_NetworkCode(user.getNetworkID());
							vomsBatchVO.setCreatedBy(user.getUserID());
							vomsBatchVO.setModifiedBy(user.getUserID());
							vomsBatchVO.setModifiedOn(vomsBatchVO.getCreatedDate());
							vomsBatchVO.setReferenceType(VOMSI.VOMS_PRINT_ENABLE_STATUS);
							vomsBatchVO.setBatchType(((String) PreferenceCache
									.getSystemPreferenceValue(PreferenceI.VOUCHER_THIRDPARTY_STATUS)));
							String batch_no = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE,
									String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
							vomsBatchVO.setReferenceNo(vomsBatchVO.getBatchNo());

							vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batch_no));
							vomsBatchVO.setQuantity(Long.toString(vomsBatchVO.getNoOfVoucher()));
							vomsBatchVO.setSuccessCount(vomsBatchVO.getNoOfVoucher());
							vomsBatchVO.setFailCount(0);

							final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
							final int insert_count1 = channelTransferWebDAO.insertVomsBatchesDownoad(con, vomsBatchVO);
							if (insert_count1 < 0) {
								con.rollback();
								throw new BTSLBaseException(classname, METHOD_NAME,
										PretupsErrorCodesI.BATCH_CREATION_FAIL, 0, null);
							}
						} else {
							con.rollback();
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BATCH_CREATION_FAIL,
									0, null);
						}

					}

					if (insert_count < 0) {

						con.rollback();
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BATCH_CREATION_FAIL, 0,
								null);

					} else {
						arr[c++] = vomsBatchVO.getBatchNo();

						con.commit();
						response.setStatus((HttpStatus.SC_OK));
						String resmsg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.BATCH_CREATION_SUCCESS, null);
						response.setMessage(resmsg);
						response.setMessageCode(PretupsErrorCodesI.BATCH_CREATION_SUCCESS);

					}

				}
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BATCH_CREATION_FAIL, 0, null);
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.BATCH_CREATION_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.BATCH_CREATION_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

}
