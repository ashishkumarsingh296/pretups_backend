package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;

public class CalculatePayableAmountAPI implements ServiceKeywordControllerI{
	private final Log _log = LogFactory.getLog(CalculatePayableAmountAPI.class.getName());
    @Override
	public void process(RequestVO p_requestVO) {
    final String METHOD_NAME = "process";
    if (_log.isDebugEnabled()) {
        _log.debug("process", " Entered p_requestVO=" + p_requestVO);
    }
    Connection con = null;
    MComConnectionI mcomCon = null;
    ChannelUserDAO channelUserDAO = null;
    StringBuilder responseStr = null;
    try {
    	channelUserDAO = new ChannelUserDAO();
        final String messageArr[] = p_requestVO.getRequestMessageArray();
        HashMap reqMap = p_requestVO.getRequestMap();
        responseStr = new StringBuilder();
        HashMap<String, Object> resMap = new HashMap<>();
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Message Array " + messageArr);
        }
        
        if (!("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())))
        {
        	// throw invalid gateway code error
        }
        	
        mcomCon = new MComConnection();
        con = mcomCon.getConnection();	
		ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<>();
		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
		ChannelUserVO userVO = (ChannelUserVO)(p_requestVO.getSenderVO());
		String transferType = PretupsI.TRANSFER_TYPE_O2C, paymentType = PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE, 
				dualCommissionType = userVO.getDualCommissionType(), 
				commissionProfileID = userVO.getCommissionProfileSetID(), commissionProfileVersion = userVO.getCommissionProfileSetVersion();
		String products=(String) reqMap.get("PRODUCTS");
		String []itemsLists = products.split(",");
		for(int i=0;i<itemsLists.length;i++)
		{
			ChannelTransferItemsVO channelTransferItemsVO = new ChannelTransferItemsVO();
			String []product = itemsLists[i].split(":");
			if(product.length != 2)
				throw new BTSLBaseException(CalculatePayableAmountAPI.class.getName(), METHOD_NAME, PretupsErrorCodesI.MAPPGW_INVALID_PRODUCTS_FORMAT);
			channelTransferItemsVO.setRequestedQuantity(product[0]);
			channelTransferItemsVO.setProductName(product[1]);
			channelTransferItemsVO.setProductCode(channelUserDAO.product(con, product[1]));
			itemsList.add(channelTransferItemsVO);
		}
		
		ArrayList<NetworkProductVO> productList = (new NetworkProductDAO()).loadProductListForXfr(con, null, p_requestVO.getRequestNetworkCode());
		NetworkProductVO networkProductVO = new NetworkProductVO();
		for(int i=0;i<itemsList.size();i++)
		{
			ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO)itemsList.get(i);
			boolean isProductValid = false;
			for(int j=0;j<productList.size();j++)
			{
				networkProductVO=(NetworkProductVO)productList.get(j);
				if(networkProductVO.getProductCode().equals(channelTransferItemsVO.getProductCode()))
                {
					channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
					channelTransferItemsVO.setShortName(networkProductVO.getShortName());
					isProductValid = true;
					break;
                }
			}
			if(!isProductValid)
				throw new BTSLBaseException(CalculatePayableAmountAPI.class.getName(), METHOD_NAME, PretupsErrorCodesI.MAPPGW_INVALID_PRODUCT_CODE, 0, new String[]{channelTransferItemsVO.getProductCode()} , null);
		}
		final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
        channelTransferVO.setChannelTransferitemsVOList(itemsList);
        channelTransferVO.setOtfFlag(false);	
		channelTransferVO.setDualCommissionType(dualCommissionType);
		channelTransferVO.setType(transferType);
		channelTransferVO.setNetworkCode(p_requestVO.getRequestNetworkCode());
		channelTransferVO.setPayInstrumentType(paymentType);
		commissionProfileDAO.loadProductListWithTaxes(con, commissionProfileID, commissionProfileVersion, itemsList, transferType, paymentType);
		final ArrayList<KeyArgumentVO> errorList = new ArrayList<KeyArgumentVO>();
		for (int i = 0, k = itemsList.size(); i < k; i++) {
			ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
			KeyArgumentVO argumentVO = new KeyArgumentVO();
			if (!channelTransferItemsVO.isSlabDefine()) {
				argumentVO.setKey(PretupsErrorCodesI.ERROR_COMMISSION_SLAB_NOT_DEFINE_SUBKEY);
				argumentVO.setArguments(new String[] { channelTransferItemsVO.getShortName(), channelTransferItemsVO.getRequestedQuantity() });
				errorList.add(argumentVO);
			}
			else if ((PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()) % channelTransferItemsVO.getTransferMultipleOf()) != 0) {
				argumentVO = new KeyArgumentVO();
				argumentVO.setKey("channeltransfer.transferdetails.error.multipleof");
				argumentVO.setArguments(new String[] { channelTransferItemsVO.getProductName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getTransferMultipleOf()) });
				errorList.add(argumentVO);
			}
		}
		if (!errorList.isEmpty()) {
			throw new BTSLBaseException(ChannelTransferBL.class.getName(), "loadAndCalculateTaxOnProducts", PretupsErrorCodesI.ERROR_COMMISSION_SLAB_NOT_DEFINE, errorList);
		}
		ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, transferType);
		String resType = null;
		resType = reqMap.get("TYPE").toString();
		responseStr.append("{ \"type\": \"" + resType + "\" ,");
		responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
		responseStr.append("\"data\":{");
		responseStr.append("\"productDetails\":[ ");
		for(int i=0;i<channelTransferVO.getChannelTransferitemsVOList().size();i++)
		{
			ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) (channelTransferVO.getChannelTransferitemsVOList()).get(i);
			responseStr.append("{ \"productCode\": \"" +channelTransferItemsVO.getProductCode()+ "\" ,");
			responseStr.append("\"productName\": \"" +channelTransferItemsVO.getShortName()+ "\" ,");
			responseStr.append("\"orderQuantity\": \""+channelTransferItemsVO.getRequiredQuantity()+ "\" ,");
			responseStr.append("\"tax1Value\": \""+channelTransferItemsVO.getTax1Value()+ "\" ,");
			responseStr.append("\"tax1Rate\": \""+channelTransferItemsVO.getTax1Rate()+ "\" ,");
			responseStr.append("\"tax1Type\": \""+channelTransferItemsVO.getTax1Type()+ "\" ,");
			responseStr.append("\"tax2Value\": \""+channelTransferItemsVO.getTax2Value()+ "\" ,");
			responseStr.append("\"tax2Rate\": \""+channelTransferItemsVO.getTax2Rate()+ "\" ,");
			responseStr.append("\"tax2Type\": \""+channelTransferItemsVO.getTax2Type()+ "\" ,");
			responseStr.append("\"tax3Value\": \""+channelTransferItemsVO.getTax3Value()+ "\" ,");
			responseStr.append("\"tax3Rate\": \""+channelTransferItemsVO.getTax3Rate()+ "\" ,");
			responseStr.append("\"tax3Type\": \""+channelTransferItemsVO.getTax3Type()+ "\" ,");
			responseStr.append("\"commissionRate\": \""+channelTransferItemsVO.getCommRate()+ "\" ,");
			responseStr.append("\"commission\": \""+channelTransferItemsVO.getCommQuantity()+ "\" ,");
			responseStr.append("\"payableAmount\": \""+channelTransferItemsVO.getPayableAmount()+ "\" ,");
			responseStr.append("\"netPayableAmount\": \""+channelTransferItemsVO.getNetPayableAmount() + "\"");

			responseStr.append("},");	
		}
		//To trim last ','
		responseStr.setLength(responseStr.length() - 1);
		responseStr.append("],");
		long totalOrderAmount = 0,totalPayableAmount = 0;
		for(int i=0;i<channelTransferVO.getChannelTransferitemsVOList().size();i++)
		{
			ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) (channelTransferVO.getChannelTransferitemsVOList()).get(i);
			totalOrderAmount+=channelTransferItemsVO.getRequiredQuantity();
			totalPayableAmount+=channelTransferItemsVO.getPayableAmount();
		}
      	responseStr.append("\"totalDetails\":{ ");
   		responseStr.append("\"totalOrderAmount\": \"" +String.valueOf(totalOrderAmount)+ "\" ,");
		responseStr.append("\"totalPayableAmount\": \""+String.valueOf(totalPayableAmount) + "\"");
		responseStr.append("}}}");
		_log.debug("response ", "Payable Amount Calculation API  " + responseStr);
		resMap.put("RESPONSE", responseStr);
		p_requestVO.setResponseMap(resMap);
		p_requestVO.setMessageCode(PretupsErrorCodesI.SUCCESS);
    }catch (BTSLBaseException be) {
        p_requestVO.setSuccessTxn(false);
        p_requestVO.setMessageCode(be.getMessageKey());
        _log.error("process", "BTSLBaseException " + be.getMessage());
        _log.errorTrace(METHOD_NAME, be);
        if (be.getMessageList() != null && !be.getMessageList().isEmpty()) {
            final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
            p_requestVO.setMessageArguments(array);
        }
        if (be.getArgs() != null) {
            p_requestVO.setMessageArguments(be.getArgs());
        }
        if (be.isKey()) {
            p_requestVO.setMessageCode(be.getMessageKey());
        } else {
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }
    } 
    catch (Exception e) {
        p_requestVO.setSuccessTxn(false);
        OracleUtil.rollbackConnection(con, CalculatePayableAmountAPI.class.getName(), METHOD_NAME);
        _log.error("process", "BTSLBaseException " + e.getMessage());
        _log.errorTrace(METHOD_NAME, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CalculatePayableAmountAPI[process]", "", "", "",
                        "Exception:" + e.getMessage());
        p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        return;
    } finally {
		if (mcomCon != null) {
			mcomCon.close("CalculatePayableAmountAPI#process");
			mcomCon = null;
		}
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Exited ");
        }
    }

 	}
}
