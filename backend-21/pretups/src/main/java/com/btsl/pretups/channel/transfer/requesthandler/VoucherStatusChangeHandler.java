package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchesDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VoucherStatusChangeHandler implements ServiceKeywordControllerI
{
	private static Log _log = LogFactory.getLog(VoucherStatusChangeHandler.class.getName());

	public VoucherStatusChangeHandler()
	{

	}



	public void process(RequestVO p_requestVO) 
	{
		Connection con=null;MComConnectionI mcomCon = null;
		HashMap<String,String> responseMap=new HashMap<String,String>();
		final String methodName = "process";
		VomsVoucherDAO vomsVoucherDAO= new VomsVoucherDAO();
		_log.debug(methodName,p_requestVO.getRequestIDStr(),"Entered for Request ID="+p_requestVO.getRequestID()+" MSISDN="+p_requestVO.getFilteredMSISDN());
		String decryptedMessage=p_requestVO.getDecryptedMessage();
		String []reqArr=null;
		VomsVoucherVO voucherVO=null;
		VomsUtil _vomsUtil=null;
		String batchNo=null;
		String batchNumber=null;
		VomsBatchVO batchVO=null;
		VomsBatchesDAO batchesDAO=null;
		VomsVoucherVO fromVoucherVO=null;
		String networkCode=null;
		String reasonForChange=null;
		HashMap requestMap=null;
		
		try
		{
			String sep=" ";
			if((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR)!=null)
				sep=(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);

			reqArr=decryptedMessage.split(sep);
			requestMap=p_requestVO.getRequestMap();
			networkCode=(String)requestMap.get("EXTNWCODE");
			reasonForChange=(String)requestMap.get("STATE_CHANGE_REASON");
			responseMap.put(VOMSI.EXTERNAL_NETWORKCODE,networkCode);
			
			
			mcomCon = new MComConnection();con=mcomCon.getConnection();

			_log.debug(methodName,p_requestVO.getRequestIDStr(),"validate Request format");

			//validateRequestFormat(p_requestVO);

			voucherVO=new VomsVoucherVO();
			voucherVO.set_fromSerialNo(reqArr[1]);
			voucherVO.setSerialNo(reqArr[1]);
			voucherVO.setToSerialNo(reqArr[2]);
			voucherVO.setStatus(reqArr[3]);
			

			long requestedVoucherCount=0;
			try{
				if(reasonForChange!=null && reasonForChange.length()>100) 
				{
					_log.debug(methodName,p_requestVO.getRequestIDStr(),"Reason For change cannot be more than of 100 chars.");
					responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
					responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
					responseMap.put(VOMSI.PRE_STATUS,"");
					responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
					responseMap.put(VOMSI.PRE_STATUS,"");
					responseMap.put(VOMSI.MESSAGE_TAG,"Reason For change cannot be more than of 100 chars");
					responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
					responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
					throw new BTSLBaseException("FAILED");
					
				}
				if(Long.parseLong(voucherVO.get_fromSerialNo()) > Long.parseLong(voucherVO.getToSerialNo()))
				{
					_log.debug(methodName,p_requestVO.getRequestIDStr(),"From Serial Number cannot be greater than To Serial Number");
					responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
					responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
					responseMap.put(VOMSI.PRE_STATUS,"");
					responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
					responseMap.put(VOMSI.PRE_STATUS,"");
					responseMap.put(VOMSI.MESSAGE_TAG,"From Serial Number cannot be greater than To Serial Number");
					responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
					responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
					throw new BTSLBaseException("FAILED");
				}

				requestedVoucherCount=Long.parseLong(voucherVO.getToSerialNo())-Long.parseLong(voucherVO.get_fromSerialNo())+1;
				
				if(requestedVoucherCount>Long.parseLong(Constants.getProperty("VOMS_CHANGE_GEN_STATUS_ONLINE_COUNT")))
				{
					//log.debug(methodName,p_requestVO.getRequestIDStr(),"No. of vouchers exceeds allowed limit for online status change.");
					responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
					responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
					responseMap.put(VOMSI.PRE_STATUS,"");
					responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
					responseMap.put(VOMSI.PRE_STATUS,"");
					responseMap.put(VOMSI.MESSAGE_TAG,"No. of vouchers exceeds allowed limit for online status change");
					responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
					responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
					throw new BTSLBaseException("FAILED");
					
				}
				
			}catch (Exception e) {
				_log.error(methodName, "Exception " + e);
				_log.errorTrace(methodName, e);
				_log.debug(methodName,p_requestVO.getRequestIDStr(),"From/To Serial Number is invalid");
				responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
				responseMap.put(VOMSI.PRE_STATUS,"");
				responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,"From/To Serial Number is invalid");
				responseMap.put(VOMSI.PRE_STATUS,"");
				responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_INVALID);
				responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
				throw new BTSLBaseException("FAILED");
			}
			boolean statusFlag=vomsVoucherDAO.validateVoucherStatus(con,reqArr[3]);
			if(!statusFlag)
			{
				_log.debug(methodName,p_requestVO.getRequestIDStr(),"Voucher Status not valid");
				responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
				responseMap.put(VOMSI.PRE_STATUS,"");
				responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,"Voucher Status not valid");
				responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_VOMS_STATUS_INVALID);
				responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
				throw new BTSLBaseException("FAILED");

			}


			_log.debug(methodName,p_requestVO.getRequestIDStr(),"Get Voucher Details");

			fromVoucherVO=vomsVoucherDAO.getVoucherDetails(con,reqArr[1],networkCode);

			if(fromVoucherVO==null)
			{
				_log.debug(methodName,p_requestVO.getRequestIDStr(),"From Serial Number is invalid or belongs to different network.");
				responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
				responseMap.put(VOMSI.PRE_STATUS,"");
				responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,"From Serial Number is invalid or belongs to different network.");
				responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_FROM_SERIALNO_INVALID);
				responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
				throw new BTSLBaseException("FAILED");
			}

			if(voucherVO.getStatus().equalsIgnoreCase(fromVoucherVO.getCurrentStatus()))
			{
				_log.debug(methodName,p_requestVO.getRequestIDStr(),"From Serail Number is already in requested Status"+voucherVO.getStatus());
				responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
				responseMap.put(VOMSI.PRE_STATUS,fromVoucherVO.getCurrentStatus());
				responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,"From Serail Number is already in requested Status");
				responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				throw new BTSLBaseException("FAILED");
			}

			_log.debug(methodName,p_requestVO.getRequestIDStr(),"Validate all voucher Details");
			long dbVoucherCount=vomsVoucherDAO.validateAllVoucherDetails(con,voucherVO,fromVoucherVO);
			if(requestedVoucherCount!=dbVoucherCount){
				_log.debug(methodName,p_requestVO.getRequestIDStr(),"Some of serial number are either invalid or having diffrent status");
				responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
				responseMap.put(VOMSI.PRE_STATUS,fromVoucherVO.getCurrentStatus());
				responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,"Some of serial number are either invalid or having diffrent status");
				responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_VOMS_STATUS_FROM_TO_SERIALNO_DIFF);
				responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
				throw new BTSLBaseException("FAILED");
			}

			//statusFlag=false;
			_log.debug(methodName,p_requestVO.getRequestIDStr(),"Before checking status mapping");
			long dbVoucherstatusmapping=vomsVoucherDAO.validateStatusMapping(con,voucherVO.getStatus(),fromVoucherVO.getCurrentStatus());
			if(dbVoucherstatusmapping<=0)
			{
				_log.debug(methodName,p_requestVO.getRequestIDStr(),"Voucher Status cannot changed to "+ voucherVO.getStatus() + " from status "+fromVoucherVO.getCurrentStatus());
				responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
				responseMap.put(VOMSI.PRE_STATUS,fromVoucherVO.getCurrentStatus());
				responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,"Voucher Status cannot changed to "+ voucherVO.getStatus() + " from status "+fromVoucherVO.getCurrentStatus());
				responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_VOMS_CUR_REQ_STATUS_MAPPING_INVALID);
				responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
				throw new BTSLBaseException("FAILED");
			}
			Date utilDate=new Date();
			batchVO=new VomsBatchVO();
			batchVO.setCreatedDate(BTSLUtil.getSQLDateFromUtilDate(utilDate));
			batchVO.setCreatedOn(BTSLUtil.getTimestampFromUtilDate(utilDate));
			batchVO.setModifiedOn(batchVO.getCreatedOn());
			batchVO.setBatchType(voucherVO.getStatus());
			//batchVO.setMessage(reasonForChange);

			batchNumber = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
			_vomsUtil =  new VomsUtil();
			batchNo=_vomsUtil.formatVomsBatchID(batchVO,batchNumber);

			_log.debug(methodName,p_requestVO.getRequestIDStr(),"batchNo="+batchNo);
			  			
			batchVO.setLocationCode(fromVoucherVO.getUserLocationCode());
			batchVO.setFromSerialNo(voucherVO.get_fromSerialNo());
			batchVO.setToSerialNo(voucherVO.getToSerialNo());
			batchVO.setProductID(fromVoucherVO.getProductID());
			batchVO.setCreatedBy(TypesI.SYSTEM_USER);
			batchVO.setNoOfVoucher(dbVoucherCount);
			batchVO.setReferenceNo(fromVoucherVO.get_batch_no());
			batchVO.setModifiedBy(TypesI.SYSTEM_USER);
			batchVO.setStatus(VOMSI.EXECUTED);
			batchVO.setSuccessCount(dbVoucherCount);
			batchVO.setFailCount(0);
			batchVO.setProcess(VOMSI.BATCH_PROCESS_CHANGE);
			batchVO.setReferenceType(fromVoucherVO.getCurrentStatus());
			batchVO.setTotalVoucherPerOrder(0);
			batchVO.setMessage("Batch SuccessFully Executed ...........");
			batchVO.setLocationCode(fromVoucherVO.getUserLocationCode());
			batchVO.setBatchNo(batchNo);
			voucherVO.SetSaleBatchNo(batchNo);
			voucherVO.setOtherInfo(reasonForChange);
			
			if(reasonForChange!=null && reasonForChange.length()>100) {
				responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
				responseMap.put(VOMSI.PRE_STATUS,fromVoucherVO.getCurrentStatus());
				responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,"Reason length is more than 100 Chars");
				responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_VOMS_CUR_REQ_STATUS_MAPPING_INVALID);
				responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
				throw new BTSLBaseException("FAILED");
				
			}
			
			
			batchVO.setDownloadCount(0);
			if(voucherVO.getStatus().equalsIgnoreCase(VOMSI.VOUCHER_ENABLE) || voucherVO.getStatus().equalsIgnoreCase(VOMSI.VOMS_PRE_ACTIVE_STATUS))
				voucherVO.setEnableBatchNo(batchNo);
			else
				voucherVO.setEnableBatchNo(fromVoucherVO.getEnableBatchNo());

			ArrayList returnBatchList=new ArrayList();
			returnBatchList.add(batchVO);
			
			ArrayList voucherList=new ArrayList();
			voucherList.add(voucherVO);
			
			batchesDAO = new VomsBatchesDAO();
			//add the new  batch
			_log.debug(methodName,"Before add Batch");

			int addCount=batchesDAO.addBatch(con,returnBatchList);

			if(addCount>0)
			{
				_log.debug(methodName,"Before voucher Status Change");
				
				long updateVoucherCount=vomsVoucherDAO.changeVoucherStatus(con,batchVO,voucherVO);
				if(updateVoucherCount!=requestedVoucherCount){
					mcomCon.finalRollback();
					_log.debug(methodName," Some of serial number are either invalid or having diffrent status");
					responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
					responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
					responseMap.put(VOMSI.PRE_STATUS,fromVoucherVO.getCurrentStatus());
					responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
					responseMap.put(VOMSI.MESSAGE_TAG,"Some of serial number are either invalid or having diffrent status");
					responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_VOMS_STATUS_FROM_TO_SERIALNO_DIFF);
					responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
					throw new BTSLBaseException("FAILED");
				}
				_log.debug(methodName," voucher Status Change sucessfuly");
				mcomCon.finalCommit();
				responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
				responseMap.put(VOMSI.PRE_STATUS,fromVoucherVO.getCurrentStatus());
				responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,"SUCCESS");
				responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.TXN_STATUS_SUCCESS);

				p_requestVO.setResponseMap(responseMap);

				VomsBatchInfoLog.addBatchLog(returnBatchList);

				if(returnBatchList!=null && !returnBatchList.isEmpty())
				{
					voucherVO.setPreviousStatus(fromVoucherVO.getCurrentStatus());
					voucherVO.setVoucherStatus(voucherVO.getStatus());
					voucherVO.setProductionLocationCode(fromVoucherVO.getProductionLocationCode());
					voucherVO.setPrevStatusModifiedBy("SU001");
					voucherVO.setStatusChangeSource("API");
					voucherVO.setMRP(fromVoucherVO.getMRP());
					voucherVO.setExpiryDateStr(fromVoucherVO.getExpiryDateStr());
					voucherVO.setPrevStatusModifiedOn(fromVoucherVO.getPrevStatusModifiedOn());
					voucherVO.setProcess("ChangeStatus");
					voucherVO.setLastErrorMessage(fromVoucherVO.getLastErrorMessage());
					VomsVoucherChangeStatusLog.log(voucherList);
				}

			}
			else
			{
				mcomCon.finalRollback();
				_log.debug(methodName,"Not able to add in batch");
				responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
				responseMap.put(VOMSI.PRE_STATUS,fromVoucherVO.getCurrentStatus());
				responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,"Not able to add in batch");
				responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_VOMS_ERROR);
				responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
				throw new BTSLBaseException("FAILED");
			}				
		}
		catch(BTSLBaseException be)
		{
			try {
				mcomCon.finalRollback();
			} catch (Exception e) {
				_log.error(methodName, "Exception:e=" + e);
				_log.errorTrace(methodName, e);
				
			}
			if(!be.getMessage().equalsIgnoreCase("FAILED"))
			{
				responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
				responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
				responseMap.put(VOMSI.PRE_STATUS,fromVoucherVO.getCurrentStatus());
				responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
				responseMap.put(VOMSI.MESSAGE_TAG,"Not able to Change Voucher Status");
				responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_VOMS_ERROR);
				responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
			}
			p_requestVO.setResponseMap(responseMap);
			_log.error("processRequest", "BTSLBaseException"+be);
		}
		catch (Exception e)
		{
			//added while Voucher Retrieval RollBack Request
			try {
				mcomCon.finalRollback();
			} catch (Exception ex) {
				_log.errorTrace(methodName, ex);
			}

			responseMap.put(VOMSI.FROM_SERIAL_NO,reqArr[1]);
			responseMap.put(VOMSI.TO_SERIAL_NO,reqArr[2]);
			responseMap.put(VOMSI.PRE_STATUS,fromVoucherVO.getCurrentStatus());
			responseMap.put(VOMSI.REQ_STATUS,reqArr[3]);
			responseMap.put(VOMSI.MESSAGE_TAG,"Not able to Change Voucher Status");
			responseMap.put(VOMSI.ERROR_TAG,PretupsErrorCodesI.ERROR_VOMS_ERROR);
			responseMap.put(VOMSI.TXNSTATUS_TAG,PretupsErrorCodesI.ERROR_FROM_TO_SERIALNO_RANGE_INVALID);
			p_requestVO.setResponseMap(responseMap);
			_log.error("processRequest", "Exception"+e);
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("VoucherStatusChangeHandler#process");
				mcomCon = null;
			}
		}
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Exiting");
		}


	}//end of finally

	



	private void validateRequestFormat(RequestVO p_requestVO) throws BTSLBaseException
	{
		String obj="validateRequestFormat";

		if(!(p_requestVO.getRequestMessageArray().length==4))
			throw new BTSLBaseException(this,obj,PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT);

	}


}
	