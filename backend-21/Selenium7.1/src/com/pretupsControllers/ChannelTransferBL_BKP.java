package com.pretupsControllers;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.commissionprofile.ChannelTransfersVO;
import com.pretupsControllers.commissionprofile.userOTFCountsVO;
import com.utils._masterVO;
import com.utils._parser;

public class ChannelTransferBL_BKP {

	public static long productCost;
	public static long value = 0;
	
	/**
	 * 
	 * APPLICABLE FOR 7.0 AND ABOVE
	 * 
	 * Method calculateMRPWithTaxAndDiscount()
	 * To calculate the taxes on the selected product list
	 * 
	 * @param p_transferItemsList
	 * @param p_txnType
	 *            String
	 * @throws SQLException 
	 * @throws ParseException 
	 * @throws Exception
	 */
	public static void calculateMRPWithTaxAndDiscount(String ReceiverMSISDN, String p_txnType, String ProductCode, String requestedQuantity) throws SQLException, ParseException {
		Boolean order= true;

		Boolean addnl=false;
		DateFormat df = new SimpleDateFormat("dd/MM/yy");
		Date dateobj = new Date();
		Date tempDate = df.parse(df.format(dateobj));
		
			ChannelTransfersVO ChannelTransfersVO = new ChannelTransfersVO();
			userOTFCountsVO userOTFCountsVO = new userOTFCountsVO();
			List<ChannelTransfersVO> otfSlabList;
			ChannelTransfersVO otfDetailsVO = null;
			ChannelTransfersVO.loadChannelTransfersDAO(ReceiverMSISDN, p_txnType, ProductCode, requestedQuantity);
			
			productCost = (long) (Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()) * ChannelTransfersVO.getUnitValue());  //.getRequestedQuantity for User Input Quantity / Products table for Unit value  

			if (!p_txnType.equals(_masterVO.getProperty("FOCCode")) /*&& channelTransferVO.isOtfFlag()*/) // channelTransferVO.isOtfFlag() can be ignored - Transfer type from Constant.properties
			{
				if((p_txnType.equals(_masterVO.getProperty("O2CTransferCode")) && !ChannelTransfersVO.isWeb()) || !p_txnType.equals(_masterVO.getProperty("O2CTransferCode")) ) // Transfer Type from Constant.properties - != WEB needs to be discussed.
				{
/*					String TargetBasedOTF = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getProperty("Network Code"), SystemPreferences.TARGET_BASED_BASE_COMMISSION);
					if(TargetBasedOTF.equalsIgnoreCase("true")) // preference Validation
					{
						ChannelTransfersVO.loadCommissionProfileDetailsForOTF();
						//commissionProfileDeatilsVO = commissionProfileTxnDAO.loadCommissionProfileDetailsForOTF(con, channelTransferItemsVO.getCommProfileDetailID()); //Commission Profile details
						userOTFCountsVO.loadUserOTFCounts(ChannelTransfersVO.getUserID(),ChannelTransfersVO.getBaseCommProfileDetailID(), addnl); // User OTF Counts table on basis of User ID & 'comm' for Commission Type

						boolean timeFlag=true;


						if(!(ChannelTransfersVO.getOtfApplicableFrom()== null && ChannelTransfersVO.getOtfApplicableTo()==null)) // To be fetched from Commission Profile details
						{
							if(!((ChannelTransfersVO.getOtfApplicableFrom().before(tempDate) 
									|| ChannelTransfersVO.getOtfApplicableFrom().equals(tempDate)) && (ChannelTransfersVO.getOtfApplicableTo().after(tempDate) // To be fetched from Commission Profile details
											|| ChannelTransfersVO.getOtfApplicableTo().equals(tempDate))))
							{
								timeFlag = false;
							}
						}

						if(ChannelTransfersVO.getOtfTimeSlab()!=null)
						{
							if(!BTSLUtil.timeRangeValidation(ChannelTransfersVO.getOtfTimeSlab(), new Date())) // BTSL file
							{
								timeFlag=false;
							}
						}
						if(timeFlag)
						{

							otfSlabList= ChannelTransfersVO.getBaseCommOtfDetails(ChannelTransfersVO.getBaseCommProfileDetailID(), order);

							for (ChannelTransfersVO commissionProfileOTFSlabVO : otfSlabList)
							{

								if(userOTFCountsVO!=null)
								{
									if (userOTFCountsVO.getOtfValue() >= commissionProfileOTFSlabVO.getOtfValue())
									{
										otfDetailsVO =  commissionProfileOTFSlabVO;
									}
								}
								else
								{
									if (Long.parseLong(requestedQuantity) >= commissionProfileOTFSlabVO.getOtfValue())
									{
										otfDetailsVO = commissionProfileOTFSlabVO;
									}
								}
							}


							if(otfDetailsVO!=null)
							{

								ChannelTransfersVO.setBaseCommProfileOTFDetailID(otfDetailsVO.getBaseCommProfileOTFDetailID());
								ChannelTransfersVO.setBaseCommProfileDetailID(otfDetailsVO.getBaseCommProfileDetailID());
								ChannelTransfersVO.setOtfValue(otfDetailsVO.getOtfValue());
								ChannelTransfersVO.setOtfTypePctOrAMt(otfDetailsVO.getOtfTypePctOrAMt());
								ChannelTransfersVO.setOtfRate(otfDetailsVO.getOtfRate());
								calculateOTFforOPT(channelTransferItemsVO, commissionProfileDeatilsVO);
								otfDetailsVO = null;

							}

							if(channelTransferItemsVO.getOtfAmount()!= 0)
							{

								channelUserVO.setCommissionProfileSetID(channelTransferVO.getCommProfileSetId());
								channelUserVO.setUserID(channelTransferVO.getToUserID());
								channelUserVO.setNetworkID(channelTransferVO.getNetworkCode());
								channelUserVO.setCommissionProfileSetVersion(channelTransferVO.getCommProfileVersion());


							}

						}
					}*/
				}
			}

