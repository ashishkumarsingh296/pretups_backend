package com.businesscontrollers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;

public class BusinessValidator extends BaseTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void validateStocks(TransactionVO TransactionVO) {
		
		//Cloning Sale wallet & fetching initiated Quantities for further validations
		boolean commissioningType = false;
		if (_masterVO.getClientDetail("DUAL_COMMISSION_FieldType").equalsIgnoreCase("0")) {
			commissioningType = Boolean.parseBoolean(DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY"));
		} else {
			if (TransactionVO.getDualCommissioningType().equalsIgnoreCase(PretupsI.Postive_Commission)) {
				commissioningType = true;
			}
		}
		
		HashMap<String, String> saleWalletExpectedBalances = (HashMap<String, String>) TransactionVO.getSalePreBalances().clone();
		HashMap<String, String> initiatedQuantities = (HashMap<String, String>) TransactionVO.getInitiatedQty().clone();
		HashMap<String, String> incentiveWalletExpectedBalances = null;
		HashMap<String, String> focWalletExpectedBalances = null;
		
		// Fetching Incentive & FOC Wallet Balances in case MULTI_WALLET_APPLY = true
		if (TransactionVO.get_multiWalletStatus().equalsIgnoreCase("true")) {
			incentiveWalletExpectedBalances = (HashMap<String, String>) TransactionVO.getIncentivePreBalances().clone();
			focWalletExpectedBalances = (HashMap<String, String>) TransactionVO.getFocPreBalances().clone();
		}
		
		/*
		 * Transaction Type: O2C Transfer
		 * 	-	Get Number of Initiated Products & their respective quantities.
		 * 	-	If Multiple wallet is applicable in system, Sale wallet expected Stock to be (PreBalance - Initiated Quantity) AND:
		 * 		-	If CommissioningType == true i.e Positive commissioning applicable in system, then Incentive Wallet is expected to be (PreBalance - Commission)
		 * 		-	If CommissioningType == false, Incentive Wallet Remains same
		 * 	-	If Multiple Wallet is not applicable in system:
		 * 		-	If CommissioningType == true i.e Positive commissioning applicable in system, then Expected Sale Wallet Balance is (PreBalance - InitiatedQty - Commission)
		 * 		-	If CommissioningType == false i.e Negative commissioning applicable in system, then Expected Sale Wallet Balance is (PreBalance - Initiated Quantity)
		 */
		if(TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("O2CTransferCode"))) {
			
				Iterator it = initiatedQuantities.entrySet().iterator();
				   while (it.hasNext()) {
				       Map.Entry pair = (Map.Entry)it.next();
				       String initiatedProduct = pair.getKey().toString();

				       long initiatedQty = _parser.getSystemAmount(pair.getValue().toString());
				       long preSaleWalletBalance = Long.parseLong(TransactionVO.getSalePreBalances().get(initiatedProduct));
				       long expectedSaleWalletPostBalance;

				       if (TransactionVO.get_multiWalletStatus().equalsIgnoreCase("true")) {
				    	   expectedSaleWalletPostBalance = preSaleWalletBalance - initiatedQty;
				    	   if (commissioningType) {
						       long preIncentiveWalletBalance = Long.parseLong(TransactionVO.getIncentivePreBalances().get(initiatedProduct));
						       long expectedIncentiveWalletPostBalance = preIncentiveWalletBalance - TransactionVO.getCommissionVO().get(initiatedProduct).getCommissionQty();
						       incentiveWalletExpectedBalances.put(initiatedProduct, evaluateAutoNetworkStock(TransactionVO, PretupsI.INCENTIVE_WALLET_LOOKUP, initiatedProduct, expectedIncentiveWalletPostBalance));
				    	   }
				       } else {
				    	   if (commissioningType)
				    		   expectedSaleWalletPostBalance = preSaleWalletBalance - initiatedQty - TransactionVO.getCommissionVO().get(initiatedProduct).getCommissionQty();
				    	   else 
				    		   expectedSaleWalletPostBalance = preSaleWalletBalance - initiatedQty;
				       }
				       
				       saleWalletExpectedBalances.put(initiatedProduct, evaluateAutoNetworkStock(TransactionVO, PretupsI.SALE_WALLET_LOOKUP, initiatedProduct, expectedSaleWalletPostBalance));
				       it.remove();
				}
			
		} 
		
