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

/*
 * This class implements LookupsRestService and provides basic method to load Look and sublookups
 */
/*@RestController
@RequestMapping("/cardGroup")*/
@Path("/voucherBundle")
//@Api(value = "Modify voucher bundle")
public class ModifyVoucherBundle {

	public static final Log log = LogFactory.getLog(ModifyVoucherBundle.class.getName());

	@SuppressWarnings("unchecked")
	@POST
	@Path("/modifyVoucherBundle")
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "Modify voucher bundle", response = PretupsResponse.class)
	public PretupsResponse<JsonNode> modifyVoucherBundle(@RequestBody String requestData)
			throws IOException, SQLException, BTSLBaseException {
		final String methodName = "addVoucherBundle";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<JsonNode> response = new PretupsResponse<>();
		ArrayList list = new ArrayList<>();
		ArrayList vomsBundleDetailIDs = new ArrayList();
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

			String vomsBundleID = dataNode.get("vomsBundleID").toString().substring(1,
					dataNode.get("vomsBundleID").toString().length() - 1);

			vomsBundleDetailIDs = voucherBundleDAO.getVomsBundleDetailIDs(con, vomsBundleID);

			VoucherBundleVO voucherBundleVO = new VoucherBundleVO();
			voucherBundleVO.setVomsBundleID(vomsBundleID);
			voucherBundleVO.setRetailPrice(Double.parseDouble(dataNode.get("retailPrice").toString().substring(1,
					dataNode.get("retailPrice").toString().length() - 1)));
			voucherBundleVO.setCreatedOn(currentDate);
			voucherBundleVO.setCreatedBy(dataNode.get("createdBy").toString().substring(1,
					dataNode.get("createdBy").toString().length() - 1));
			voucherBundleVO.setModifiedOn(currentDate);
			voucherBundleVO.setModifiedBy(dataNode.get("modifiedBy").toString().substring(1,
					dataNode.get("modifiedBy").toString().length() - 1));
			voucherBundleVO.setStatus(PretupsI.STATUS_ACTIVE);

			int updateSetCount = voucherBundleDAO.updateVoucherBundle(con, voucherBundleVO);

			String temp1 = "profile";
			String temp2 = "quantity";
			String pID = "";
			String profileID = "";
			int i = 0;
			for (i = 0; i < vomsBundleDetailIDs.size(); i++) {
				pID = dataNode.get(temp1 + i).toString();
				profileID = pID.substring(1, pID.length() - 1);
				String quantity = dataNode.get(temp2 + i).toString();
				if (Integer.parseInt(quantity) != 0) {
					voucherBundleVO.setVomsBundleDetailID(vomsBundleDetailIDs.get(i).toString());
					voucherBundleVO.setQuantity(Integer.parseInt(quantity));
					voucherBundleVO.setVoucherProfile(profileID);
					voucherBundleVO.setStatus(PretupsI.STATUS_ACTIVE);
					int count = voucherBundleDAO.updateVoucherBundleDetails(con, voucherBundleVO);
				} else if (Integer.parseInt(quantity) == 0) {
					voucherBundleVO.setVomsBundleDetailID(vomsBundleDetailIDs.get(i).toString());
					voucherBundleVO.setStatus(PretupsI.STATUS_DELETE);
					int count = voucherBundleDAO.updateVoucherBundleDetailsStatus(con, voucherBundleVO);
				}
			}
			for (int j = i; j < Integer.parseInt(Constants.getProperty("NUMBER_OF_ROWS")); j++) {
				pID = dataNode.get(temp1 + j).toString();
				profileID = pID.substring(1, pID.length() - 1);
				String quantity = dataNode.get(temp2 + j).toString();
				if (Integer.parseInt(quantity) != 0) {
					voucherBundleVO.setVomsBundleDetailID(
							String.valueOf(IDGenerator.getNextID(PretupsI.VOMS_BUNDLE_DETAIL_ID, TypesI.ALL)));
					voucherBundleVO.setBundleName(dataNode.get("bundleName").toString().substring(1,
							dataNode.get("bundleName").toString().length() - 1));
					voucherBundleVO.setQuantity(Integer.parseInt(quantity));
					voucherBundleVO.setVoucherProfile(profileID);
					voucherBundleVO.setStatus(PretupsI.STATUS_ACTIVE);
					int count = voucherBundleDAO.addVoucherBundleDetails(con, voucherBundleVO);
				} else
					continue;
			}

			if (updateSetCount <= 0) {
				try {
					mcomCon.finalRollback();
				} catch (Exception e) {
					log.errorTrace(methodName, e);
				}
				log.error(methodName, "Error: while updating voucher bundle details");
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			}

			mcomCon.finalCommit();
			// Prepare Response
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "voucherbundle.modifyvoucherbundle.successmessage");

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