			// In case of FOC Commission will not be calculated
			if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
				value = calculatorI.calculateCommission(ChannelTransfersVO.getCommType(), ChannelTransfersVO.getCommRate(), productCost);

			}
			ChannelTransfersVO.setCommValue(value);
			value = 0;
			value = calculatorI.calculateDiscount(ChannelTransfersVO.getDiscountType(), ChannelTransfersVO.getDiscountRate(), productCost);
			ChannelTransfersVO.setDiscountValue(value);
			value = 0;
			
			/* To check whether tax is applicable for transaction or not.We have
			 * the following three type of the
			 * transaction and following corresponding check condition.
			 * 1. In case of O2C transfer Tax calculation is mandatory.
			 * 2. In case of C2C transfer Tax calculation is based on the
			 * TAX_APPLICABLE_ON_C2C flag of the
			 * commission profile.
			 * 3. In case of FOC transfer Tax calculation is based on the
			 * TAX_APPLICABLE_ON_FOC flag of the
			 * commission profile.
			 */
			if (p_txnType.equals(_masterVO.getProperty("O2CTransferCode")) || (p_txnType.equals(_masterVO.getProperty("C2CTransferCode")) && PretupsI.YES.equals(ChannelTransfersVO
					.getTaxOnChannelTransfer())) || (p_txnType.equals(_masterVO.getProperty("FOCCode")) && PretupsI.YES.equals(ChannelTransfersVO.getTaxOnFOCTransfer()))) {
				value = calculatorI.calculateTax1(ChannelTransfersVO.getTax1Type(), ChannelTransfersVO.getTax1Rate(), productCost);
				ChannelTransfersVO.setTax1Value(value);
				value = 0;
				value = calculatorI.calculateTax2(ChannelTransfersVO.getTax2Type(), ChannelTransfersVO.getTax2Rate(), ChannelTransfersVO.getTax1Value());
				ChannelTransfersVO.setTax2Value(value);
				value = 0;
				// set commision value as 0 , becoz commision not calculated on
				// the commision in case pf withdraw and return.
/*				String POSITIVE_COMM_APPLY = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY");
				_masterVO.getProperty("");
				if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(channelTransferVO.getTransferSubType())) {
					channelTransferItemsVO.setCommValue(0);
				}*/
				// In case of FOC Tax3 will not be calculated
				if (!p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
					value = calculatorI.calculateTax3(ChannelTransfersVO.getTax3Type(), ChannelTransfersVO.getTax3Rate(), ChannelTransfersVO.getCommValue());
				}
				ChannelTransfersVO.setTax3Value(value);
				value = 0;
			}
			value = calculatorI.calculateCommissionQuantity(ChannelTransfersVO.getCommValue(), ChannelTransfersVO.getUnitValue(), ChannelTransfersVO.getTax3Value());

/*			if (!p_txnType.equals(_masterVO.getProperty("FOCCode")))
			{
				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
				{ 
					value= value+ channelTransferItemsVO.getOtfAmount();

				}
			}*/

			ChannelTransfersVO.setCommQuantity(value);
			long payableAmount = 0;
			long netPayableAmount = 0;
			String POSITIVE_COMM_APPLY = DBHandler.AccessHandler.getSystemPreference("POSITIVE_COMM_APPLY");
