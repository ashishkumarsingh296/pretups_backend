package com.inter.hcpt.voucherconsumption;

/*
 * @(#)EVDHandler.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Zeeshan Aleem 04/12/2014 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * Controller class for Voucher Management System.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.inter.util.VOMSProductVO;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.inter.util.VOMSVoucherVO;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.util.OracleUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.util.VomsUtil;

public class VchrConsHandler implements InterfaceHandler {
	private static final Log LOG = LogFactory.getLog(VchrConsHandler.class.getName());
	private HashMap _requestMap = null;
	private String _inTXNID = null;
	private String _msisdn = null;
	private String _referenceID = null;
	private String _interfaceID = null;// Contains the interfaceID
	private String _userType = null;
	private static SimpleDateFormat _sdf = new SimpleDateFormat("yyMMddHHmm");
	private static int _transactionIDCounter = 0;
	private static int _prevMinut = 0;
	private int _ambgMaxRetryCount = 0;
	private boolean _isRetryRequest = false;
	private int _ambgCurrentRetryCount = 0;
	// private NodeCloser _nodeCloser =null;
	private HttpURLConnection _urlConnection = null;
	private HashMap<String, String> _responseMap = null;// Contains the response
	// parameter as key and
	// value pair.
	private static VchrConsRequestFormatter _VchrConsRequestFormatter = null;
	static {
		if (LOG.isDebugEnabled())
			LOG.debug("VchrConsHandler[static]", "Entered");
		try {
			_VchrConsRequestFormatter = new VchrConsRequestFormatter();
		} catch (Exception e) {
			LOG.errorTrace("Static", e);
			LOG.error("VchrConsHandler[static]", "While instantiation of CS5MobinilRequestFormatter get Exception e::" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS5MobililINHandler[static]", "", "", "", "While instantiation of CS5MobinilRequestFormatter get Exception e::" + e.getMessage());
		} finally {
			if (LOG.isDebugEnabled())
				LOG.debug("CS5MobililINHandler[static]", "Exited");
		}
	}

	public VchrConsHandler() {
		super();
	}

	/**
	 * validate Method is used for getting the pin and serial number
	 * 
	 * @param p_map
	 *            HashMap
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	public void validate(HashMap p_map) throws BTSLBaseException, Exception {
		String methodName = "validate";
		if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered " + InterfaceUtil.getPrintMap(p_map));
		Connection con = null;
		try {
			_requestMap = p_map;
			_inTXNID = InterfaceUtil.getINTransactionID();
			_requestMap.put("IN_TXN_ID", _inTXNID);
			String networkCode = (String) _requestMap.get("NETWORK_CODE");
			_interfaceID = (String) _requestMap.get("INTERFACE_ID");		

			String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
	            // String validateRequired="Y";
	            if ("N".equals(validateRequired)) {
	                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);	                
	                _requestMap.put("SERVICE_CLASS",PretupsI.ALL );
	                return;
	            }

			Date currDate = InterfaceUtil.getDateTimeFromDateString((String) _requestMap.get("TRANSFER_DATE"));
			// long
			// requestedAmt=Long.parseLong((String)_requestMap.get("INTERFACE_AMOUNT"));
			String currentStatus = (String) _requestMap.get("UPDATE_STATUS");
			String modifiedBy = (String) _requestMap.get("SENDER_USER_ID");
			String statusChangeSource = (String) _requestMap.get("SOURCE");
			String interfaceID = (String) _requestMap.get("INTERFACE_ID");
			String selector = (String) _requestMap.get("CARD_GROUP_SELECTOR");
			String type = (String) _requestMap.get("SEL_TYPE");

			ArrayList transferIdList = new ArrayList();
			transferIdList.add((String) _requestMap.get("TRANSACTION_ID"));

			// Load the active profiles for specified network for current date
			VOMSVoucherDAO vomsDAO = new VOMSVoucherDAO();
			con = VomsUtil.getSingleConnection(FileCache.getValue(interfaceID, "datasourceurl"), FileCache.getValue(interfaceID, "userid"), FileCache.getValue(interfaceID, "passwd"));

			// String timeStampCheck=FileCache.getValue(interfaceID,
			// "TIME_STAMP_CHK");
			//boolean timeStmpChk = false;
			//HashMap profileMap = null;
			String serialNo = null;
			if (_requestMap.get("SERIALNUMBER") != null)
				serialNo = _requestMap.get("SERIALNUMBER").toString();
			ArrayList voucherList = vomsDAO.validateVoucherCodeNew(con, _requestMap.get("VOUCHER_CODE").toString(), serialNo, modifiedBy, currDate, statusChangeSource, currentStatus, transferIdList, (String) _requestMap.get("NETWORK_CODE"));
			VOMSVoucherVO vomsVO = (VOMSVoucherVO) voucherList.get(0);
			long requestedAmt = vomsVO.getPayableAmount();
			/*
			 * if (SystemPreferences.PRIVATE_RECHARGE_ALLOWED) profileMap =
			 * vomsDAO.loadActiveProfilesForPrivateRecharge(con, networkCode, currDate,
			 * timeStmpChk,"VCN"); else profileMap = vomsDAO.loadActiveProfiles(con,
			 * networkCode, currDate, timeStmpChk);

            VOMSProductVO productVO = null;
			 */ 

			if (LOG.isDebugEnabled())
			{
				LOG.debug(methodName, "Entered requestedAmt " + requestedAmt);
				LOG.debug(methodName, "Entered vomsVO " + vomsVO);


			}

			/*            

			 * if (SystemPreferences.PRIVATE_RECHARGE_ALLOWED) productVO = (VOMSProductVO)
			 * profileMap.get(String.valueOf(requestedAmt) + "_" + selector); else productVO
			 * = (VOMSProductVO) profileMap.get(String.valueOf(requestedAmt));

            if (productVO == null) {
                throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_MRP_REQUESTED);
            }
            // Get the PIN & serial number details of voucher. Only voucher with
            // status enabled will
            // be picked from VOMS_VOUCHERS table.
            // ArrayList voucherList=
            // VOMSVoucherDAO.loadPINAndSerialNumber(con,productVO,modifiedBy,currDate,statusChangeSource,currentStatus,transferIdList,(String)_requestMap.get("NETWORK_CODE"),1);
			 */
			if (vomsDAO.insertDetailsInVoucherAudit(con, vomsVO) > 0)
				con.commit();
			else {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDHandler[validate]", "", "", networkCode, "Not able to add Serial No=" + vomsVO.getSerialNo() + " in voucher history tables");
			}

			// Set the transaction status and other details in map
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
			// _requestMap.put("PRODUCT_ID", productVO.getProductID());
			_requestMap.put("SERIAL_NUMBER", vomsVO.getSerialNo());
			_requestMap.put("UPDATE_STATUS", vomsVO.getCurrentStatus());
			_requestMap.put("PREVIOUS_STATUS", vomsVO.getPreviousStatus());
			// _requestMap.put("TALK_TIME", String.valueOf(productVO.getTalkTime()));
			// _requestMap.put("VALIDITY", String.valueOf(productVO.getValidity()));
			_requestMap.put("PAYABLE_AMT", String.valueOf(requestedAmt));
			_requestMap.put("PIN", vomsVO.getPinNo());
			_requestMap.put("SERVICE_CLASS", BTSLUtil.NullToString(FileCache.getValue(interfaceID, "SERVICE_CLASS")));

			if (!SystemPreferences.PAYAMT_MRP_SAME)
				_requestMap.put("RECEIVER_PAYABLE_AMT", String.valueOf(vomsVO.getPayableAmount()));

		} catch (BTSLBaseException be) {
			LOG.errorTrace(methodName, be);
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception e) {
					LOG.errorTrace(methodName, be);
				}
			}
			throw be;
		} catch (Exception e) {
			LOG.errorTrace(methodName, e);
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception e1) {
					LOG.errorTrace(methodName, e1);
				}
			}
			LOG.error(methodName, "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate in =" + _requestMap.get("INTERFACE_ID") + " Exception =" + e.getMessage());
			throw e;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
			}
			if (LOG.isDebugEnabled())
				LOG.debug(methodName, "Exited _requestMap=" + _requestMap);
		}
	}

	/**
	 * credit Method is used for updating the status to consume in voms_vouches
	 * table.
	 * 
	 * @param p_map
	 *            HashMap
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	public void credit(HashMap p_map) throws BTSLBaseException, Exception {
		String methodName = "credit";

		if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered p_requestMap: " + p_map);
		double systemAmtDouble = 0;
		double multFactorDouble = 0;
		String amountStr = null;
		_requestMap = p_map;
		String transactionCode = null;
		String transactionType = null;
		try {
			_interfaceID = (String) _requestMap.get("INTERFACE_ID");
			transactionCode = FileCache.getValue(_interfaceID, "TRANSACTION_CODE_RF");
			transactionType = FileCache.getValue(_interfaceID, "TRANSACTION_TYPE_RF");
			_inTXNID = getINTransactionID(_requestMap);
			_requestMap.put("IN_RECON_ID", _inTXNID);
			_requestMap.put("IN_TXN_ID", _inTXNID);
			_referenceID = (String) _requestMap.get("TRANSACTION_ID");
			_msisdn = (String) _requestMap.get("MSISDN");
			_userType = (String) _requestMap.get("USER_TYPE");

			String creditRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("INTERFACE_ACTION"));
			if(!BTSLUtil.isNullString(creditRequired) && !"N".equals(creditRequired))
			{


				if (!InterfaceUtil.isNullString(transactionCode))
					_requestMap.put("TRANSACTION_CODE_RF", transactionCode);

				if (!InterfaceUtil.isNullString(transactionType))
					_requestMap.put("TRANSACTION_TYPE_RF", transactionType);
				// Fetching the MULT_FACTOR from the INFile.
				// While sending the amount to IN, it would be multiplied by this
				// factor, and recieved balance would be devided by this factor.
				String multFactor = FileCache.getValue(_interfaceID, "MULT_FACTOR");
				if (LOG.isDebugEnabled())
					LOG.debug("credit", "multFactor:" + multFactor);
				if (InterfaceUtil.isNullString(multFactor)) {
					LOG.error("credit", "MULT_FACTOR  is not defined in the INFile");
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,methodName, _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
					throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				multFactor = multFactor.trim();
				_requestMap.put("MULT_FACTOR", multFactor);
				setInterfaceParameters();// Set the interface parameters into
				// requestMap
				_requestMap.put("SYSTEM_STATUS_MAPPING", "SYSTEM_STATUS_MAPPING_CREDIT");

				try {
					multFactorDouble = Double.parseDouble(multFactor);
					double interfaceAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
					systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);
					amountStr = String.valueOf(systemAmtDouble);
					// Based on the INFiles ROUND_FLAG flag, we have to decide to
					// round the transfer amount or not.
					String roundFlag = FileCache.getValue(_interfaceID, "ROUND_FLAG");
					if (LOG.isDebugEnabled())
						LOG.debug(methodName, "From file cache roundFlag = " + roundFlag);
					// If the ROUND_FLAG is not defined in the INFile
					if (InterfaceUtil.isNullString(roundFlag)) {
						roundFlag = "Y";
						EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "methodName", _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
					}
					// If rounding of amount is allowed, round the amount value and
					// put this value in request map.
					if ("Y".equals(roundFlag.trim())) {
						amountStr = String.valueOf(Math.round(systemAmtDouble));
						_requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
					}
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
					LOG.error(methodName, "Exception e:" + e.getMessage());
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "methodName", "REFERENCE ID = " + _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
					throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
				}
				if (LOG.isDebugEnabled())
					LOG.debug(methodName, "transfer_amount:" + amountStr);
				// set transfer_amount in request map as amountStr (which is round
				// value of INTERFACE_AMOUNT)
				_requestMap.put("transfer_amount", amountStr);
				// key value of requestMap is formatted into XML string for the
				// validate request.
				String inStr = _VchrConsRequestFormatter.generateRequest(VchrConsI.ACTION_RECHARGE_CREDIT, _requestMap);
				try {
					_ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID, "CREDIT_RETRY_CNT"));
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
					_ambgMaxRetryCount = 1;
				}
				// sending the Re-charge request to IN along with re-charge action
				// defined in CS5MobililI interface
				sendRequestToIN(inStr, VchrConsI.ACTION_RECHARGE_CREDIT);

				// set TRANSACTION_STATUS as Success in request map
				_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
				// set NEW_EXPIRY_DATE into request map
				if (!_isRetryRequest) {
					_requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("supervisionExpiryDate"), "yyyyMMdd"));
					_requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd"));
					// set INTERFACE_POST_BALANCE into request map as obtained thru
					// response map.
					try {
						String postBalanceStr = (String) _responseMap.get("accountValue1");
						postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr, multFactorDouble);
						_requestMap.put("INTERFACE_POST_BALANCE", postBalanceStr);
					} catch (Exception e) {
						LOG.errorTrace(methodName, e);
						LOG.error("credit", "Exception e:" + e.getMessage());
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "methodName", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
						throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.ERROR_RESPONSE);
					}
					_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
				} else
					_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
			}
		} catch (BTSLBaseException be) {
			p_map.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			LOG.error(methodName, "BTSLBaseException be:" + be.getMessage());
			if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
				throw be;
			try {
				_requestMap.put("TRANSACTION_TYPE", "CR");
				handleCancelTransaction();
			} catch (BTSLBaseException bte) {
				throw bte;
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
				LOG.error(methodName, "Exception e:" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "methodName", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
				throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
		} catch (Exception e) {
			LOG.errorTrace(methodName, e);
			LOG.error(methodName, "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "methodName", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
			throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}





		if (LOG.isDebugEnabled())
			LOG.debug(methodName + " Entered ", " _requestMap: " + _requestMap);


		Connection con = null;
		try {
			_requestMap = p_map;
			_inTXNID = InterfaceUtil.getINTransactionID();
			_requestMap.put("IN_TXN_ID", _inTXNID);
			Date currDate = InterfaceUtil.getDateTimeFromDateString((String) _requestMap.get("TRANSFER_DATE"));
			String currentStatus = (String) _requestMap.get("UPDATE_STATUS");
			String previousStatus = (String) _requestMap.get("PREVIOUS_STATUS");
			String modifiedBy = (String) _requestMap.get("SENDER_ID");
			String statusChangeSource = (String) _requestMap.get("SOURCE");
			// Update the voucher status to consume
			con = VomsUtil.getSingleConnection(FileCache.getValue(_interfaceID, "datasourceurl"), FileCache.getValue(_interfaceID, "userid"), FileCache.getValue(_interfaceID, "passwd"));
			VOMSVoucherVO vomsVO = new VOMSVoucherVO();
			// Update the Voucher status to under process if the previous status
			// is enabled.
			vomsVO.setPreviousStatus(previousStatus);
			vomsVO.setCurrentStatus(currentStatus);
			vomsVO.setModifiedBy(modifiedBy);
			vomsVO.setModifiedOn(currDate);
			vomsVO.setStatusChangeSource(statusChangeSource);
			vomsVO.setStatusChangePartnerID(modifiedBy);
			vomsVO.setSerialNo((String) _requestMap.get("SERIAL_NUMBER"));
			vomsVO.setTransactionID((String) _requestMap.get("TRANSACTION_ID"));
			vomsVO.setUserLocationCode((String) _requestMap.get("NETWORK_CODE"));
			VOMSVoucherDAO vomsDAO = new VOMSVoucherDAO();
			if (VOMSVoucherDAO.updateVoucherStatus(con, vomsVO) > 0) {
				if (vomsDAO.insertDetailsInVoucherAudit(con, vomsVO) > 0)
					con.commit();
				else {
					con.rollback();
					throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_ERROR_INSERTION_AUDIT_TABLE);
				}
			} else
				throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_ERROR_UPDATION);
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			_requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
		} catch (BTSLBaseException be) {
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
			}
			throw be;
		} catch (Exception e) {
			LOG.errorTrace(methodName, e);
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception e1) {
					LOG.errorTrace(methodName, e1);
				}
			}
			LOG.error(methodName, "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while updating voucher =" + _requestMap.get("SERIAL_NUMBER") + " status to " + _requestMap.get("UPDATE_STATUS") + " for Interface=" + _requestMap.get("INTERFACE_ID") + " ");
			throw e;
		}
		finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
			}
			if (LOG.isDebugEnabled())
				LOG.debug(methodName, "Exited _requestMap=" + _requestMap);
		}

	}

	/**
	 * debit Method is used for updating the status to enable in voms_vouches
	 * table.
	 * 
	 * @param p_map
	 *            HashMap
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	public void debit(HashMap p_map) throws BTSLBaseException, Exception {
		String methodName = "debit";
		if (LOG.isDebugEnabled())
			LOG.debug(methodName + " Entered ", " _requestMap: " + _requestMap);
		try {
			credit(p_map);
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			throw e;
		} finally {
			if (LOG.isDebugEnabled())
				LOG.debug(methodName + " Exited ", " _requestMap: " + _requestMap);
		}
	}

	/**
	 * creditAdjust Method is used for updating the status to ambigious in
	 * voms_vouches table.
	 * 
	 * @param p_map
	 *            HashMap
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception {
		String methodName = "creditAdjust";
		if (LOG.isDebugEnabled())
			LOG.debug(methodName + " Entered ", " _requestMap: " + _requestMap);
		try {
			credit(p_map);
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			throw e;
		} finally {
			if (LOG.isDebugEnabled())
				LOG.debug(methodName + " Exited ", " _requestMap: " + _requestMap);
		}
	}

	public void debitAdjust(HashMap p_map) {
	}

	/**
	 * This method would be used to adjust the validity of the subscriber
	 * account at the IN.
	 * 
	 * @param HashMap
	 *            p_requestMap
	 * @throws BTSLBaseException
	 *             , Exception
	 */
	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
	}// end of validityAdjust

	/*
	 * public static void main(String [] a)
	 * {
	 * try
	 * {
	 * ConfigServlet.loadProcessCache(a[0],a[1]);
	 * FileCache.loadAtStartUp();
	 * HashMap h=new HashMap();
	 * h.put("NETWORK_CODE","AK");
	 * h.put("TRANSFER_DATE","11/04/07 10:00");
	 * h.put("INTERFACE_AMOUNT","2000");
	 * h.put("UPDATE_STATUS","UP");
	 * h.put("PREVIOUS_STATUS","EN");
	 * h.put("SENDER_USER_ID","AKLA0000000040");
	 * h.put("SOURCE","WEB");
	 * h.put("INTERFACE_ID","INTID00004");
	 * EVDHandler EVDHandler=new EVDHandler();
	 * EVDHandler.validate(h);
	 * EVDHandler.validate(h);
	 * EVDHandler.validate(h);
	 * EVDHandler.validate(h);
	 * EVDHandler.validate(h);
	 * EVDHandler.validate(h);
	 * EVDHandler.validate(h);
	 * EVDHandler.validate(h);
	 * }
	 * catch(Exception e)
	 * {
	 * e.printStackTrace();
	 * }
	 * finally
	 * {
	 * ConfigServlet.destroyProcessCache();
	 * }
	 * }
	 */

	/**
	 * Method to generate the transaction ID.
	 * 
	 * @throws BTSLBaseException
	 */
	protected static synchronized String getINTransactionID(HashMap p_requestMap) throws BTSLBaseException {
		String methodName = "getINTransactionID";
		String instanceID = null;
		int MAX_COUNTER = 9999;
		int inTxnLength = 4;
		String serviceType = null;
		String userType = null;
		Date mydate = null;
		String minut2Compare = null;
		String dateStr = null;
		String transactionId = null;
		try {
			serviceType = (String) p_requestMap.get("REQ_SERVICE");
			if ("RC".equals(serviceType))
				serviceType = "8";
			else if ("PRCMDA".equals(serviceType))
				serviceType = "7";
			// else if("PRC".equals(serviceType) || "PCR".equals(serviceType) ||
			// "ACCINFO".equals(serviceType) || "C2SENQ".equals(serviceType) )
			// modified by harsh for Scheduled Credit Transfer
			else if ("PRC".equals(serviceType) || "PCR".equals(serviceType) || "ACCINFO".equals(serviceType) || "C2SENQ".equals(serviceType) || PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER.equals(serviceType))
				serviceType = "6";
			userType = (String) p_requestMap.get("USER_TYPE");
			if ("S".equals(userType))
				userType = "3";
			else if ("R".equals(userType))
				userType = "2";
			instanceID = FileCache.getValue((String) p_requestMap.get("INTERFACE_ID"), "INSTANCE_ID");
			if (InterfaceUtil.isNullString(instanceID)) {
				LOG.error(methodName, "Parameter INSTANCE_ID is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[validate]", "", "", (String) p_requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			mydate = new Date();
			dateStr = _sdf.format(mydate);
			minut2Compare = dateStr.substring(8, 10);
			int currentMinut = Integer.parseInt(minut2Compare);
			if (currentMinut != _prevMinut) {
				_transactionIDCounter = 1;
				_prevMinut = currentMinut;
			} else if (_transactionIDCounter > MAX_COUNTER)
				_transactionIDCounter = 1;
			else
				_transactionIDCounter++;
			String txnid = String.valueOf(_transactionIDCounter);
			int length = txnid.length();
			int tmpLength = inTxnLength - length;
			if (length < inTxnLength) {
				for (int i = 0; i < tmpLength; i++)
					txnid = "0" + txnid;
			}
			transactionId = serviceType + dateStr + instanceID + txnid + userType;
		} catch (Exception e) {
			LOG.errorTrace(methodName, e);
		}
		return transactionId;
	}

	/**
	 * This method is used to set the interface parameters into requestMap,
	 * these parameters are as bellow
	 * 1.Origin node type.
	 * 2.Origin host type.
	 * 
	 * @throws Exception
	 */
	private void setInterfaceParameters() throws Exception, BTSLBaseException {
		String methodName = "setInterfaceParameters";
		if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered");
		try {
			String cancelTxnAllowed = FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
			if (InterfaceUtil.isNullString(cancelTxnAllowed)) {
				LOG.error(methodName, "Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CANCEL_TXN_ALLOWED", cancelTxnAllowed.trim());

			String systemStatusMappingCr = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT");
			if (InterfaceUtil.isNullString(systemStatusMappingCr)) {
				LOG.error(methodName, "Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT", systemStatusMappingCr.trim());

			String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
			if (InterfaceUtil.isNullString(systemStatusMappingCrAdj)) {
				LOG.error(methodName, "Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ", systemStatusMappingCrAdj.trim());

			String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
			if (InterfaceUtil.isNullString(systemStatusMappingDbtAdj)) {
				LOG.error(methodName, "Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ", systemStatusMappingDbtAdj.trim());

			String systemStatusMappingCrBck = FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING_CREDIT_BCK");
			if (InterfaceUtil.isNullString(systemStatusMappingCrBck)) {
				LOG.error(methodName, "Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK", systemStatusMappingCrBck.trim());

			String cancelCommandStatusMapping = FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING");
			if (InterfaceUtil.isNullString(cancelCommandStatusMapping)) {
				LOG.error(methodName, "Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", cancelCommandStatusMapping.trim());

			String cancelNA = FileCache.getValue(_interfaceID, "CANCEL_NA");
			if (InterfaceUtil.isNullString(cancelNA)) {
				LOG.error(methodName, "Value of CANCEL_NA is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CANCEL_NA", cancelNA.trim());

			String warnTimeStr = (String) FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
			if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

			String nodeType = FileCache.getValue(_interfaceID, "NODE_TYPE");
			if (InterfaceUtil.isNullString(nodeType)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "NODE_TYPE is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("NODE_TYPE", nodeType.trim());

			String hostName = FileCache.getValue(_interfaceID, "HOST_NAME");
			if (InterfaceUtil.isNullString(hostName)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "HOST_NAME is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("HOST_NAME", hostName.trim());

			String subscriberNAI = FileCache.getValue(_interfaceID, "NAI");
			if (InterfaceUtil.isNullString(subscriberNAI) || !InterfaceUtil.isNumeric(subscriberNAI)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "NAI is not defined in IN File or not numeric");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("SubscriberNumberNAI", subscriberNAI.trim());

			String currency = FileCache.getValue(_interfaceID, "CURRENCY");
			if (InterfaceUtil.isNullString(currency)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "CURRENCY is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("CURRENCY", currency.trim());

			String RefillAccountAfterFlag = FileCache.getValue(_interfaceID, "REFILL_ACNT_AFTER_FLAG");
			if (InterfaceUtil.isNullString(RefillAccountAfterFlag)) {
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "REFILL_ACNT_AFTER_FLAG is not defined in IN File ");
				throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("REFILL_ACNT_AFTER_FLAG", RefillAccountAfterFlag.trim());

			String extData1 = FileCache.getValue(_interfaceID, "EXTERNAL_DATA1");
			if (InterfaceUtil.isNullString(extData1))
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VchrConsHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "EXTERNAL_DATA1 is not defined in IN File ");
			_requestMap.put("EXTERNAL_DATA1", extData1.trim());

			String extData2 = FileCache.getValue(_interfaceID, "EXTERNAL_DATA2");
			if (InterfaceUtil.isNullString(extData2))
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VchrConsHandler[setInterfaceParameters]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, " INTERFACE ID = " + _interfaceID, "Network code " + (String) _requestMap.get("NETWORK_CODE"), "EXTERNAL_DATA2 is not defined in IN File ");
			_requestMap.put("EXTERNAL_DATA2", extData2.trim());
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			LOG.error(methodName, "Exception e=" + e.getMessage());
			throw e;
		} finally {
			if (LOG.isDebugEnabled())
				LOG.debug(methodName, "Exited _requestMap:" + _requestMap);
		}
	}

	/**
	 * This method is used to send the request to IN and stored the response
	 * after parsing.
	 * This method also take care about to handle the error situation to send
	 * the alarm and set the error code.
	 * 1.Invoke the getNodeVO method of NodeScheduler class and pass the
	 * Transaction Id.
	 * 2.If the VO is Null then mark the request as fail and throw exception(New
	 * Error code that defines No connection for any Node is available).
	 * 3.If the VO is not NULL then pass the Node detail to CS5UrlConnection
	 * class and get connection.
	 * 4.After the processing the request(may be successful or fail) decrement
	 * the connection counter and pass the
	 * transaction id that is removed from the transNodeList.
	 * 
	 * @param String
	 *            p_inRequestStr
	 * @param int p_action
	 * @throws BTSLBaseException
	 */
	private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException {
		String methodName = "sendRequestToIN";
		if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered _msisdn=" + _msisdn + " p_action=" + p_action + " p_inRequestStr=" + p_inRequestStr);
		TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::" + p_inRequestStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + p_action);
		String responseStr = "";
		long startTime = 0;
		long endTime = 0;
		int conRetryNumber = 0;
		long warnTime = 0;
		int readTimeOut = 0;
		String inReconID = null;
		long retrySleepTime = 0;
		String url1 = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			if (p_action != VchrConsI.ACTION_ACCOUNT_DETAILS)
				_responseMap = new HashMap<String, String>();
			inReconID = (String) _requestMap.get("IN_RECON_ID");
			if (inReconID == null)
				inReconID = _inTXNID;
			retrySleepTime = Long.parseLong(FileCache.getValue(_interfaceID, "RETRY_SLEEP_TIME"));
			url1 = String.valueOf(FileCache.getValue(_interfaceID, "URL_1"));
			while (_ambgCurrentRetryCount++ <= _ambgMaxRetryCount) {
				try {
					if (p_action != VchrConsI.ACTION_ACCOUNT_INFO && p_action != VchrConsI.ACTION_ACCOUNT_DETAILS) {
						if (LOG.isDebugEnabled())
							LOG.error(methodName, "SENDING RETRY........" + (_ambgCurrentRetryCount - 1) + "_isRetryRequest " + _isRetryRequest + "IN Transaction Id" + (String) _requestMap.get("IN_RECON_ID"));
					}
					long startTimeNode = System.currentTimeMillis();
					if (LOG.isDebugEnabled())
						LOG.debug("sendRequestToIN", "Start time to find the scheduled Node startTimeNode::" + startTimeNode + "miliseconds");
					URL url = new URL(url1);
					_urlConnection = (HttpURLConnection) url.openConnection();
					_urlConnection.setConnectTimeout(10000);
					_urlConnection.setReadTimeout(10000);
					_urlConnection.setDoOutput(true);
					_urlConnection.setDoInput(true);
					_urlConnection.addRequestProperty("Content-Type", "text/xml");
					_urlConnection.setRequestMethod("POST");
					try {
						// PrintWriter out = cs5URLConnection.getPrintWriter();
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())), true);
						out.flush();
						startTime = System.currentTimeMillis();
						_requestMap.put("IN_START_TIME", String.valueOf(startTime));
						out.println(p_inRequestStr);
						out.flush();
					} catch (Exception e) {
						LOG.errorTrace(methodName, e);
						LOG.error(methodName, "Exception e::" + e);
						// EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES,
						// EventStatusI.RAISED,
						// EventLevelI.FATAL,"VchrConsHandler[sendRequestToIN]",_referenceID,_msisdn,
						// (String)
						// _requestMap.get("NETWORK_CODE"),"While sending request to CS5Mobinil IN INTERFACE_ID=["+_interfaceID
						// +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"] Exception::"+e.getMessage());
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
					}
					try {
						StringBuffer buffer = new StringBuffer();
						String response = "";
						try {
							// Reading the response from buffered reader.
							in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
							while ((response = in.readLine()) != null) {
								buffer.append(response);
							}
							endTime = System.currentTimeMillis();
							if (warnTime <= (endTime - startTime)) {
								LOG.info(methodName, "WARN time reaches, startTime::" + startTime + " endTime::" + endTime + " From file cache warnTime::" + warnTime + " time taken (endTime-startTime)::" + (endTime - startTime));
								// EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.MAJOR,"VchrConsHandler[sendRequestToIN]",_inTXNID,_msisdn,(String)
								// _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"CS5MobinilIN is taking more time than the warning threshold. Time: "+(endTime-startTime)+"INTERFACE_ID=["+_interfaceID
								// +"]and Node Number=["+cs5NodeVO.getNodeNumber()+"]");
							}
						} catch (Exception e) {
							LOG.errorTrace(methodName, e);
							LOG.error(methodName, "Exception e::" + e.getMessage());
							if (_ambgCurrentRetryCount > _ambgMaxRetryCount) {
								LOG.error(methodName, "Read time out occured. Retry attempts exceeded so throwing AMBIGOUS exception");
								EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "Ferma6INHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Read timeout from IN. Retry attempts exceeded so throwing AMBIGOUS exception");
								_requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
								throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
							}
							Thread.sleep(retrySleepTime);
							_isRetryRequest = true;
							_requestMap.put("IN_RECON_ID", _inTXNID + "01");
							_requestMap.put("IN_TXN_ID", _inTXNID + "01");
							continue;
						} finally {
							if (endTime == 0)
								endTime = System.currentTimeMillis();
							_requestMap.put("IN_END_TIME", String.valueOf(endTime));
							LOG.error(methodName, "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime + " defined read time out is:" + readTimeOut);
						}
						responseStr = buffer.toString();
						TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + responseStr, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action=" + p_action);
						if (LOG.isDebugEnabled())
							LOG.debug(methodName, "_msisdn=" + _msisdn + " p_action=" + p_action + " responseStr=" + responseStr);
						if (InterfaceUtil.isNullString(responseStr)) {
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Blank response from CS5MobinilIN");
							LOG.error(methodName, "NULL response from interface");
							// _nodeCloser.updateCountersOnAmbiguousResp(cs5NodeVO);
							if (_ambgCurrentRetryCount > _ambgMaxRetryCount) {
								LOG.error(methodName, "Read time out occured. Retry attempts exceeded so throwing AMBIGOUS exception");
								EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "NULL response from IN. Retry attempts exceeded so throwing AMBIGOUS exception");
								_requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
								throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
							}
							Thread.sleep(retrySleepTime);
							_isRetryRequest = true;
							_requestMap.put("IN_RECON_ID", _inTXNID + "01");
							_requestMap.put("IN_TXN_ID", _inTXNID + "01");
							continue;
						}
						/*
						 * if(cs5NodeVO.isSuspended())
						 * _nodeCloser.resetCounters(cs5NodeVO);
						 */
						_responseMap = _VchrConsRequestFormatter.parseResponse(p_action, responseStr, _responseMap);
						// Here the various checks would be done based on the
						// response.
						// Check the fault code if it is not null then handle
						// the event with message as fault string and error
						// code.
						// First check whether the responseCode is null
						// If the response code is null,check the fault code,if
						// present get the fault string and
						// a.throw the exception with error code
						// INTERFACE_PROCESS_REQUEST_ERROR.
						// b.Handle the event with Level FATAL and message as
						// fault strring
						// 1.If the responseCode is other than 0
						// a.check if the code is 102 then throw
						// BTSLBaseException
						// 2.If the responseCode is 0 then checks the following.
						String faultCode = (String) _responseMap.get("faultCode");
						if (!InterfaceUtil.isNullString(faultCode)) {
							// Log the value of executionStatus for
							// corresponding msisdn,recieved from the response.
							LOG.info(methodName, "faultCode::" + faultCode + "_inTXNID::" + _inTXNID + " _msisdn::" + _msisdn);
							_requestMap.put("INTERFACE_STATUS", faultCode);// Put
							// the
							// interface_status
							// in
							// requestMap
							LOG.error(methodName, "faultCode=" + _responseMap.get("faultCode") + "faultString = " + _responseMap.get("faultString"));
							EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, (String) _responseMap.get("faultString"));
							throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
						}
						String responseCode = (String) _responseMap.get("responseCode");
						_requestMap.put("INTERFACE_STATUS", responseCode);
						Object[] successList = VchrConsI.RESULT_OK.split(",");
						if (!Arrays.asList(successList).contains(responseCode)) {
							if (VchrConsI.SUBSCRIBER_NOT_FOUND.equals(responseCode)) {
								LOG.error(methodName, "Subscriber not found with MSISDN::" + _msisdn);
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VchrConsHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Subscriber is not found at IN");
								throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
							} else if (VchrConsI.OLD_TRANSACTION_ID.equals(responseCode)) {
								if (!_isRetryRequest) {
									if (LOG.isDebugEnabled())
										LOG.debug(methodName, "_isRetryRequest:" + _isRetryRequest);
									EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsHandler[sendRequestToIN]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Transaction ID mismatch");
									throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PARAMETER_MISMATCH);// recharge
									// request
									// with
									// old
									// transaction
									// id
								}
							} else {
								LOG.error(methodName, "Error code received from IN ::" + responseCode);
								EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, _interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VchrConsHandler[sendRequestToIN]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID + " Stage = " + p_action, "Error code received from IN " + responseCode);
								throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
							}
						}
					} catch (BTSLBaseException be) {
						throw be;
					} catch (Exception e) {
						LOG.errorTrace(methodName, e);
						LOG.error(methodName, "Exception e::" + e.getMessage());
						throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
					}
					break;
				} finally {
					in.close();
					out.close();
				}
			}
		} catch (BTSLBaseException be) {
			LOG.error(methodName, "BTSLBaseException be::" + be.getMessage());
			throw be;
		} catch (Exception e) {
			LOG.errorTrace(methodName, e);
			LOG.error(methodName, "Exception e::" + e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		} finally {
			if (LOG.isDebugEnabled())
				LOG.debug(methodName, "Exiting  _interfaceID::" + _interfaceID + " Stage::" + p_action + " responseStr::" + responseStr);
		}

	}

	/**
	 * Method to send cancel request to IN for any ambiguous transaction.
	 * This method also makes reconciliation log entry.
	 * 
	 * @throws BTSLBaseException
	 */
	private void handleCancelTransaction() throws BTSLBaseException {
		String methodName = "handleCancelTransaction";
		if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered.");
		String cancelTxnAllowed = null;
		String cancelTxnStatus = null;
		String reconciliationLogStr = null;
		String cancelCommandStatus = null;
		String cancelNA = null;
		String interfaceStatus = null;
		Log reconLog = null;
		String systemStatusMapping = null;
		try {
			_requestMap.put("REMARK1", FileCache.getValue(_interfaceID, "REMARK1"));
			_requestMap.put("REMARK2", FileCache.getValue(_interfaceID, "REMARK2"));
			// get reconciliation log object associated with interface
			reconLog = ReconcialiationLog.getLogObject(_interfaceID);
			if (LOG.isDebugEnabled())
				LOG.debug(methodName, "reconLog." + reconLog);
			cancelTxnAllowed = (String) _requestMap.get("CANCEL_TXN_ALLOWED");
			// if cancel transaction is not supported by IN, get error codes
			// from mapping present in IN fILE,write it
			// into recon log and throw exception (This exception tells the
			// final status of transaction which was ambiguous) which would be
			// handled by validate, credit or debitAdjust methods
			if ("N".equals(cancelTxnAllowed)) {
				cancelNA = (String) _requestMap.get("CANCEL_NA");// Cancel
				// command
				// status as
				// NA.
				cancelCommandStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap, cancelNA, "CANCEL_COMMAND_STATUS_MAPPING");
				_requestMap.put("MAPPED_CANCEL_STATUS", cancelCommandStatus);
				interfaceStatus = (String) _requestMap.get("INTERFACE_STATUS");
				systemStatusMapping = (String) _requestMap.get("SYSTEM_STATUS_MAPPING");
				cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap, interfaceStatus, systemStatusMapping); // PreTUPs
				// Transaction
				// status
				// as
				// FAIL/AMBIGUOUS
				// based
				// on
				// value
				// of
				// SYSTEM_STATUS_MAPPING
				_requestMap.put("MAPPED_SYS_STATUS", cancelTxnStatus);
				reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(_requestMap);
				reconLog.info("", reconciliationLogStr);
				if (!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
					throw new BTSLBaseException(this, "handleCancelTransaction", cancelTxnStatus); // //Based
				// on
				// the
				// value
				// of
				// SYSTEM_STATUS
				// mark
				// the
				// transaction
				// as
				// FAIL
				// or
				// AMBIGUOUS
				// to
				// the
				// system.(//should
				// these
				// be
				// put
				// in
				// error
				// log
				// also.
				// ??????)
				_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
				// added to discard amount field from the message.
				_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			LOG.errorTrace(methodName, e);
			LOG.error(methodName, "Exception e:" + e.getMessage());
			throw new BTSLBaseException(this, "handleCancelTransaction", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		} finally {
			if (LOG.isDebugEnabled())
				LOG.debug(methodName, "Exited");
		}
	}

}
