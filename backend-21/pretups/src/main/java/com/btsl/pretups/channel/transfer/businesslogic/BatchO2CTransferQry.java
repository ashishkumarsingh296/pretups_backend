package com.btsl.pretups.channel.transfer.businesslogic;

public interface BatchO2CTransferQry {

	/**
	 * @param itemStatus
	 * @param currentLevel
	 * @return
	 */
	String loadBatchO2CMasterDetailsQry(String[] itemStatus, String currentLevel);

	/**
	 * @param batchid
	 * @param msisdn
	 * @param goeDomain
	 * @param domain
	 * @param productCode
	 * @return
	 */
	String loadBatchO2CMasterDetailsQry(String batchid,String msisdn,String[] goeDomain, String[] domain, String[] productCode);

	/**
	 * @param itemStatus
	 * @return
	 */
	String loadBatchItemsMapQry(String itemStatus);

	/**
	 * @return
	 */
	String processO2CWithdrawByBatchSelectItemsDetails();

	/**
	 * @return
	 */
	String closeO2CWithdrawByBatchLoadNetworkStockQry();

	/**
	 * @return
	 */
	String closeO2CWithdrawByBatchSelectNetworkStockQry();

	/**
	 * @return
	 */
	String closeO2CWithdrawByBatchSelectUserBalances();

	/**
	 * @return
	 */
	String closeO2CWithdrawByBatchSelectItemsDetails();

	/**
	 * @return
	 */
	String loadBatchDetailsListQry();

	/**
	 *
	 * @param currentLevel
	 * @param itemStatus
	 * @param categoryCode
	 * @param geoDomain
	 * @param domainCode
	 * @return
	 */
	String loadO2CTransferApprovalListQry(String currentLevel, String itemStatus, String categoryCode, String geoDomain, String domainCode);

	/**
	 *
	 * @param approvalLevel
	 * @param statusUsed
	 * @param categoryCode
	 * @param geoDomain
	 * @param domainCode
	 * @return
	 */
	String loadO2CWithdrawal_FOCApprovalListQry(String approvalLevel, String statusUsed, String categoryCode,
			String geoDomain, String domainCode);

			/**
				 *
				 * @param mItemStatus
				 * @param approvalLevel
				 * @return
				 */
			String loadBatchTransferApprovalDetails(String itemstatus,String approvallevel);

				/**
				 *
				 * @param mItemStatus
				 * @param approvalLevel
				 * @return
				 */
				String loadBatchWithdrawalorFOCApprovalDetails(String mItemStatus, String approvalLevel);

				/**
				 *
				 * @param itemStatus
				 * @return
				 */
				String loadBatchO2CItemsMapQry(String itemStatus);

				/**
				 *
				 * @param itemStatus
				 * @return
				 */
				String loadBatchO2CItemsMapQryFOCorWithdrawal(String itemStatus);

}
