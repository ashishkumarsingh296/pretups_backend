package com.restapi.voucherbundle.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.RequestBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;
import com.btsl.voms.voucherbundle.businesslogic.VoucherBundleDAO;
import com.btsl.voms.voucherbundle.businesslogic.VoucherBundleVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/voucherBundle")
//@Api(value = "Add voucher bundle")
public class AddVoucherBundle {

	public static final Log log = LogFactory.getLog(AddVoucherBundle.class.getName());

	@SuppressWarnings("unchecked")
	@POST
	@Path("/addVoucherBundle")
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "Add voucher bundle", response = PretupsResponse.class)
	public PretupsResponse<JsonNode> addVoucherBundle(@RequestBody String requestData)
			throws IOException, SQLException, BTSLBaseException {
		final String methodName = "addVoucherBundle";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		int insertSetCount = 0;
		int count = 0;
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<JsonNode> response = new PretupsResponse<>();
		ArrayList list = new ArrayList<>();
		final Date currentDate = new Date();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			response = new PretupsResponse<JsonNode>();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,
					new TypeReference<JsonNode>() {
					});
			JsonNode dataNode = requestNode.get("data");
			final VoucherBundleDAO voucherBundleDAO = new VoucherBundleDAO();

			VoucherBundleVO voucherBundleVO = new VoucherBundleVO();
			voucherBundleVO.setVomsBundleID(String.valueOf(IDGenerator.getNextID(PretupsI.VOMS_BUNDLE_ID, TypesI.ALL)));
			voucherBundleVO.setBundleName(dataNode.get("bundleName").toString().substring(1,
					dataNode.get("bundleName").toString().length() - 1));
			voucherBundleVO.setPrefixID(
					dataNode.get("prefixID").toString().substring(1, dataNode.get("prefixID").toString().length() - 1));
			voucherBundleVO.setRetailPrice(Double.parseDouble(dataNode.get("retailPrice").toString().substring(1,
					dataNode.get("retailPrice").toString().length() - 1)));
			voucherBundleVO.setLastBundleSequence(0L);
			voucherBundleVO.setCreatedOn(currentDate);
			voucherBundleVO.setCreatedBy(dataNode.get("createdBy").toString().substring(1,
					dataNode.get("createdBy").toString().length() - 1));
			voucherBundleVO.setModifiedOn(currentDate);
			voucherBundleVO.setModifiedBy(dataNode.get("modifiedBy").toString().substring(1,
					dataNode.get("modifiedBy").toString().length() - 1));
			voucherBundleVO.setStatus(PretupsI.STATUS_ACTIVE);

			if (!(voucherBundleDAO.isVoucherBundleNameExist(con, voucherBundleVO))
					&& !(voucherBundleDAO.isPrefixIDExist(con, voucherBundleVO))) {
				insertSetCount = voucherBundleDAO.addVoucherBundle(con, voucherBundleVO);

				String temp1 = "profile";
				String temp2 = "quantity";
				for (int i = 0; i < Integer.parseInt(Constants.getProperty("NUMBER_OF_ROWS")); i++) {
					String pID = dataNode.get(temp1 + i).toString();
					String profileID = pID.substring(1, pID.length() - 1);
					String quantity = dataNode.get(temp2 + i).toString();
					if (Integer.parseInt(quantity) != 0) {
						voucherBundleVO.setVomsBundleDetailID(
								String.valueOf(IDGenerator.getNextID(PretupsI.VOMS_BUNDLE_DETAIL_ID, TypesI.ALL)));
						voucherBundleVO.setQuantity(Integer.parseInt(quantity));
						voucherBundleVO.setVoucherProfile(profileID);

						count = voucherBundleDAO.addVoucherBundleDetails(con, voucherBundleVO);

					} else
						continue;
				}
			} else if ((voucherBundleDAO.isVoucherBundleNameExist(con, voucherBundleVO))
					|| (voucherBundleDAO.isPrefixIDExist(con, voucherBundleVO))) {
				response.setResponse(PretupsI.RESPONSE_FAIL, true, "voucherbundle.addvoucherbundle.existingdetails");
				return response;
			}

			if (insertSetCount <= 0 || count <= 0) {
				try {
					mcomCon.finalRollback();
				} catch (Exception e) {
					log.errorTrace(methodName, e);
				}
			}

			mcomCon.finalCommit();
			// Prepare Response
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "voucherbundle.addvoucherbundle.successaddmessage");

		} catch (BTSLBaseException e) {
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(e.getMessage());
			response.setMessageKey(e.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("addVoucherBundle#" + methodName);
					mcomCon = null;
				}
			} catch (Exception e) {
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}

		return response;
	}
}
