package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
/*
 *  * Interface which provides base for ChannelUserReportServiceImpl class
 *   * also declares different method for ChannelUserReportService
 *    */

public interface ChannelUserReportService {

	/*
	 * Method loadCategory to load the receiver's category
	 * 
	 * @param domain
	 * 
	 * @param networkId
	 * 
	 * @throws BTSLBaseException,Exception
	 * 
	 * @returns ArrayList
	 */
	public List loadCategory() throws Exception; // load category

	/*
	 * Method loadDomain to load the domain
	 * 
	 * @throws BTSLBaseException,Exception
	 * 
	 * @returns List<ListValueVO>
	 */

	/*
	 * Method loadDomain to load the domain
	 * 
	 * @throws BTSLBaseException,Exception
	 * 
	 * @returns List<ListValueVO>
	 */
	public List<ListValueVO> loadDomain() throws Exception;

	public List<ListValueVO> loadTxnSubType() throws Exception;

	public List<ListValueVO> loadTransfercategory() throws Exception;

	public List<ListValueVO> loadUserData(String domain, String category, String geography, String user,
			String networkId, String domID) throws BTSLBaseException, IOException;

	public <T> void processListValueVOValue(List<T> listObject, String type) throws Exception;

}