/*			if (!p_txnType.equals(_masterVO.getProperty("FOCCode")) && POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
					.equalsIgnoreCase(channelTransferVO.getTransferSubType())) {
				channelTransferItemsVO.setSenderDebitQty(PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()));
				channelTransferItemsVO.setReceiverCreditQty(channelTransferItemsVO.getRequiredQuantity());
				channelTransferItemsVO.setProductTotalMRP(productCost);
				channelTransferItemsVO.setPayableAmount(productCost);
				channelTransferItemsVO.setNetPayableAmount(productCost);
			}// this is executed in case of Transfer and POSITIVE_COMM_APPLY is
*/			// true
			if (POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode")) && _masterVO.getProperty("O2CTransferCode").equalsIgnoreCase(p_txnType)) {
				value = 0;
				value = calculatorI.calculateReceiverCreditQuantity(ChannelTransfersVO.getRequestedQuantity(), ChannelTransfersVO.getUnitValue(),
						ChannelTransfersVO.getCommQuantity());
				ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
				System.out.println("Sender Debit Quantity: " + _parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
				ChannelTransfersVO.setReceiverCreditQty(value);
				System.out.println("Receiver Credit Quantity: " + value);
				ChannelTransfersVO.setPayableAmount(productCost);
				System.out.println("Payable Amount: " + productCost);
				ChannelTransfersVO.setNetPayableAmount(productCost);
				System.out.println("Net Payable Amount: " + productCost);
				final int multiplicationFactor = Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("AMOUNT_MULT_FACTOR"));
				ChannelTransfersVO.setProductTotalMRP(ChannelTransfersVO.getReceiverCreditQty() * ChannelTransfersVO.getUnitValue() / multiplicationFactor);
			}// executed in case of FOC or in case of POSITIVE_COMM_APPLY value
			// is false
			else if (!POSITIVE_COMM_APPLY.equalsIgnoreCase("true") && !p_txnType.equals(_masterVO.getProperty("FOCCode"))) {
/*				if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
				{ 
					payableAmount = calculatorI.calculatePayableAmount(channelTransferItemsVO.getUnitValue(), Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()),
							channelTransferItemsVO.getCommValue() + channelTransferItemsVO.getOtfAmount(), channelTransferItemsVO.getDiscountValue());
				}
				else
				{*/
					payableAmount = calculatorI.calculatePayableAmount(ChannelTransfersVO.getUnitValue(), Double.parseDouble(ChannelTransfersVO.getRequestedQuantity()),
							ChannelTransfersVO.getCommValue(), ChannelTransfersVO.getDiscountValue());
				//}
				netPayableAmount = calculatorI.calculateNetPayableAmount(payableAmount, ChannelTransfersVO.getTax3Value());
				ChannelTransfersVO.setPayableAmount(payableAmount);
				System.out.println("Payable Amount: " + _parser.getDisplayAmount(payableAmount));
				ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
				System.out.println("Net Payable Amount: " + _parser.getDisplayAmount(netPayableAmount));
				ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
				System.out.println("Sender Debit Quantity: " + _parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
				ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
				System.out.println("Receiver Credit Quantity: " + _parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
				ChannelTransfersVO.setProductTotalMRP(productCost);
			} else {
				ChannelTransfersVO.setSenderDebitQty(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
				System.out.println("Sender Debit Quantity: " + _parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
				ChannelTransfersVO.setReceiverCreditQty(ChannelTransfersVO.getRequiredQuantity());
				System.out.println("Receiver Credit Quantity: " + ChannelTransfersVO.getRequiredQuantity());
				ChannelTransfersVO.setProductTotalMRP(productCost);
				System.out.println("Product Total MRP: " + productCost);
				ChannelTransfersVO.setPayableAmount(payableAmount);
				System.out.println("Payable Amount: " + payableAmount);
				ChannelTransfersVO.setNetPayableAmount(netPayableAmount);
				System.out.println("Net Payable Amount: " + netPayableAmount);
			}
/*			ChannelTransfersVO.setApprovedQuantity(_parser.getSystemAmount(ChannelTransfersVO.getRequestedQuantity()));
		
		if(!(ChannelTransfersVO.isWeb() && p_txnType.equals(_masterVO.getProperty("C2CTransferCode"))))
		{
			if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelTransferVO.getNetworkCode()))
			{
				List<UserOTFCountsVO> userOtfCountsList;
				userOtfCountsList=userTransferCountsDAO.loadUserOTFCountsList(con, channelTransferVO.getToUserID(),commissionProfileDeatilsVO.getBaseCommProfileDetailID(), addnl);

				if(channelTransferItemsVO.getOtfAmount() !=0 && channelTransferVO.isTargetAchieved())
				{

					//Message handling for OTF
					TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
					tbcm.loadBaseCommissionProfileDetailsForTargetMessages(con,channelUserVO);
				}
			} 
		}*/

	}
}