		/*
		 * Transaction Type: O2C Return & O2C Withdraw
		 * 	-	Get Number of Initiated Products & their respective quantities.
		 * 	-	Sale wallet expected Stock to be (PreBalance + Return Quantity)
		 */
		else if(TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("O2CReturnCode")) || TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("O2CWithdrawCode"))) {
			
				Iterator it = initiatedQuantities.entrySet().iterator();
				   while (it.hasNext()) {
				       Map.Entry pair = (Map.Entry)it.next();
				       String initiatedProduct = pair.getKey().toString();

				       long initiatedQty = _parser.getSystemAmount(pair.getValue().toString());
				       long preSaleWalletBalance = Long.parseLong(TransactionVO.getSalePreBalances().get(initiatedProduct));
				       long expectedSaleWalletPostBalance = preSaleWalletBalance + initiatedQty;
				       saleWalletExpectedBalances.put(initiatedProduct, Long.toString(expectedSaleWalletPostBalance));
				       it.remove();
				}
			
		}
		
		else if(TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("FOCCode"))) {
			
				Iterator it = initiatedQuantities.entrySet().iterator();
				   while (it.hasNext()) {
				       Map.Entry pair = (Map.Entry)it.next();
	
				       String initiatedProduct = pair.getKey().toString();
				       long initiatedQty = _parser.getSystemAmount(pair.getValue().toString());
				       
				       if (TransactionVO.get_multiWalletStatus().equalsIgnoreCase("true")) {
					       long prefocWalletBalance = Long.parseLong(TransactionVO.getFocPreBalances().get(initiatedProduct));
					       long expectedFocWalletPostBalance = prefocWalletBalance - initiatedQty;
					       focWalletExpectedBalances.put(initiatedProduct, evaluateAutoNetworkStock(TransactionVO, PretupsI.FOC_WALLET_LOOKUP, initiatedProduct, expectedFocWalletPostBalance)); 
				       } else {
				    	   long preSaleWalletBalance = Long.parseLong(TransactionVO.getSalePreBalances().get(initiatedProduct));
				    	   long expectedSaleWalletPostBalance = preSaleWalletBalance - initiatedQty;
				    	   saleWalletExpectedBalances.put(initiatedProduct, evaluateAutoNetworkStock(TransactionVO, PretupsI.SALE_WALLET_LOOKUP, initiatedProduct, expectedSaleWalletPostBalance));
				       }
				       it.remove();
				   }
		}
		
		/*
		 * Transaction Type: C2C Transfer
		 * 	-	Get Commission Quantity
		 * 	-	If CommissioningType is True i.e Positive commissioning applicable in system AND:
		 * 		-	If Multiple Wallet is applicable in system, then Incentive Wallet is expected to be (PreBalance - Commission)
		 * 		-	If Multiple Wallet is not applicable in system, then Sale Wallet is expected to be (PreBalance - Commission)
		 */
		else if(TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("C2CTransferCode"))) {
			
				Iterator it = initiatedQuantities.entrySet().iterator();
				   while (it.hasNext()) {
				       Map.Entry pair = (Map.Entry)it.next();
				       String initiatedProduct = pair.getKey().toString();

				       long initiatedQty = _parser.getSystemAmount(pair.getValue().toString());
				       
				       if (commissioningType) {
				    	   if (TransactionVO.get_multiWalletStatus().equalsIgnoreCase("true")) {
				    		   long preIncentiveWalletBalance = Long.parseLong(TransactionVO.getIncentivePreBalances().get(initiatedProduct));
						       long expectedIncentiveWalletPostBalance = preIncentiveWalletBalance - TransactionVO.getCommissionVO().get(initiatedProduct).getCommissionQty();
						       incentiveWalletExpectedBalances.put(initiatedProduct, evaluateAutoNetworkStock(TransactionVO, PretupsI.INCENTIVE_WALLET_LOOKUP, initiatedProduct, expectedIncentiveWalletPostBalance));
				    	   } else {
				    		   long preSaleWalletBalance = Long.parseLong(TransactionVO.getSalePreBalances().get(initiatedProduct));
						       long expectedSaleWalletPostBalance = preSaleWalletBalance - TransactionVO.getCommissionVO().get(initiatedProduct).getCommissionQty();
						       saleWalletExpectedBalances.put(initiatedProduct, evaluateAutoNetworkStock(TransactionVO, PretupsI.SALE_WALLET_LOOKUP, initiatedProduct, expectedSaleWalletPostBalance));
				    	   }
				       }
				       
				       it.remove();
				}
			
		} 
		
		/*
		 * Transaction Type: C2C Withdraw & C2C Return
		 * 	-	Network Stocks remains unchanged in both the cases
		 */
		if(TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("C2CWithdrawCode")) || TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("C2CReturnCode"))) {
						
		} 
		
		Log.info(mapToString(TransactionVO, TransactionVO.getSalePreBalances(), saleWalletExpectedBalances, TransactionVO.getSalePostBalances(), "SALE: Wallet Validation"));
		
		if (saleWalletExpectedBalances.equals(TransactionVO.getSalePostBalances()))
			ExtentI.Markup(ExtentColor.GREEN, "SALE Wallet Validated Successfully! Post Balances Matched with expected Balances.");
		else {
			ExtentI.Markup(ExtentColor.RED, "SALE Wallet Validated Successfully! Post Balances Mis-match found with expected Balances.");
			Log.failNode("----- Failure -----");
		}
			
		
		if (TransactionVO.get_multiWalletStatus().equalsIgnoreCase("true")) {
			Log.info(mapToString(TransactionVO, TransactionVO.getIncentivePreBalances(), incentiveWalletExpectedBalances, TransactionVO.getIncentivePostBalances(), "INCENTIVE: Wallet Validation"));
			
			if (incentiveWalletExpectedBalances.equals(TransactionVO.getIncentivePostBalances()))
				ExtentI.Markup(ExtentColor.GREEN, "INCENTIVE Wallet Validated Successfully! Post Balances Matched with expected Balances.");
			else {
				ExtentI.Markup(ExtentColor.RED, "INCENTIVE Wallet Validated Successfully! Post Balances Mis-match found with expected Balances.");
				Log.failNode("----- Failure -----");
			}
			
			Log.info(mapToString(TransactionVO, TransactionVO.getFocPreBalances(), focWalletExpectedBalances, TransactionVO.getFocPostBalances(), "FOC: Wallet Validation"));
			
			if (focWalletExpectedBalances.equals(TransactionVO.getFocPostBalances()))
				ExtentI.Markup(ExtentColor.GREEN, "FOC Wallet Validated Successfully! Post Balances Matched with expected Balances.");
			else {
				ExtentI.Markup(ExtentColor.RED, "FOC Wallet Validated Successfully! Post Balances Mis-match found with expected Balances.");
				Log.failNode("----- Failure -----");
			}
		}
			
	}
	
	public static void validateUserBalances(TransactionVO TransactionVO) {
		
		HashMap<String, String> initiatedQuantities = (HashMap<String, String>) TransactionVO.getInitiatedQty().clone();
		HashMap<String, String> receiverExpectedPostBalances;
		HashMap<String, String> senderExpectedPostBalances;
		
		/*
		 * Transaction Type: O2C Transfer
		 * Validate To User (Receiver) PreBalance as PreBalance + Receiver Credit Quantity - Fetched from Commission Calculation Logic.
		 */
		if (TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("O2CTransferCode"))) {		
				receiverExpectedPostBalances = (HashMap<String, String>) TransactionVO.get_toUserPreBalances().clone();
				
				Iterator it = initiatedQuantities.entrySet().iterator();
				   while (it.hasNext()) {
					   Map.Entry pair = (Map.Entry) it.next();
					   String initiatedProduct = pair.getKey().toString();

					   /* --- Added by krishan.chawla on 02/01/19 to control NullPointerException when there is no record for user balance in user_balances table --- */
					   long expectedPostBalance;
					   if (TransactionVO.get_toUserPreBalances().get(initiatedProduct) != null)
						   expectedPostBalance = Long.parseLong(TransactionVO.get_toUserPreBalances().get(initiatedProduct)) + TransactionVO.getCommissionVO().get(initiatedProduct).getReceiverCreditQty();
					   else
						   expectedPostBalance = TransactionVO.getCommissionVO().get(initiatedProduct).getReceiverCreditQty();

					   receiverExpectedPostBalances.put(initiatedProduct, Long.toString(expectedPostBalance));
					   it.remove();
				   }
			
			Log.info(mapToString(TransactionVO, TransactionVO.get_toUserPreBalances(), receiverExpectedPostBalances, TransactionVO.get_toUserPostBalances(), "Receiver Balance Validation"));
			
			if (receiverExpectedPostBalances.equals(TransactionVO.get_toUserPostBalances()))
				ExtentI.Markup(ExtentColor.GREEN, "Receiver Balances Validated Successfully! Post Balances Matched with expected Balances.");
			else {
				ExtentI.Markup(ExtentColor.RED, "Receiver Balances Validated Successfully! Post Balances Mis-match found with expected Balances.");
				Log.failNode("----- Failure -----");
			}
		}
		
		/*
		 * Transaction Type: O2C Return
		 * 	-	Get Number of Initiated Products & their respective quantities.
		 * 	-	Validate From User (Sender) PreBalance as PreBalance - Return Quantity (PreBalance - Return Quantity)
		 */
		else if(TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("O2CReturnCode")) || TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("O2CWithdrawCode"))) {
				senderExpectedPostBalances = (HashMap<String, String>) TransactionVO.get_fromUserPreBalances().clone();
				
				Iterator it = initiatedQuantities.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry)it.next();
				    String initiatedProduct = pair.getKey().toString();

				    long initiatedQty = _parser.getSystemAmount(pair.getValue().toString());
				    long expectedPostBalance = Long.parseLong(TransactionVO.get_fromUserPreBalances().get(initiatedProduct)) - initiatedQty;
				    senderExpectedPostBalances.put(initiatedProduct, Long.toString(expectedPostBalance));
				    it.remove();
				}
					
				Log.info(mapToString(TransactionVO, TransactionVO.get_fromUserPreBalances(), senderExpectedPostBalances, TransactionVO.get_fromUserPostBalances(), "Sender Balance Validation"));
					
				if (senderExpectedPostBalances.equals(TransactionVO.get_fromUserPostBalances()))
					ExtentI.Markup(ExtentColor.GREEN, "Sender Balances Validated Successfully! Post Balances Matched with expected Balances.");
				else {
					ExtentI.Markup(ExtentColor.RED, "Sender Balances Validated Successfully! Post Balances Mis-match found with expected Balances.");
					Log.failNode("----- Failure -----");
				}		
		}
		
		/*
		 * Transaction Type: FOC
		 * 	-	Get Number of Initiated Products & their respective quantities.
		 * 	-	User Balance expected to be (PreBalance + FOC Initiate Quantity)
		 */
		else if(TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("FOCCode"))) {
				receiverExpectedPostBalances = (HashMap<String, String>) TransactionVO.get_toUserPreBalances().clone();
				
				Iterator it = initiatedQuantities.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry)it.next();
				    String initiatedProduct = pair.getKey().toString();

				    long initiatedQty = _parser.getSystemAmount(pair.getValue().toString());

					/* --- Added by krishan.chawla on 31/12/18 to control NullPointerException when there is no record for user balance in user_balances table --- */
					long expectedPostBalance;
					if (TransactionVO.get_toUserPreBalances().get(initiatedProduct) != null)
						expectedPostBalance = Long.parseLong(TransactionVO.get_toUserPreBalances().get(initiatedProduct)) + initiatedQty;
					else
						expectedPostBalance = initiatedQty;

					receiverExpectedPostBalances.put(initiatedProduct, Long.toString(expectedPostBalance));
				    it.remove();
				}
					
				Log.info(mapToString(TransactionVO, TransactionVO.get_toUserPreBalances(), receiverExpectedPostBalances, TransactionVO.get_toUserPostBalances(), "Receiver Balance Validation"));
					
				if (receiverExpectedPostBalances.equals(TransactionVO.get_toUserPostBalances()))
					ExtentI.Markup(ExtentColor.GREEN, "Receiver Balances Validated Successfully! Post Balances Matched with expected Balances.");
				else {
					ExtentI.Markup(ExtentColor.RED, "Receiver Balances Validated Successfully! Post Balances Mis-match found with expected Balances.");
					Log.failNode("----- Failure -----");
				}		
		}
		
		/*
		 * Transaction Type: C2C Transfer
		 * Validate From User (Sender) PreBalance as PreBalance - Sender Debit Quantity - Fetched from Commission Calculation Logic.
		 * Validate To User (Receiver) PreBalance as PreBalance + Receiver Credit Quantity - Fetched from Commission Calculation Logic.
		 */
		else if (TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("C2CTransferCode"))) {
				senderExpectedPostBalances = (HashMap<String, String>) TransactionVO.get_fromUserPreBalances().clone();
				receiverExpectedPostBalances = (HashMap<String, String>) TransactionVO.get_toUserPreBalances().clone();
				
				Iterator it = initiatedQuantities.entrySet().iterator();
				   while (it.hasNext()) {
					   Map.Entry pair = (Map.Entry)it.next();
				       String initiatedProduct = pair.getKey().toString();
				       long expectedSenderPostBalance = Long.parseLong(TransactionVO.get_fromUserPreBalances().get(initiatedProduct)) - TransactionVO.getCommissionVO().get(initiatedProduct).getSenderDebitQty();
				       
				       /* --- Added by lokesh.kontey on 15/05/2019 to control NullPointerException when there is no record for user balance in user_balances table for receiver user --- */
				       long expectedReceiverPostBalance;
				       if (TransactionVO.get_toUserPreBalances().get(initiatedProduct) != null)
							expectedReceiverPostBalance = Long.parseLong(TransactionVO.get_toUserPreBalances().get(initiatedProduct)) + TransactionVO.getCommissionVO().get(initiatedProduct).getReceiverCreditQty();
						else
							expectedReceiverPostBalance = TransactionVO.getCommissionVO().get(initiatedProduct).getReceiverCreditQty();
				       
				       senderExpectedPostBalances.put(initiatedProduct, Long.toString(expectedSenderPostBalance));
				       receiverExpectedPostBalances.put(initiatedProduct, Long.toString(expectedReceiverPostBalance));
				       it.remove();
				   }
			
			Log.info(mapToString(TransactionVO, TransactionVO.get_fromUserPreBalances(), senderExpectedPostBalances, TransactionVO.get_fromUserPostBalances(), "Sender Balance Validation"));
			if (senderExpectedPostBalances.equals(TransactionVO.get_fromUserPostBalances()))
				ExtentI.Markup(ExtentColor.GREEN, "Sender Balances Validated Successfully! Post Balances Matched with expected Balances.");
			else {
				ExtentI.Markup(ExtentColor.RED, "Sender Balances Validated Successfully! Post Balances Mis-match found with expected Balances.");
				Log.failNode("----- Failure -----");
			}
			
			Log.info(mapToString(TransactionVO, TransactionVO.get_toUserPreBalances(), receiverExpectedPostBalances, TransactionVO.get_toUserPostBalances(), "Receiver Balance Validation"));
			if (receiverExpectedPostBalances.equals(TransactionVO.get_toUserPostBalances()))
				ExtentI.Markup(ExtentColor.GREEN, "Receiver Balances Validated Successfully! Post Balances Matched with expected Balances.");
			else {
				ExtentI.Markup(ExtentColor.RED, "Receiver Balances Validated Successfully! Post Balances Mis-match found with expected Balances.");
				Log.failNode("----- Failure -----");
			}
		}
		
		/*
		 * Transaction Type: C2C Withdraw & C2C Return
		 * Validate From User (Sender) PreBalance as PreBalance - Sender Debit Quantity - Initiated Withdraw / Return Quantity.
		 * Validate To User (Receiver) PreBalance as PreBalance + Sender Debit Quantity - Initiated Withdraw / Return Quantity.
		 */
		else if (TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("C2CWithdrawCode")) || TransactionVO.get_txntype().equalsIgnoreCase(_masterVO.getProperty("C2CReturnCode"))) {
				senderExpectedPostBalances = (HashMap<String, String>) TransactionVO.get_fromUserPreBalances().clone();
				receiverExpectedPostBalances = (HashMap<String, String>) TransactionVO.get_toUserPreBalances().clone();
				
				Iterator it = initiatedQuantities.entrySet().iterator();
				   while (it.hasNext()) {
					   Map.Entry pair = (Map.Entry)it.next();
				       String initiatedProduct = pair.getKey().toString();
				       long expectedSenderPostBalance = Long.parseLong(TransactionVO.get_fromUserPreBalances().get(initiatedProduct)) - _parser.getSystemAmount(initiatedQuantities.get(initiatedProduct));
				       long expectedReceiverPostBalance = Long.parseLong(TransactionVO.get_toUserPreBalances().get(initiatedProduct)) + _parser.getSystemAmount(initiatedQuantities.get(initiatedProduct));
				       senderExpectedPostBalances.put(initiatedProduct, Long.toString(expectedSenderPostBalance));
				       receiverExpectedPostBalances.put(initiatedProduct, Long.toString(expectedReceiverPostBalance));
				       it.remove();
				   }
			
			Log.info(mapToString(TransactionVO, TransactionVO.get_fromUserPreBalances(), senderExpectedPostBalances, TransactionVO.get_fromUserPostBalances(), "Sender Balance Validation"));
			if (senderExpectedPostBalances.equals(TransactionVO.get_fromUserPostBalances()))
				ExtentI.Markup(ExtentColor.GREEN, "Sender Balances Validated Successfully! Post Balances Matched with expected Balances.");
			else {
				ExtentI.Markup(ExtentColor.RED, "Sender Balances Validated Successfully! Post Balances Mis-match found with expected Balances.");
				Log.failNode("----- Failure -----");
			}
			
			Log.info(mapToString(TransactionVO, TransactionVO.get_toUserPreBalances(), receiverExpectedPostBalances, TransactionVO.get_toUserPostBalances(), "Receiver Balance Validation"));
			if (receiverExpectedPostBalances.equals(TransactionVO.get_toUserPostBalances()))
				ExtentI.Markup(ExtentColor.GREEN, "Receiver Balances Validated Successfully! Post Balances Matched with expected Balances.");
			else {
				ExtentI.Markup(ExtentColor.RED, "Receiver Balances Validated Successfully! Post Balances Mis-match found with expected Balances.");
				Log.failNode("----- Failure -----");
			}
		}

	}
	
	
	public static String mapToString(TransactionVO TransactionVO, Map<String, String> PreBalances, Map<String, String> ExpectedBalances, Map<String, String> PostBalances, String header) {
		   StringBuilder logBuilder = new StringBuilder();
		   logBuilder.append("<pre><center><b>"+ header +"</b></center><br>");
		   logBuilder.append("<table width=\"80%\"><tr><th width=\"40%\">Product</th><th width=\"20%\">PreBalance</th><th width=\"15%\">Commission</th><th width=\"20%\">Expected Balance</th><th width=\"20%\">PostBalance</th><th width=\"15%\">Status</th></tr>");
		   for (String key : PreBalances.keySet()) {
			logBuilder.append("<tr>");
			logBuilder.append("<td>" + key + "</td>");
			logBuilder.append("<td>" + _parser.getDisplayAmount(Long.parseLong(PreBalances.get(key))) + "</td>");
			try {
				logBuilder.append("<td>" + _parser.getDisplayAmount(TransactionVO.getCommissionVO().get(key).getCommissionQty()) + "</td>");
			} catch (NullPointerException e) {
				logBuilder.append("<td>N/A</td>");
			}
			logBuilder.append("<td>" + _parser.getDisplayAmount(Long.parseLong(ExpectedBalances.get(key))) + "</td>");
			logBuilder.append("<td>" + _parser.getDisplayAmount(Long.parseLong(PostBalances.get(key))) + "</td>");
			if (ExpectedBalances.get(key).equalsIgnoreCase(PostBalances.get(key)))
				logBuilder.append("<td><font color=\"green\">Pass</font></td>");
			else
				logBuilder.append("<td><font color=\"red\">Fail</font></td>");
			logBuilder.append("</tr>");
		   }
		   logBuilder.append("</table></pre>");
		   return logBuilder.toString();
	}
	
	private static String evaluateAutoNetworkStock(TransactionVO TransactionVO, String walletType, String initiatedProduct, long expectedPostBalance) {
		final String methodname = "evaluateAutoNetworkStock";
		Log.info("Entered " + methodname + " :: Evaluating Auto Network Stock Feature.");
		String autoNetworkStockStatus = TransactionVO.getautoNetworkStockSystemStatus();
		if (!BTSLUtil.isNullString(autoNetworkStockStatus) && autoNetworkStockStatus.equalsIgnoreCase("true")) {
			HashMap<String, Object[]> autoNetworkStockThresholds = TransactionVO.getAutoNetworkStockThresholds(walletType);
			String productAutoNetworkThreshold = "";
			if (!autoNetworkStockThresholds.isEmpty()) {
				if(autoNetworkStockThresholds.containsKey(initiatedProduct))
					productAutoNetworkThreshold = autoNetworkStockThresholds.get(initiatedProduct)[0].toString();
			}
			
			if (!BTSLUtil.isNullString(productAutoNetworkThreshold)) {
				if (expectedPostBalance < _parser.getSystemAmount(productAutoNetworkThreshold)) {
					ExtentI.Markup(ExtentColor.GREY, "Auto Network Stock feature should be applicable for " + initiatedProduct + "as the Post Balance reaches under " + autoNetworkStockThresholds.get(initiatedProduct)[0].toString() + " alerting value.");
					return String.valueOf(expectedPostBalance + _parser.getSystemAmount(autoNetworkStockThresholds.get(initiatedProduct)[1].toString()));
				} else {
					ExtentI.Markup(ExtentColor.GREY, "Auto Network Stock feature should not be applicable for " + initiatedProduct + "as the Post Balance remains above " + autoNetworkStockThresholds.get(initiatedProduct)[0].toString() + " alerting value.");
					return String.valueOf(expectedPostBalance); 
				}
			}
			
			/*Iterator it = expectedWalletBalances.entrySet().iterator();
			   while (it.hasNext()) {
				   Map.Entry pair = (Map.Entry)it.next();
				   String initiatedProduct = pair.getKey().toString();
				   
				   String thresholdLimit = (String) autoNetworkStockThresholds.get(initiatedProduct)[0];
				   if(!BTSLUtil.isNullString(thresholdLimit)) {
				       long expectedPostBalance = Long.parseLong(expectedWalletBalances.get(initiatedProduct));				       
				       if (expectedPostBalance < _parser.getSystemAmount(thresholdLimit)) {
				    	   long expectedPostBalanceAfterAutoNetworkStock = expectedPostBalance + _parser.getSystemAmount(autoNetworkStockThresholds.get(initiatedProduct)[1].toString());
				    	   expectedWalletBalances.put(initiatedProduct, Long.toString(expectedPostBalanceAfterAutoNetworkStock));
				       }
				   }
			   }*/
		} else {
			ExtentI.Markup(ExtentColor.GREY, "Auto Network Stock feature :: False");
			return String.valueOf(expectedPostBalance);
		}
		return String.valueOf(expectedPostBalance);
	}
	
}
