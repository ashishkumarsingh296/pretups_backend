package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelVoucherItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;

public class C2CVoucherTransferDetailsController implements ServiceKeywordControllerI{
	Connection con = null;
	MComConnectionI mcomCon = null;
	String p_roleCode = null;

	

	protected final Log _log = LogFactory.getLog(getClass().getName());

	
	private ChannelTransferVO viewTransferDetails(RequestVO p_requestVO) throws BTSLBaseException, SQLException{
		final String METHOD_NAME = "viewTransferDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered ");
		}
		Gson gson = new Gson();
		String transferId = null;
		String networkCode = null;
		String networkCodeFor = null;
		String transferType = null;
		ArrayList vomsProductlist = new ArrayList<>();
		try{
		String requestMsg = p_requestVO.getRequestMessageOrigStr();
		ArrayList voucherTypeList =  new ArrayList<>();
		if(PretupsI.MOBILE_APP_GATEWAY.equals(p_requestVO.getRequestGatewayCode())){
		final String message[] = p_requestVO.getRequestMessageArray();
		transferId = message[3];
		transferType = message[4];
		networkCode = message[5];
		networkCodeFor = message[6];
		}
		else if (PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode()))  {
		if (requestMsg != null) {
			C2CTransferDetailsVO reqMsgObj = gson.fromJson(requestMsg, C2CTransferDetailsVO.class);
			

			if (reqMsgObj != null) {
				transferId = reqMsgObj.getData().getTransferId();
				networkCode = reqMsgObj.getData().getNetworkCode();
				networkCodeFor = reqMsgObj.getData().getNetworkCodeFor();
				transferType = reqMsgObj.getData().getTransferType();
			}
		}
		}
		if(BTSLUtil.isNullString(transferId))
		{
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.TRF_ID_EMPTY);
		}
		if(BTSLUtil.isNullString(networkCode))
		{
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.NETWORK_CODE_EMPTY);
		}
		if(BTSLUtil.isNullString(networkCodeFor))
		{
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.NETWORK_CODE_FOR_EMPTY);
		}
		
		 List keyValueList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true);
		   HashMap<String, String> trfSubTypeMap =(HashMap<String, String>) keyValueList.stream()
		      .collect(Collectors.toMap(ListValueVO::getValue,ListValueVO::getLabel));
		
		
		
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		String trfType=null;
		boolean o2cAcknolwedgmentRequest=false;
		if(BTSLUtil.isNullString(transferType))
		{
			o2cAcknolwedgmentRequest=true;  // if transferType ="" , o2c Acknowlegment report request has come.
			trfType =	channelTransferDAO.checkTransIDTransferSubType(con,transferId);
			transferType=trfType;
		}
		channelTransferVO.setTransferID(transferId); 
		channelTransferVO.setNetworkCode(networkCode); 
		channelTransferVO.setNetworkCodeFor(networkCodeFor);
		
		channelTransferVO.setLoginID(p_requestVO.getUserLoginId());
		 if( !channelTransferDAO.viewTransactionIDAllowCheck(con, channelTransferVO) ) {
			 throw new BTSLBaseException(this, METHOD_NAME, "o2cenquiry.viewo2ctransfers.msg.notauthorize");
		 }
		
		if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(transferType)){
			if(transferId.contains("OT")) {
				channelTransferVO.setLoginID(p_requestVO.getUserLoginId());
				 if(channelTransferDAO.viewTransactionIDAllowCheck(con, channelTransferVO)) {
		     		channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
				 }else{
					 throw new BTSLBaseException(this, METHOD_NAME, "o2cenquiry.viewo2ctransfers.msg.notauthorize");	 
				 }
			}else {
				channelTransferDAO.loadChannelTransfersVOC2C(con, channelTransferVO);
			}
		
		if(BTSLUtil.isNullString(channelTransferVO.getToUserID()) || channelTransferVO.getTransferSubType().equals("V")==false){
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.INVALID_TXN);
		}
		final ArrayList<ChannelTransferItemsVO> itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con, channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
				channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
		channelTransferVO.setChannelTransferList(itemsList);
		ArrayList <ChannelVoucherItemsVO> channelVoucherItemList = new ChannelTransferDAO().loadChannelVoucherItemsList(con, channelTransferVO.getTransferID(), channelTransferVO.getTransferDate());
        channelTransferVO.setChannelVoucherItemsVoList(channelVoucherItemList);
        channelTransferVO.setChannelTransferitemsVOList(new ChannelTransferDAO().loadChannelTransferItems(con, channelTransferVO.getTransferID()));
        int length  = channelVoucherItemList.size();
        VomsCategoryVO vomsCategoryVO = new VomsCategoryVO();
        if(length>0){
        	
        	vomsCategoryVO.setVoucherType(((ChannelVoucherItemsVO)channelVoucherItemList.get(0)).getVoucherType());
        	vomsCategoryVO.setName(((ChannelVoucherItemsVO)channelVoucherItemList.get(0)).getVoucherType());
        	vomsCategoryVO.setStatus("Y");
			voucherTypeList .add(vomsCategoryVO);
        }
      
		VomsBatchVO vomsOrderVO = null; 
		VomsProductVO vo = null;
		ArrayList arList = null;
		Iterator itr = null;
		ArrayList slabslist =  new ArrayList<>();
		ChannelVoucherItemsVO voucherItemVO = null;
		for (int i = 0; i < length; i++) {
			vomsOrderVO = new VomsBatchVO();
			voucherItemVO = (ChannelVoucherItemsVO)channelVoucherItemList.get(i);
			vomsOrderVO.setSeq_id( BTSLUtil.parseLongToInt(voucherItemVO.getSNo()) );
			vomsOrderVO.setDenomination(voucherItemVO.getTransferMrp()+".0");
			vomsOrderVO.setQuantity(voucherItemVO.getRequiredQuantity()+"");
			vomsOrderVO.setFromSerialNo(voucherItemVO.getFromSerialNum());
			vomsOrderVO.setToSerialNo(voucherItemVO.getToSerialNum());
			itr = vomsProductlist.iterator();
			arList = new ArrayList();
			while(itr.hasNext()){
				vo = (VomsProductVO)itr.next();
				if(BTSLUtil.floatEqualityCheck((double) voucherItemVO.getTransferMrp(), (double) vo.getMrp(), "==")){
					arList.add(vo);
					vomsOrderVO.setProductName(vo.getProductName());
				}
			}
			vomsOrderVO.setPreQuantity(voucherItemVO.getRequiredQuantity()+"");
			vomsOrderVO.setPreFromSerialNo(voucherItemVO.getFromSerialNum());
			vomsOrderVO.setPreToSerialNo(voucherItemVO.getToSerialNum());
			vomsOrderVO.setPreProductId(voucherItemVO.getProductId());
			vomsOrderVO.setProductlist(arList);
			slabslist.add(vomsOrderVO);
		}
		channelTransferVO.setSlabslist(slabslist);
		channelTransferVO.setVoucherTypeList(voucherTypeList);
		
		
		if(!channelTransferVO.getStatus().contentEquals("CLOSE") && o2cAcknolwedgmentRequest) {//if closed then only fetch data  
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.O2CACK_TRANSACTION_NOT_CLOSED_YET);
		}
		
		//getting voucher details	
		if(channelTransferVO.getStatus().contentEquals("CLOSE") && o2cAcknolwedgmentRequest) {//if closed then only fetch data  
			ArrayList<VomsBatchVO> voucherDetails = new ChannelTransferDAO().loadVoucherDetailsForTransactionId(con, channelTransferVO.getTransferID(), channelTransferVO.getStatus());
			channelTransferVO.setVoucherDetails(voucherDetails); 
		}
		try {
			String paymentDate = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getPayInstrumentDate()));
			channelTransferVO.setPayInstrumentDateAsString(paymentDate);
		}catch(Exception e) {
			channelTransferVO.setPayInstrumentDateAsString(null);
		}
		
		try {
			String createdOn = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getCreatedOn()));
			channelTransferVO.setCreatedOnAsString(createdOn);
		}catch(Exception e) {
			channelTransferVO.setCreatedOnAsString(null);
		}
		
        if(trfSubTypeMap.get(transferType)!=null) {
        	channelTransferVO.setTransferSubTypeAsString(trfSubTypeMap.get(transferType));	
        }
		
		return channelTransferVO;
		}
		else if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(transferType) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.contentEquals(transferType)
				|| PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.contentEquals(transferType)
				|| PretupsI.CHANNEL_TRANSFER_SUB_TYPE_REVERSAL.contentEquals(transferType)
				)
		{
			
			channelTransferVO.setTransferSubType(transferType);
			
			channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
			HashSet<String> possibleSubType = Stream.of("T","R","W","X").collect(Collectors.toCollection(HashSet::new));
			if(BTSLUtil.isNullString(channelTransferVO.getToUserID()) || !possibleSubType.contains(channelTransferVO.getTransferSubType())){
				throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.INVALID_TXN);
			}
			
			if(!channelTransferVO.getStatus().contentEquals("CLOSE") && o2cAcknolwedgmentRequest) {//if closed then only fetch data  
				throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.O2CACK_TRANSACTION_NOT_CLOSED_YET);
			}
			
			channelTransferVO.setProductType(new ProductTypeDAO().getProductType(con, channelTransferVO.getProductCode()));
			final ArrayList<ChannelTransferItemsVO> itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con, channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
					channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
			channelTransferVO.setChannelTransferList(itemsList);
			channelTransferVO.set_transferMRPReplica(channelTransferVO.getTransferMRP());
			ArrayList<ChannelTransferItemsVO> channelTransferItemsVO1 = channelTransferDAO.loadChannelTransferItems(con, channelTransferVO.getTransferID());
			channelTransferVO.setChannelTransferitemsVOList(channelTransferItemsVO1);
			//setting date
			try {
				String paymentDate = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getPayInstrumentDate()));
				channelTransferVO.setPayInstrumentDateAsString(paymentDate);
			}catch(Exception e) {
				channelTransferVO.setPayInstrumentDateAsString(null);
			}
			
			try {
				String createdOn = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getCreatedOn()));
				channelTransferVO.setCreatedOnAsString(createdOn);
			}catch(Exception e) {
				channelTransferVO.setCreatedOnAsString(null);
			}
	        if(trfSubTypeMap.get(transferType)!=null) {
	        	channelTransferVO.setTransferSubTypeAsString(trfSubTypeMap.get(transferType));	
	        }

	        UserBalancesVO balancesVO = null;
	        long balance = 0;
	        ArrayList<ChannelTransferItemsVO>  itemsList1 = channelTransferVO.getChannelTransferitemsVOList();
	        final ArrayList balancesList = new UserBalancesDAO().loadUserBalanceList(con, channelTransferVO.getFromUserID(), channelTransferVO.getNetworkCode(),
	                channelTransferVO.getNetworkCodeFor());
	        for (int i = 0, k = itemsList1.size(); i < k; i++) {
	        	ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) itemsList1.get(i);
	        	if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
	        		for (int m = 0, n = balancesList.size(); m < n; m++){
	        			balancesVO = (UserBalancesVO) balancesList.get(m);
	        			if (balancesVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
	        				balance+=balancesVO.getBalance();
	        			}
	        		}
	        		channelTransferItemsVO.setSenderBalance(balance);
	        	}
	        	else{
	        		for (int m = 0, n = balancesList.size(); m < n; m++) {
	        			balancesVO = (UserBalancesVO) balancesList.get(m);
	        			if (balancesVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
	        				channelTransferItemsVO.setSenderBalance(balancesVO.getBalance());
	        			}
	        		}
	        	}

	        }
	        
			return channelTransferVO;
		}
		else
		{
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.INVALID_TXN);
		}
		}catch(BTSLBaseException be){
			_log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error(METHOD_NAME, "Exception " + e);
			_log.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException(this, METHOD_NAME,e.getMessage());
		}finally {
			if (mcomCon != null) {
				mcomCon.close("C2CVoucherTransferDetailsController#"+METHOD_NAME);
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, " Exited ");
			}
		}
	}
	
	private ChannelTransferVO viewTransferDetailsNew(RequestVO p_requestVO) throws BTSLBaseException, SQLException{

		final String METHOD_NAME = "viewTransferDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered ");
		}
		Gson gson = new Gson();
		String transferId = null;
		String networkCode = null;
		String networkCodeFor = null;
		String transferType = null;
		ArrayList vomsProductlist = new ArrayList<>();
		try{
		String requestMsg = p_requestVO.getRequestMessageOrigStr();
		ArrayList voucherTypeList =  new ArrayList<>();
		if(PretupsI.MOBILE_APP_GATEWAY.equals(p_requestVO.getRequestGatewayCode())){
		final String message[] = p_requestVO.getRequestMessageArray();
		transferId = message[3];
		transferType = message[4];
		networkCode = message[5];
		networkCodeFor = message[6];
		}
		else if (PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode()))  {
		if (requestMsg != null) {
			C2CTransferDetailsVO reqMsgObj = gson.fromJson(requestMsg, C2CTransferDetailsVO.class);
			

			if (reqMsgObj != null) {
				transferId = reqMsgObj.getData().getTransferId();
				networkCode = reqMsgObj.getData().getNetworkCode();
				networkCodeFor = reqMsgObj.getData().getNetworkCodeFor();
				transferType = reqMsgObj.getData().getTransferType();
			}
		}
		}
		if(BTSLUtil.isNullString(transferId))
		{
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.TRF_ID_EMPTY);
		}
		if(BTSLUtil.isNullString(networkCode))
		{
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.NETWORK_CODE_EMPTY);
		}
		if(BTSLUtil.isNullString(networkCodeFor))
		{
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.NETWORK_CODE_FOR_EMPTY);
		}
		
		 List keyValueList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true);
		   HashMap<String, String> trfSubTypeMap =(HashMap<String, String>) keyValueList.stream()
		      .collect(Collectors.toMap(ListValueVO::getValue,ListValueVO::getLabel));
		
		
		
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		String trfType=null;
		boolean o2cAcknolwedgmentRequest=false;
		if(BTSLUtil.isNullString(transferType))
		{
			o2cAcknolwedgmentRequest=true;  // if transferType ="" , o2c Acknowlegment report request has come.
			trfType =	channelTransferDAO.checkTransIDTransferSubType(con,transferId);
			transferType=trfType;
		}
		channelTransferVO.setTransferID(transferId); 
		channelTransferVO.setNetworkCode(networkCode); 
		channelTransferVO.setNetworkCodeFor(networkCodeFor);
		
		channelTransferVO.setLoginID(p_requestVO.getUserLoginId());
		 if( !channelTransferDAO.viewTransactionIDAllowCheckNew(con, channelTransferVO) ) {
			 throw new BTSLBaseException(this, METHOD_NAME, "o2cenquiry.viewo2ctransfers.msg.notauthorize");
		 }
		
		if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(transferType)){
			if(transferId.contains("OT")) {
				channelTransferVO.setLoginID(p_requestVO.getUserLoginId());
				 if(channelTransferDAO.viewTransactionIDAllowCheck(con, channelTransferVO)) {
		     		channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
				 }else{
					 throw new BTSLBaseException(this, METHOD_NAME, "o2cenquiry.viewo2ctransfers.msg.notauthorize");	 
				 }
			}else {
				channelTransferDAO.loadChannelTransfersVOC2C(con, channelTransferVO);
			}
		
		if(BTSLUtil.isNullString(channelTransferVO.getToUserID()) || channelTransferVO.getTransferSubType().equals("V")==false){
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.INVALID_TXN);
		}
		final ArrayList<ChannelTransferItemsVO> itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con, channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
				channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
		channelTransferVO.setChannelTransferList(itemsList);
		ArrayList <ChannelVoucherItemsVO> channelVoucherItemList = new ChannelTransferDAO().loadChannelVoucherItemsList(con, channelTransferVO.getTransferID(), channelTransferVO.getTransferDate());
        channelTransferVO.setChannelVoucherItemsVoList(channelVoucherItemList);
        channelTransferVO.setChannelTransferitemsVOList(new ChannelTransferDAO().loadChannelTransferItems(con, channelTransferVO.getTransferID()));
        int length  = channelVoucherItemList.size();
        VomsCategoryVO vomsCategoryVO = new VomsCategoryVO();
        if(length>0){
        	
        	vomsCategoryVO.setVoucherType(((ChannelVoucherItemsVO)channelVoucherItemList.get(0)).getVoucherType());
        	vomsCategoryVO.setName(((ChannelVoucherItemsVO)channelVoucherItemList.get(0)).getVoucherType());
        	vomsCategoryVO.setStatus("Y");
			voucherTypeList .add(vomsCategoryVO);
        }
      
		VomsBatchVO vomsOrderVO = null; 
		VomsProductVO vo = null;
		ArrayList arList = null;
		Iterator itr = null;
		ArrayList slabslist =  new ArrayList<>();
		ChannelVoucherItemsVO voucherItemVO = null;
		for (int i = 0; i < length; i++) {
			vomsOrderVO = new VomsBatchVO();
			voucherItemVO = (ChannelVoucherItemsVO)channelVoucherItemList.get(i);
			vomsOrderVO.setSeq_id( BTSLUtil.parseLongToInt(voucherItemVO.getSNo()) );
			vomsOrderVO.setDenomination(voucherItemVO.getTransferMrp()+".0");
			vomsOrderVO.setQuantity(voucherItemVO.getRequiredQuantity()+"");
			vomsOrderVO.setFromSerialNo(voucherItemVO.getFromSerialNum());
			vomsOrderVO.setToSerialNo(voucherItemVO.getToSerialNum());
			itr = vomsProductlist.iterator();
			arList = new ArrayList();
			while(itr.hasNext()){
				vo = (VomsProductVO)itr.next();
				if(BTSLUtil.floatEqualityCheck((double) voucherItemVO.getTransferMrp(), (double) vo.getMrp(), "==")){
					arList.add(vo);
					vomsOrderVO.setProductName(vo.getProductName());
				}
			}
			vomsOrderVO.setPreQuantity(voucherItemVO.getRequiredQuantity()+"");
			vomsOrderVO.setPreFromSerialNo(voucherItemVO.getFromSerialNum());
			vomsOrderVO.setPreToSerialNo(voucherItemVO.getToSerialNum());
			vomsOrderVO.setPreProductId(voucherItemVO.getProductId());
			vomsOrderVO.setProductlist(arList);
			slabslist.add(vomsOrderVO);
		}
		channelTransferVO.setSlabslist(slabslist);
		channelTransferVO.setVoucherTypeList(voucherTypeList);
		
		
		if(!channelTransferVO.getStatus().contentEquals("CLOSE") && o2cAcknolwedgmentRequest) {//if closed then only fetch data  
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.O2CACK_TRANSACTION_NOT_CLOSED_YET);
		}
		
		//getting voucher details	
		if(channelTransferVO.getStatus().contentEquals("CLOSE") && o2cAcknolwedgmentRequest) {//if closed then only fetch data  
			ArrayList<VomsBatchVO> voucherDetails = new ChannelTransferDAO().loadVoucherDetailsForTransactionId(con, channelTransferVO.getTransferID(), channelTransferVO.getStatus());
			channelTransferVO.setVoucherDetails(voucherDetails); 
		}
		try {
			String paymentDate = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getPayInstrumentDate()));
			channelTransferVO.setPayInstrumentDateAsString(paymentDate);
		}catch(Exception e) {
			channelTransferVO.setPayInstrumentDateAsString(null);
		}
		
		try {
			String createdOn = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getCreatedOn()));
			channelTransferVO.setCreatedOnAsString(createdOn);
		}catch(Exception e) {
			channelTransferVO.setCreatedOnAsString(null);
		}
		
        if(trfSubTypeMap.get(transferType)!=null) {
        	channelTransferVO.setTransferSubTypeAsString(trfSubTypeMap.get(transferType));	
        }
		
		return channelTransferVO;
		}
		else if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(transferType) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.contentEquals(transferType)
				|| PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.contentEquals(transferType)
				|| PretupsI.CHANNEL_TRANSFER_SUB_TYPE_REVERSAL.contentEquals(transferType)
				)
		{
			
			channelTransferVO.setTransferSubType(transferType);
			
			channelTransferDAO.loadChannelTransfersVO(con, channelTransferVO);
			HashSet<String> possibleSubType = Stream.of("T","R","W","X").collect(Collectors.toCollection(HashSet::new));
			if(BTSLUtil.isNullString(channelTransferVO.getToUserID()) || !possibleSubType.contains(channelTransferVO.getTransferSubType())){
				throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.INVALID_TXN);
			}
			
			if(!channelTransferVO.getStatus().contentEquals("CLOSE") && o2cAcknolwedgmentRequest) {//if closed then only fetch data  
				throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.O2CACK_TRANSACTION_NOT_CLOSED_YET);
			}
			
			channelTransferVO.setProductType(new ProductTypeDAO().getProductType(con, channelTransferVO.getProductCode()));
			final ArrayList<ChannelTransferItemsVO> itemsList = ChannelTransferBL.loadChannelTransferItemsWithBalances(con, channelTransferVO.getTransferID(), channelTransferVO.getNetworkCode(),
					channelTransferVO.getNetworkCodeFor(), channelTransferVO.getToUserID());
			channelTransferVO.setChannelTransferList(itemsList);
			channelTransferVO.set_transferMRPReplica(channelTransferVO.getTransferMRP());
			ArrayList<ChannelTransferItemsVO> channelTransferItemsVO1 = channelTransferDAO.loadChannelTransferItems(con, channelTransferVO.getTransferID());
			channelTransferVO.setChannelTransferitemsVOList(channelTransferItemsVO1);
			//setting date
			try {
				String paymentDate = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getPayInstrumentDate()));
				channelTransferVO.setPayInstrumentDateAsString(paymentDate);
			}catch(Exception e) {
				channelTransferVO.setPayInstrumentDateAsString(null);
			}
			
			try {
				String createdOn = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(channelTransferVO.getCreatedOn()));
				channelTransferVO.setCreatedOnAsString(createdOn);
			}catch(Exception e) {
				channelTransferVO.setCreatedOnAsString(null);
			}
	        if(trfSubTypeMap.get(transferType)!=null) {
	        	channelTransferVO.setTransferSubTypeAsString(trfSubTypeMap.get(transferType));	
	        }

	        UserBalancesVO balancesVO = null;
	        long balance = 0;
	        ArrayList<ChannelTransferItemsVO>  itemsList1 = channelTransferVO.getChannelTransferitemsVOList();
	        final ArrayList balancesList = new UserBalancesDAO().loadUserBalanceList(con, channelTransferVO.getFromUserID(), channelTransferVO.getNetworkCode(),
	                channelTransferVO.getNetworkCodeFor());
	        for (int i = 0, k = itemsList1.size(); i < k; i++) {
	        	ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) itemsList1.get(i);
	        	if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
	        		for (int m = 0, n = balancesList.size(); m < n; m++){
	        			balancesVO = (UserBalancesVO) balancesList.get(m);
	        			if (balancesVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
	        				balance+=balancesVO.getBalance();
	        			}
	        		}
	        		channelTransferItemsVO.setSenderBalance(balance);
	        	}
	        	else{
	        		for (int m = 0, n = balancesList.size(); m < n; m++) {
	        			balancesVO = (UserBalancesVO) balancesList.get(m);
	        			if (balancesVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
	        				channelTransferItemsVO.setSenderBalance(balancesVO.getBalance());
	        			}
	        		}
	        	}

	        }
	        
			return channelTransferVO;
		}
		else
		{
			throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.INVALID_TXN);
		}
		}catch(BTSLBaseException be){
			_log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
			throw be;
		} catch (Exception e) {
			_log.error(METHOD_NAME, "Exception " + e);
			_log.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException(this, METHOD_NAME,e.getMessage());
		}finally {
			if (mcomCon != null) {
				mcomCon.close("C2CVoucherTransferDetailsController#"+METHOD_NAME);
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, " Exited ");
			}
		}
	
	}
	
	@Override
	public void process(RequestVO p_requestVO) {
		final String METHOD_NAME = "process";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered ");
		}
		try {
			ChannelTransferVO channelTVO = new ChannelTransferVO();
			channelTVO = viewTransferDetails(p_requestVO);
			p_requestVO.setChannelTransferVO(channelTVO);
			p_requestVO.setSenderReturnMessage("Successful!!");
	        p_requestVO.setSuccessTxn(true);
	        p_requestVO.setMessageCode("8141");
			
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(METHOD_NAME, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + be.getMessage());
			if (be.getMessageList() != null && be.getMessageList().size() > 0) {
				final String[] array = {
						BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
				p_requestVO.setMessageArguments(array);
			}
			if (be.getArgs() != null) {
				p_requestVO.setMessageArguments(be.getArgs());
			}

			if (be.getMessageKey() != null) {
				p_requestVO.setMessageCode(be.getMessageKey());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			}
			_log.errorTrace(METHOD_NAME, be);
			return;

		} catch (Exception e) {

			p_requestVO.setSenderReturnMessage("Transaction has been failed!");
			p_requestVO.setSuccessTxn(false);

			_log.error(METHOD_NAME, "Exception " + e);
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(METHOD_NAME, "SQLException : ", esql.getMessage());
			}
			finally {
				if (mcomCon != null) {
					mcomCon.close("C2CTrfInitiateController#process");
					mcomCon = null;
				}
				if (_log.isDebugEnabled()) {
					_log.debug("process", " Exited ");
				}
			}
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CVoucherTransferDetailsController[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);

			return;
		}

	}
	
	
	public RequestVO process1(RequestVO p_requestVO) {
		final String METHOD_NAME = "process";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered ");
		}
		try {
			ChannelTransferVO channelTVO = new ChannelTransferVO();
			channelTVO = viewTransferDetailsNew(p_requestVO);
			p_requestVO.setChannelTransferVO(channelTVO);
			p_requestVO.setSenderReturnMessage("Successful!!");
	        p_requestVO.setSuccessTxn(true);
	        p_requestVO.setMessageCode("8141");
			return p_requestVO;
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(METHOD_NAME, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + be.getMessage());
			if (be.getMessageList() != null && be.getMessageList().size() > 0) {
				final String[] array = {
						BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
				p_requestVO.setMessageArguments(array);
			}
			if (be.getArgs() != null) {
				p_requestVO.setMessageArguments(be.getArgs());
			}

			if (be.getMessageKey() != null) {
				p_requestVO.setMessageCode(be.getMessageKey());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			}
			_log.errorTrace(METHOD_NAME, be);
			return p_requestVO;

		} catch (Exception e) {

			p_requestVO.setSenderReturnMessage("Transaction has been failed!");
			p_requestVO.setSuccessTxn(false);

			_log.error(METHOD_NAME, "Exception " + e);
			_log.errorTrace(METHOD_NAME, e);
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(METHOD_NAME, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CVoucherTransferDetailsController[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);

			return p_requestVO;
		}

	}

}
